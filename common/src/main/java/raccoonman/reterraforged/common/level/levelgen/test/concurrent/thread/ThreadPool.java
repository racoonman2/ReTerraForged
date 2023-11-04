package raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.Batcher;

public interface ThreadPool {
    int size();
    
    void shutdown();
    
    void shutdownNow();
    
    default boolean isManaged() {
        return false;
    }
    
    Future<?> submit(Runnable task);
    
    <T> Future<T> submit(Callable<T> task);
    
    Resource<Batcher> batcher();
}
