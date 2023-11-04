package raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread;

import java.util.concurrent.ThreadFactory;

public record SimpleThreadFactory(String name) implements ThreadFactory {
    
    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread = new Thread(r);
        thread.setName(this.name);
        return thread;
    }
}
