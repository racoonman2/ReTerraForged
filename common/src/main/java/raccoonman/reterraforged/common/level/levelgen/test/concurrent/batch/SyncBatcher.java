package raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;

public class SyncBatcher implements Batcher, Resource<Batcher> {

	@Override
	public void size(int size) {
	}

	@Override
	public void submit(Runnable task) {
		task.run();
	}

	@Override
	public Batcher get() {
		return this;
	}

	@Override
	public boolean isOpen() {
		return false;
	}

	@Override
	public void close() {
	}
}
