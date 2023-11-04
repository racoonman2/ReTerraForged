// 
// Decompiled by Procyon v0.5.36
// 

package raccoonman.reterraforged.common.level.levelgen.test.concurrent.pool;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;

public class ThreadLocalPool<T> {
    private final int size;
    private final Supplier<T> factory;
    private final Consumer<T> cleaner;
    private final ThreadLocal<Pool<T>> local;
    
    public ThreadLocalPool(int size, Supplier<T> factory) {
        this(size, factory, t -> {});
    }
    
    public ThreadLocalPool(int size, Supplier<T> factory, Consumer<T> cleaner) {
        this.size = size;
        this.factory = factory;
        this.cleaner = cleaner;
        this.local = ThreadLocal.withInitial(this::createPool);
    }
    
    public Resource<T> get() {
        return this.local.get().retain();
    }
    
    private Pool<T> createPool() {
        return new Pool<>(this.size, this.factory, this.cleaner);
    }
    
    private static class Pool<T> {
        private int size;
        private Supplier<T> factory;
        private Consumer<T> cleaner;
        private List<Resource<T>> pool;
        private int index;
        
        private Pool(int size, Supplier<T> factory, Consumer<T> cleaner) {
            this.size = size;
            this.index = size - 1;
            this.factory = factory;
            this.cleaner = cleaner;
            this.pool = new ObjectArrayList<>(size);
            for (int i = 0; i < size; ++i) {
                this.pool.add(new PoolResource<>(factory.get(), this));
            }
        }
        
        private Resource<T> retain() {
            if (this.index > 0) {
                Resource<T> value = this.pool.remove(this.index);
                --this.index;
                return value;
            }
            return new PoolResource<>(this.factory.get(), this);
        }
        
        private void restore(Resource<T> resource) {
            if (this.index + 1 < this.size) {
                this.cleaner.accept(resource.get());
                this.pool.add(resource);
                ++this.index;
            }
        }
    }
    
    private static class PoolResource<T> implements Resource<T> {
        private T value;
        private Pool<T> pool;
        
        private PoolResource(T value, Pool<T> pool) {
            this.value = value;
            this.pool = pool;
        }
        
        @Override
        public T get() {
            return this.value;
        }
        
        @Override
        public boolean isOpen() {
            return true;
        }
        
        @Override
        public void close() {
            this.pool.restore(this);
        }
    }
}
