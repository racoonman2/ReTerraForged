package raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.map.LongMap;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.map.StampedBoundLongMap;

public class Cache<V extends ExpiringEntry> implements Runnable, Predicate<V> {
    private String name;
    private LongMap<V> map;
    private long lifetimeMS;
    private volatile long timeout;
    
    public Cache(String name, long expireTime, long interval, TimeUnit unit) {
        this(name, 256, expireTime, interval, unit);
    }
    
    public Cache(String name, int capacity, long expireTime, long interval, TimeUnit unit) {
        this(name, capacity, expireTime, interval, unit, StampedBoundLongMap::new);
    }
    
    public Cache(String name, int capacity, long expireTime, long interval, TimeUnit unit, IntFunction<LongMap<V>> mapFunc) {
        this.timeout = 0L;
        this.name = name;
        this.map = mapFunc.apply(capacity);
        this.lifetimeMS = unit.toMillis(expireTime);
        CacheManager.get().schedule(this, unit.toMillis(interval));
    }
    
    public String getName() {
        return this.name;
    }
    
    public void remove(long key) {
        this.map.remove(key, ExpiringEntry::close);
    }
    
    public V get(long key) {
        return this.map.get(key);
    }
    
    public V computeIfAbsent(long key, LongFunction<V> func) {
        return this.map.computeIfAbsent(key, func);
    }
    
    public <T> T map(long key, LongFunction<V> func, Function<V, T> mapper) {
        return this.map.map(key, func, mapper);
    }
    
    @Override
    public void run() {
        this.timeout = System.currentTimeMillis() - this.lifetimeMS;
        this.map.removeIf(this);
    }
    
    @Override
    public boolean test(V v) {
        return v.getTimestamp() < this.timeout;
    }
}
