package raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.SafeCloseable;

public interface Batcher extends SafeCloseable {
    void size(int size);
    
    void submit(Runnable task);
    
    default void submit(BatchTask task) {
        this.submit((Runnable) task);
    }
}
