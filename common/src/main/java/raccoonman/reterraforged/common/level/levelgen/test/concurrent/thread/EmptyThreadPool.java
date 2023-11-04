package raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.Batcher;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.SyncBatcher;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;

public class EmptyThreadPool implements ThreadPool {
    private final ThreadLocal<SyncBatcher> batcher;
    
    public EmptyThreadPool() {
        this.batcher = ThreadLocal.withInitial(SyncBatcher::new);
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    @Override
    public Future<?> submit(final Runnable runnable) {
        return LazyCallable.adapt(runnable);
    }
    
    @Override
    public <T> Future<T> submit(final Callable<T> callable) {
        return LazyCallable.adaptComplete(callable);
    }
    
    @Override
    public void shutdown() {
        ThreadPools.markShutdown(this);
    }
    
    @Override
    public void shutdownNow() {
    }
    
    @Override
    public Resource<Batcher> batcher() {
        return this.batcher.get();
    }
}
