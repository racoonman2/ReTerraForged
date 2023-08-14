package raccoonman.reterraforged.common.level.levelgen.continent.river2;

import java.util.List;

import raccoonman.reterraforged.common.util.pos.PosUtil;
import raccoonman.reterraforged.common.util.storage.LongCache;
import raccoonman.reterraforged.common.util.storage.LossyCache;

//TODO
public class RiverSampler {
    private static final int RIVER_CACHE_SIZE = 1024;
//    private final int seed;
    // rivers per cell
    private final LongCache<RiverNode> nodeCache;
    
	public RiverSampler(int seed) {
//		this.seed = seed;
		this.nodeCache = LossyCache.concurrent(RIVER_CACHE_SIZE, RiverNode[]::new);
	}
	
	public RiverNode getAtCell(int cellX, int cellZ) {
		return this.nodeCache.computeIfAbsent(PosUtil.pack(cellX, cellZ), this::getUncached);
	}
	
	private RiverNode getUncached(long cell) {
//		int cellX = PosUtil.unpackLeft(cell);
//		int cellZ = PosUtil.unpackRight(cell);
//		int cellSeed = NoiseUtil.hash2D(this.seed, cellX, cellZ);
	
		//TODO
		
		return null;
	}
	
	private record RiverNode(List<RiverNode> nodes) {
		//TODO
	}
}
