package raccoonman.reterraforged.concurrent.task;

import java.util.function.Function;
import java.util.function.Supplier;

public class LazySupplier<T> extends LazyCallable<T> {
    private Supplier<T> supplier;
    
    public LazySupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    protected T create() {
        return this.supplier.get();
    }
    
    public <V> LazySupplier<V> then(Function<T, V> mapper) {
        return supplied(this, mapper);
    }
    
    public static <T> LazySupplier<T> of(Supplier<T> supplier) {
        return new LazySupplier<T>(supplier);
    }
    
    public static <V, T> LazySupplier<T> factory(V value, Function<V, T> function) {
        return of(() -> function.apply(value));
    }
    
    public static <V, T> LazySupplier<T> supplied(Supplier<V> supplier, Function<V, T> function) {
        return of(() -> function.apply(supplier.get()));
    }
}
