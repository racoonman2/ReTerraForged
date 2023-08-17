package raccoonman.reterraforged.common.level.levelgen.terrain;

import raccoonman.reterraforged.common.util.pos.PosUtil;
import raccoonman.reterraforged.common.util.storage.LongCache;
import raccoonman.reterraforged.common.util.storage.LossyCache;
import raccoonman.reterraforged.common.util.storage.ObjectPool;

public class TerrainGenerator {
	private TerrainNoise noise;
	private ObjectPool<TerrainCache> pool;
	private LongCache<TerrainCache> cache;
	
	public TerrainGenerator(TerrainNoise noise) {
		this.noise = noise;
		this.pool = ObjectPool.forCacheSize(2048, () -> {
			return new TerrainCache(noise.levels);
		});
		this.cache = LossyCache.concurrent(512, TerrainCache[]::new, this.pool::restore);
	}
	
	public void generate(int x, int z, TerrainCache terrainData) {
		this.noise.generate(x, z, terrainData::fill);
	}
	
	public TerrainCache getCached(int x, int z) {
		return this.cache.computeIfAbsent(PosUtil.pack(x, z), (k) -> {
			TerrainCache terrain = this.pool.take();
			this.generate(PosUtil.unpackLeft(k), PosUtil.unpackRight(k), terrain);
			return terrain;
		});
	}
}
