package raccoonman.reterraforged.common.level.levelgen.test.world.rivermap;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;

public class LegacyRiverCache extends RiverCache {
	
    public LegacyRiverCache(RiverGenerator generator) {
        super(generator);
    }
    
    @Override
    public Rivermap getRivers(Cell cell, int seed) {
        return this.getRivers(cell.continentX, cell.continentZ, seed);
    }
    
    @Override
    public Rivermap getRivers(int x, int z, int seed) {
        return this.cache.computeIfAbsent(NoiseUtil.seed(x, z), id -> this.generator.generateRivers(x, z, id, seed));
    }
}
