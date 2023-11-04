package raccoonman.reterraforged.common.level.levelgen.test.tile.gen;

import java.util.concurrent.TimeUnit;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.Cache;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.CacheEntry;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPool;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileFactory;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;

public class TileCache implements TileProvider {
	public static int QUEUING_MIN_POOL_SIZE = 4;
	private boolean canQueue;
	private TileFactory generator;
	private ThreadPool threadPool;
	private Cache<CacheEntry<Tile>> cache;

	public TileCache(TileFactory generator, ThreadPool threadPool) {
		this.canQueue = threadPool.size() > 4;
		this.generator = generator;
		this.threadPool = threadPool;
		this.cache = new Cache<>("TileCache", 256, 60L, 20L, TimeUnit.SECONDS);
		generator.setListener(this);
	}

	@Override
	public void onDispose(Tile tile) {
		this.cache.remove(tile.getRegionId());
	}

	@Override
	public int chunkToRegion(int coord) {
		return this.generator.chunkToRegion(coord);
	}

	@Override
	public CacheEntry<Tile> get(long id) {
		return this.cache.get(id);
	}

	@Override
	public CacheEntry<Tile> getOrCompute(long id) {
		return this.cache.computeIfAbsent(id, this::computeCacheEntry);
	}

	@Override
	public void queueChunk(int chunkX, int chunkZ) {
		if (!this.canQueue) {
			return;
		}
		this.queueRegion(this.chunkToRegion(chunkX), this.chunkToRegion(chunkZ));
	}

	@Override
	public void queueRegion(int regionX, int regionZ) {
		if (!this.canQueue) {
			return;
		}
		this.getOrCompute(Tile.getRegionId(regionX, regionZ));
	}

	protected CacheEntry<Tile> computeCacheEntry(long id) {
		int regionX = Tile.getRegionX(id);
		int regionZ = Tile.getRegionZ(id);
		LazyCallable<Tile> tile = this.generator.getTile(regionX, regionZ);
		return CacheEntry.computeAsync(tile, this.threadPool);
	}
}
