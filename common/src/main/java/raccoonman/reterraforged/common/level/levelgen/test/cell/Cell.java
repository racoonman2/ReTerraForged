package raccoonman.reterraforged.common.level.levelgen.test.cell;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.SimpleResource;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.pool.ThreadLocalPool;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.BiomeType;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;

public class Cell {
    private static final Cell DEFAULTS = new Cell();
    private static final Cell EMPTY = new Cell() {
        @Override
        public boolean isAbsent() {
            return true;
        }
    };
    private static final ThreadLocalPool<Cell> POOL = new ThreadLocalPool<>(32, Cell::new, Cell::reset);
    private static final ThreadLocal<Resource<Cell>> LOCAL = ThreadLocal.withInitial(() -> {
        return new SimpleResource<>(new Cell(), Cell::reset);
    });
    public float value;
    public float erosion;
    public float sediment;
    public float gradient;
    public float moisture;
    public float temperature;
    public float continentId;
    public float continentEdge;
    public float terrainRegionId;
    public float terrainRegionEdge;
    public long terrainRegionCenter;
    public float biomeRegionId;
    public float biomeRegionEdge;
    public float macroBiomeId;
    public float riverMask;
    public int continentX;
    public int continentZ;
    public boolean erosionMask;
    public Terrain terrain;
    public BiomeType biome;
    public float erosionLevel;
    public float ridgeLevel;
    public float continentLevel;
    public float temperatureLevel;
    public float moistureLevel;
    
    public Cell() {
        this.moisture = 0.5F;
        this.temperature = 0.5F;
        this.biomeRegionEdge = 1.0F;
        this.riverMask = 1.0F;
        this.erosionMask = false;
        this.terrain = TerrainType.NONE;
        this.biome = BiomeType.GRASSLAND;
    }
    
    public void copyFrom(Cell other) {
        this.value = other.value;
        this.continentX = other.continentX;
        this.continentZ = other.continentZ;
        this.continentId = other.continentId;
        this.continentEdge = other.continentEdge;
        this.terrainRegionId = other.terrainRegionId;
        this.terrainRegionEdge = other.terrainRegionEdge;
        this.biomeRegionId = other.biomeRegionId;
        this.biomeRegionEdge = other.biomeRegionEdge;
        this.riverMask = other.riverMask;
        this.erosionMask = other.erosionMask;
        this.moisture = other.moisture;
        this.temperature = other.temperature;
        this.macroBiomeId = other.macroBiomeId;
        this.gradient = other.gradient;
        this.erosion = other.erosion;
        this.sediment = other.sediment;
        this.biome = other.biome;
        this.terrain = other.terrain;
        this.erosionLevel = other.erosionLevel;
        this.ridgeLevel = other.ridgeLevel;
        this.continentLevel = other.continentLevel;
        this.temperatureLevel = other.temperatureLevel;
        this.moistureLevel = other.moistureLevel;
    }
    
    public Cell reset() {
        this.copyFrom(Cell.DEFAULTS);
        return this;
    }
    
    public boolean isAbsent() {
        return false;
    }
    
    public static Cell empty() {
        return Cell.EMPTY;
    }
    
    public static Resource<Cell> getResource() {
        Resource<Cell> resource = Cell.LOCAL.get();
        if (resource.isOpen()) {
            return Cell.POOL.get();
        }
        return resource;
    }
    
    public interface ContextVisitor<C> {
        void visit(Cell cell, int x, int z, C ctx);
    }
    
    public interface Visitor {
        void visit(Cell cell, int x, int z);
    }
}
