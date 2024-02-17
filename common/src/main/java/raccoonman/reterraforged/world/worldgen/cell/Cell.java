package raccoonman.reterraforged.world.worldgen.cell;

import raccoonman.reterraforged.concurrent.Resource;
import raccoonman.reterraforged.concurrent.SimpleResource;
import raccoonman.reterraforged.concurrent.pool.ThreadLocalPool;
import raccoonman.reterraforged.world.worldgen.biome.type.BiomeType;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;

public class Cell {
    private static final Cell DEFAULTS = new Cell();
    private static final Cell EMPTY = new Cell() {

    	@Override
        public boolean isAbsent() {
            return true;
        }
    };
    private static final ThreadLocalPool<Cell> POOL = new ThreadLocalPool<>(32, Cell::new, Cell::reset);
    public static final ThreadLocal<Resource<Cell>> LOCAL = ThreadLocal.withInitial(() -> {
        return new SimpleResource<>(new Cell(), Cell::reset);
    });
    public float height;
    @Deprecated
    public float localErosion;
    public float localErosion2;
    @Deprecated
    public float sediment;
    public float sediment2;
    public float gradient;
    public float regionMoisture;
    public float regionTemperature;
    public float continentId;
    public float continentNoise;
    public float continentEdge;
    public float continentalness;
    public float terrainRegionId;
    public float terrainRegionEdge;
    public long terrainRegionCenter;
    public float biomeRegionId;
    public float biomeRegionEdge;
    public float macroBiomeId;
    public float riverDistance;
    public int continentX;
    public int continentZ;
    public boolean erosionMask;
    public Terrain terrain;
    public float terrainMask;
    //TODO
    public float borderMask;
    @Deprecated
    public float mountainChainAlpha;
    public BiomeType biomeType;
    public float erosion;
    public float weirdness;
    public float temperature;
    public float moisture;
    
    public Cell() {
        this.regionMoisture = 0.5F;
        this.regionTemperature = 0.5F;
        this.biomeRegionEdge = 1.0F;
        this.riverDistance = 1.0F;
        this.erosionMask = false;
        this.terrain = TerrainType.NONE;
        this.terrainMask = 1.0F;
        this.biomeType = BiomeType.GRASSLAND;
    }
    
    public void copyFrom(Cell other) {
        this.height = other.height;
        this.localErosion = other.localErosion;
        this.localErosion2 = other.localErosion2;
        this.sediment = other.sediment;
        this.sediment2 = other.sediment2;
        this.gradient = other.gradient;
        this.regionMoisture = other.regionMoisture;
        this.regionTemperature = other.regionTemperature;
        this.continentId = other.continentId;
        this.continentNoise = other.continentNoise;
        this.continentEdge = other.continentEdge;
        this.continentalness = other.continentalness;
        this.terrainRegionId = other.terrainRegionId;
        this.terrainRegionEdge = other.terrainRegionEdge;
        this.terrainRegionCenter = other.terrainRegionCenter;
        this.biomeRegionId = other.biomeRegionId;
        this.biomeRegionEdge = other.biomeRegionEdge;
        this.macroBiomeId = other.macroBiomeId;
        this.riverDistance = other.riverDistance;
        this.continentX = other.continentX;
        this.continentZ = other.continentZ;
        this.erosionMask = other.erosionMask;
        this.terrain = other.terrain;
        this.terrainMask = other.terrainMask;
        this.borderMask = other.borderMask;
        this.biomeType = other.biomeType;
        this.erosion = other.erosion;
        this.weirdness = other.weirdness;
        this.temperature = other.temperature;
        this.moisture = other.moisture;
    }

    public Cell reset() {
        this.copyFrom(Cell.DEFAULTS);
        return this;
    }

    @Deprecated(forRemoval = true)
    public boolean isAbsent() {
        return false;
    }

    @Deprecated(forRemoval = true)
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
    
    public interface Visitor {
        void visit(Cell cell, int x, int z);
    }
}
