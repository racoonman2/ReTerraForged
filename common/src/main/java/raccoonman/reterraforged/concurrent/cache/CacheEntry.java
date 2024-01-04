package raccoonman.reterraforged.concurrent.cache;

import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.Future;

import raccoonman.reterraforged.concurrent.task.LazyCallable;

public class CacheEntry<T> extends LazyCallable<T> implements ExpiringEntry {
	private volatile long timestamp;
	private Future<T> task;

	public CacheEntry(Future<T> task) {
		this.task = task;
		this.timestamp = System.currentTimeMillis();
	}

	@Override
	public T get() {
		this.timestamp = System.currentTimeMillis();
		return super.get();
	}

	@Override
	public boolean isDone() {
		return this.task.isDone();
	}

	@Override
	public long getTimestamp() {
		return this.timestamp;
	}

	@Override
	public void close() {
		if (this.value instanceof SafeCloseable value) {
			value.close();
			return;
		}
		if (this.value instanceof AutoCloseable value) {
			try {
				value.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected T create() {
		if (this.task instanceof ForkJoinTask<T> task) {
			return task.join();
		}
		try {
			return this.task.get();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static <T> CacheEntry<T> supply(Future<T> task) {
		return new CacheEntry<>(task);
	}
}
