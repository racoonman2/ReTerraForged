package raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;

public class TaskBatcher implements Batcher, BatchTask.Notifier, Resource<Batcher> {
	private Executor executor;
	private volatile CountDownLatch latch;

	public TaskBatcher(Executor executor) {
		this.executor = executor;
	}

	@Override
	public Batcher get() {
		return this;
	}

	@Override
	public boolean isOpen() {
		CountDownLatch latch = this.latch;
		return latch != null && latch.getCount() > 0L;
	}

	@Override
	public void markDone() {
		CountDownLatch latch = this.latch;
		latch.countDown();
	}

	@Override
	public void size(int size) {
		this.latch = new CountDownLatch(size);
	}

	@Override
	public void submit(Runnable task) {
	}

	@Override
	public void submit(BatchTask task) {
		CountDownLatch latch = this.latch;
		if (latch == null) {
			throw new IllegalStateException("Submitted batch task before setting the size limit!");
		}
		task.setNotifier(this);
		this.executor.execute(task);
	}

	@Override
	public void close() {
		CountDownLatch latch = this.latch;
		if (latch == null) {
			throw new IllegalStateException("Closed batcher before any work was done!");
		}
		try {
			if (!latch.await(60L, TimeUnit.SECONDS)) {
				throw new BatchTimeoutException("Heightmap generation took over 60 seconds. Check logs for errors");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
