package raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPools;

public class CacheManager {
    private static final CacheManager INSTANCE = new CacheManager();
    private List<ScheduledFuture<?>> cacheTasks;
    
    private CacheManager() {
        this.cacheTasks = new ArrayList<>();
    }
    
    public synchronized void schedule(Cache<?> cache, long intervalMS) {
        this.cacheTasks.add(ThreadPools.scheduleRepeat(cache, intervalMS));
    }
    
    public synchronized void clear() {
        if (this.cacheTasks.isEmpty()) {
            return;
        }
        for (ScheduledFuture<?> task : this.cacheTasks) {
            try {
                task.cancel(false);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        this.cacheTasks.clear();
    }
    
    public static CacheManager get() {
        return CacheManager.INSTANCE;
    }
}
