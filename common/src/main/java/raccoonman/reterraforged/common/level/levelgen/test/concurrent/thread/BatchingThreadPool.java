package raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.Batcher;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.TaskBatcher;;

public class BatchingThreadPool implements ThreadPool {
	private int size;
	private boolean managed;
	private ExecutorService taskExecutor;
	private ExecutorService batchExecutor;

	private BatchingThreadPool(int taskSize, int batchSize, boolean managed) {
		this.managed = managed;
		this.size = taskSize + batchSize;
		this.taskExecutor = Executors.newFixedThreadPool(taskSize, new WorkerFactory("TF-Task"));
		this.batchExecutor = Executors.newFixedThreadPool(batchSize, new WorkerFactory("TF-Batch"));
	}

	@Override
	public int size() {
		return this.size;
	}

	@Override
	public Future<?> submit(Runnable runnable) {
		return this.taskExecutor.submit(runnable);
	}

	@Override
	public <T> Future<T> submit(Callable<T> callable) {
		return this.taskExecutor.submit(callable);
	}

	@Override
	public boolean isManaged() {
		return this.managed;
	}

	@Override
	public void shutdown() {
		this.taskExecutor.shutdown();
		this.batchExecutor.shutdown();
		ThreadPools.markShutdown(this);
	}

	@Override
	public void shutdownNow() {
		this.taskExecutor.shutdownNow();
		this.batchExecutor.shutdownNow();
	}

	@Override
	public Resource<Batcher> batcher() {
		return new TaskBatcher(this.batchExecutor);
	}

	public static ThreadPool of(int size, boolean keepalive) {
		int tasks = Math.max(1, size / 2);
		int batches = Math.max(2, size);
		return new BatchingThreadPool(tasks, batches, keepalive);
	}
}
