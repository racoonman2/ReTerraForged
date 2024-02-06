package raccoonman.reterraforged.concurrent.cache;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.IntFunction;
import java.util.function.LongFunction;

import raccoonman.reterraforged.concurrent.cache.map.LongMap;

public class Cache<V extends ExpiringEntry> implements AutoCloseable {
	public static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor((r) -> {
		Thread thread = new Thread(r);
		thread.setName("CacheScheduler");
		return thread;
	});
	
    private LongMap<V> map;
    private long lifetimeMS;
    private volatile long timeout;
    private ScheduledFuture<?> poll;
    
    public Cache(int capacity, long expireTime, long pollInterval, TimeUnit unit, IntFunction<LongMap<V>> mapFunc) {
        this.timeout = 0L;
        this.map = mapFunc.apply(capacity);
        this.lifetimeMS = unit.toMillis(expireTime);
        
        long intervalMillis = unit.toMillis(pollInterval);
        this.poll = SCHEDULER.scheduleAtFixedRate(this::poll, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
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
    
    public void poll() {
        this.timeout = System.currentTimeMillis() - this.lifetimeMS;
        this.map.removeIf((entry) -> entry.getTimestamp() < this.timeout);
    }

	@Override
	public void close() {
		this.poll.cancel(false);
	}
}
