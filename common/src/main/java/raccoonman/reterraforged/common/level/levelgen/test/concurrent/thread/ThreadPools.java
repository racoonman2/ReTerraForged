package raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class ThreadPools {
	public static ThreadPool NONE = new EmptyThreadPool();
	private static final Object LOCK = new Object();
	private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor(new SimpleThreadFactory("TF-Scheduler"));
	private static WeakReference<ThreadPool> INSTANCE = new WeakReference<>(null);

	public static ThreadPool createDefault() {
		return create(defaultPoolSize());
	}

	public static ThreadPool create(int poolSize) {
		return create(poolSize, false);
	}

	public static ThreadPool create(int poolSize, boolean keepAlive) {
		synchronized (ThreadPools.LOCK) {
			ThreadPool current = ThreadPools.INSTANCE.get();
			if (current != null && current.isManaged()) {
				if (poolSize == current.size()) {
					return current;
				}
				current.shutdown();
			}
			ThreadPool next = BatchingThreadPool.of(poolSize, !keepAlive);
			if (next.isManaged()) {
				ThreadPools.INSTANCE = new WeakReference<>(next);
			}
			return next;
		}
	}

	public static int defaultPoolSize() {
		return Math.max(2, Runtime.getRuntime().availableProcessors());
	}

	public static void scheduleDelayed(Runnable runnable, long delayMS) {
		ThreadPools.SCHEDULER.schedule(runnable, delayMS, TimeUnit.MILLISECONDS);
	}

	public static ScheduledFuture<?> scheduleRepeat(Runnable runnable, long intervalMS) {
		return ThreadPools.SCHEDULER.scheduleAtFixedRate(runnable, intervalMS, intervalMS, TimeUnit.MILLISECONDS);
	}

	public static void markShutdown(ThreadPool threadPool) {
		synchronized (ThreadPools.LOCK) {
			if (threadPool == ThreadPools.INSTANCE.get()) {
				ThreadPools.INSTANCE.clear();
			}
		}
	}

	public static void shutdownAll() {
		ThreadPools.SCHEDULER.shutdownNow();
		synchronized (ThreadPools.LOCK) {
			ThreadPool pool = ThreadPools.INSTANCE.get();
			if (pool != null) {
				pool.shutdown();
				ThreadPools.INSTANCE.clear();
			}
		}
	}
}
