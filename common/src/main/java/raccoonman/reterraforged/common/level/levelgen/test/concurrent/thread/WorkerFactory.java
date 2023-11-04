package raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.ThreadFactory;

public record WorkerFactory(String prefix, ThreadGroup group, AtomicInteger threadNumber) implements ThreadFactory {

	public WorkerFactory(String name) {
		this(name + "-Worker-", Thread.currentThread().getThreadGroup(), new AtomicInteger(1));
	}

	@Override
	public Thread newThread(Runnable task) {
		Thread thread = new Thread(this.group, task);
		thread.setDaemon(true);
		thread.setName(this.prefix + this.threadNumber.getAndIncrement());
		return thread;
	}
}
