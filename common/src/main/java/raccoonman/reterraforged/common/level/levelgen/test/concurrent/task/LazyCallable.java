package raccoonman.reterraforged.common.level.levelgen.test.concurrent.task;

import java.util.function.Function;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Supplier;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPool;

import java.util.concurrent.Future;
import java.util.concurrent.Callable;

public abstract class LazyCallable<T> implements Callable<T>, Future<T>, Supplier<T> {
	private StampedLock lock;
	protected volatile T value;

	public LazyCallable() {
		this.lock = new StampedLock();
		this.value = null;
	}

	@Override
	public T call() {
		long optRead = this.lock.tryOptimisticRead();
		T result = this.value;
		if (this.lock.validate(optRead) && result != null) {
			return result;
		}
		long read = this.lock.readLock();
		try {
			result = this.value;
			if (result != null) {
				return result;
			}
		} finally {
			this.lock.unlockRead(read);
		}
		long write = this.lock.writeLock();
		try {
			result = this.value;
			if (result == null) {
				result = this.create();
				Objects.requireNonNull(result);
				this.value = result;
			}
			return result;
		} finally {
			this.lock.unlockWrite(write);
		}
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		return false;
	}

	@Override
	public boolean isCancelled() {
		return false;
	}

	@Override
	public boolean isDone() {
		long optRead = this.lock.tryOptimisticRead();
		boolean done = this.value != null;
		if (this.lock.validate(optRead)) {
			return done;
		}
		long read = this.lock.readLock();
		try {
			return this.value != null;
		} finally {
			this.lock.unlockRead(read);
		}
	}

	@Override
	public T get() {
		return this.call();
	}

	@Override
	public T get(long timeout, TimeUnit unit) {
		return this.call();
	}

	public <V> LazyCallable<V> then(ThreadPool executor, Function<T, V> function) {
		return callAsync(() -> function.apply(this.get()), executor);
	}

	protected abstract T create();

	public static LazyCallable<Void> adapt(Runnable runnable) {
		return new RunnableAdapter(runnable);
	}

	public static <T> LazyCallable<T> adapt(Callable<T> callable) {
		if (callable instanceof LazyCallable<T> c) {
			return c;
		}
		return new CallableAdapter<>(callable);
	}

	public static <T> LazyCallable<T> adaptComplete(Callable<T> callable) {
		return new CompleteAdapter<>(callable);
	}

	public static <T> LazyCallable<T> callAsync(Callable<T> callable, ThreadPool executor) {
		return new FutureAdapter<>(executor.submit(callable));
	}

	public static class CallableAdapter<T> extends LazyCallable<T> {
		private Callable<T> callable;

		public CallableAdapter(Callable<T> callable) {
			this.callable = callable;
		}

		@Override
		protected T create() {
			try {
				return this.callable.call();
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		}
	}

	public static class FutureAdapter<T> extends LazyCallable<T> {
		private Future<T> future;

		FutureAdapter(Future<T> future) {
			this.future = future;
		}

		@Override
		public boolean isDone() {
			return this.future.isDone();
		}

		@Override
		protected T create() {
			try {
				return this.future.get();
			} catch (Throwable t) {
				t.printStackTrace();
				return null;
			}
		}
	}

	public static class RunnableAdapter extends LazyCallable<Void> {
		private Runnable runnable;

		RunnableAdapter(Runnable runnable) {
			this.runnable = runnable;
		}

		@Override
		protected Void create() {
			this.runnable.run();
			return null;
		}
	}

	public static class CompleteAdapter<T> extends LazyCallable<T> {
		private Callable<T> callable;

		public CompleteAdapter(Callable<T> callable) {
			this.callable = callable;
		}

		@Override
		protected T create() {
			try {
				return this.callable.call();
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public boolean isDone() {
			return true;
		}
	}
}
