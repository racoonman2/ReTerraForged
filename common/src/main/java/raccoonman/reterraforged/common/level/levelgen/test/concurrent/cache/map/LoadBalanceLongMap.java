package raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.map;

import it.unimi.dsi.fastutil.HashCommon;
import java.util.function.LongFunction;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.function.Predicate;
import java.util.function.Consumer;
import java.util.concurrent.locks.StampedLock;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;

public class LoadBalanceLongMap<T> implements LongMap<T> {
    private int mask;
    private int sectionCapacity;
    private Long2ObjectLinkedOpenHashMap<T>[] maps;
    private StampedLock[] locks;
    
    @SuppressWarnings("unchecked")
	public LoadBalanceLongMap(int factor, int size) {
        factor = getNearestFactor(factor);
        size = getSectionSize(size, factor);
        this.mask = factor - 1;
        this.sectionCapacity = size - 2;
        this.maps = new Long2ObjectLinkedOpenHashMap[factor];
        this.locks = new StampedLock[factor];
        for (int i = 0; i < factor; ++i) {
            this.maps[i] = new Long2ObjectLinkedOpenHashMap<>(size);
            this.locks[i] = new StampedLock();
        }
    }
    
    @Override
    public int size() {
        int size = 0;
        for (int i = 0; i < this.locks.length; ++i) {
            StampedLock lock = this.locks[i];
            long stamp = lock.readLock();
            try {
                size += this.maps[i].size();
            } finally {
                lock.unlockRead(stamp);
            }
        }
        return size;
    }
    
    @Override
    public void clear() {
        for (int i = 0; i < this.locks.length; ++i) {
            StampedLock lock = this.locks[i];
            long stamp = lock.writeLock();
            try {
                this.maps[i].clear();
            } finally {
                lock.unlockWrite(stamp);
            }
        }
    }
    
    @Override
    public void remove(long key) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        long stamp = lock.writeLock();
        try {
            this.maps[index].remove(key);
        } finally {
            lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public void remove(long key, Consumer<T> consumer) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        long stamp = lock.writeLock();
        try {
            this.maps[index].remove(key, (Object)consumer);
        } finally {
            lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public int removeIf(Predicate<T> predicate) {
        int count = 0;
        for (int i = 0; i < this.locks.length; ++i) {
            StampedLock lock = this.locks[i];
            Long2ObjectLinkedOpenHashMap<T> map = this.maps[i];
            long stamp = lock.writeLock();
            try {
                int startSize = map.size();
                ObjectIterator<Long2ObjectMap.Entry<T>> iterator = map.long2ObjectEntrySet().fastIterator();
                while (iterator.hasNext()) {
                    Long2ObjectMap.Entry<T> entry = iterator.next();
                    if (predicate.test(entry.getValue())) {
                        iterator.remove();
                    }
                }
                count += startSize - map.size();
            } finally {
                lock.unlockWrite(stamp);
            }
        }
        return count;
    }
    
    @Override
    public void put(long key, T value) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        Long2ObjectLinkedOpenHashMap<T> map = this.maps[index];
        long stamp = lock.writeLock();
        try {
            if (map.size() > this.sectionCapacity) {
                map.removeFirst();
            }
            map.put(key, value);
        } finally {
            lock.unlockWrite(stamp);
        }
    }
    
    @Override
    public T get(long key) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        long stamp = lock.readLock();
        try {
            return this.maps[index].get(key);
        } finally {
            lock.unlockRead(stamp);
        }
    }
    
    @Override
    public T computeIfAbsent(long key, LongFunction<T> factory) {
        int index = this.getIndex(key);
        StampedLock lock = this.locks[index];
        Long2ObjectLinkedOpenHashMap<T> map = this.maps[index];
        long readStamp = lock.readLock();
        try {
            T t = map.get(key);
            if (t != null) {
                return t;
            }
        } finally {
            lock.unlockRead(readStamp);
        }
        long writeStamp = lock.writeLock();
        try {
            if (map.size() > this.sectionCapacity) {
                map.removeFirst();
            }
            return map.computeIfAbsent(key, factory);
        } finally {
            lock.unlockWrite(writeStamp);
        }
    }
    
    private int getIndex(long key) {
        return HashCommon.long2int(key) & this.mask;
    }
    
    private static int getSectionSize(int size, int factor) {
        int section = size / factor;
        if (section * factor < size) {
            ++section;
        }
        return section;
    }
    
    private static int getNearestFactor(int i) {
        int j;
        for (j = 0; i != 0; i >>= 1, ++j) {}
        return j;
    }
}
