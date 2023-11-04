package raccoonman.reterraforged.common.level.levelgen.test.world.heightmap;

import java.util.function.LongFunction;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.map.LoadBalanceLongMap;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.map.LongMap;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;

public class HeightmapCache {
    private static final int CACHE_SIZE = 4096;
    private float waterLevel;
    private float beachLevel;
    private Heightmap heightmap;
    private LongMap<Cell> cache;
    private LongFunction<Cell> compute;
    private LongFunction<Cell> contextCompute;
    private ThreadLocal<CachedContext> contextLocal;
    
    public HeightmapCache(Heightmap heightmap) {
        this(heightmap, CACHE_SIZE);
    }
    
    public HeightmapCache(Heightmap heightmap, int size) {
        this.compute = this::compute;
        this.contextCompute = this::contextCompute;
        this.contextLocal = ThreadLocal.withInitial(() -> new CachedContext());
        this.heightmap = heightmap;
        this.waterLevel = heightmap.getLevels().water;
        this.beachLevel = heightmap.getLevels().water(5);
        this.cache = new LoadBalanceLongMap<>(Runtime.getRuntime().availableProcessors(), size);
    }
    
    public Cell get(int x, int z) {
        long index = NoiseUtil.pack(x, z);
        return this.cache.computeIfAbsent(index, this.compute);
    }
    
    public Rivermap generate(Cell cell, int x, int z, Rivermap rivermap) {
        CachedContext context = this.contextLocal.get();
        try {
            context.cell = cell;
            context.rivermap = rivermap;
            long index = NoiseUtil.pack(x, z);
            Cell value = this.cache.computeIfAbsent(index, this.contextCompute);
            if (value != cell) {
                cell.copyFrom(value);
            }
            return context.rivermap;
        } finally {
            context.rivermap = null;
        }
    }
    
    private Cell compute(long index) {
        int x = NoiseUtil.unpackLeft(index);
        int z = NoiseUtil.unpackRight(index);
        Cell cell = new Cell();
        this.heightmap.apply(cell, x, z, 0);
        if (cell.terrain == TerrainType.COAST && cell.value > this.waterLevel && cell.value <= this.beachLevel) {
            cell.terrain = TerrainType.BEACH;
        }
        return cell;
    }
    
    private Cell contextCompute(long index) {
        CachedContext context = this.contextLocal.get();
        int x = NoiseUtil.unpackLeft(index);
        int z = NoiseUtil.unpackRight(index);
        this.heightmap.applyBase(context.cell, x, z);
        context.rivermap = Rivermap.get(context.cell, context.rivermap, this.heightmap);
        this.heightmap.applyRivers(context.cell, x, z, context.rivermap);
        this.heightmap.applyClimate(context.cell, x, z);
        return context.cell;
    }
    
    private static class CachedContext {
        private Cell cell;
        private Rivermap rivermap;
    }
}
