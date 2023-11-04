package raccoonman.reterraforged.common.level.levelgen.test.world.rivermap;

import java.util.concurrent.TimeUnit;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.Cache;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.map.StampedLongMap;

public class RiverCache {
    protected RiverGenerator generator;
    protected Cache<Rivermap> cache;
    
    public RiverCache(RiverGenerator generator) {
        this.cache = new Cache<>("RiverCache", 32, 5L, 1L, TimeUnit.MINUTES, StampedLongMap::new);
        this.generator = generator;
    }
    
    public Rivermap getRivers(Cell cell, int seed) {
        return this.getRivers(cell.continentX, cell.continentZ, seed);
    }
    
    public Rivermap getRivers(int x, int z, int seed) {
        return this.cache.computeIfAbsent(NoiseUtil.pack(x, z), id -> this.generator.generateRivers(NoiseUtil.unpackLeft(id), NoiseUtil.unpackRight(id), id, seed));
    }
}
