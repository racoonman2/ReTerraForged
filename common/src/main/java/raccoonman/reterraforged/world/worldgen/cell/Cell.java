package raccoonman.reterraforged.world.worldgen.cell;

import raccoonman.reterraforged.concurrent.Resource;
import raccoonman.reterraforged.concurrent.SimpleResource;
import raccoonman.reterraforged.concurrent.pool.ThreadLocalPool;
import raccoonman.reterraforged.world.worldgen.cell.biome.type.BiomeType;
import raccoonman.reterraforged.world.worldgen.cell.terrain.Terrain;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;

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
    public float heightErosion;
    public float sediment;
    public float gradient;
    public float regionMoisture;
    public float regionTemperature;
    public float continentId;
    public float continentEdge;
    public float terrainRegionId;
    public float terrainRegionEdge;
    public float biomeRegionId;
    public float biomeRegionEdge;
    public float macroBiomeId;
    public float riverMask;
    public int continentX;
    public int continentZ;
    public boolean erosionMask;
    public Terrain terrain;
    public BiomeType biome;
    public float erosion;
    public float weirdness;
    public float temperature;
    public float moisture;
    
    @Deprecated(forRemoval = true)
    public float beachNoise;

    public Cell() {
        this.regionMoisture = 0.5F;
        this.regionTemperature = 0.5F;
        this.biomeRegionEdge = 1.0F;
        this.riverMask = 1.0F;
        this.erosionMask = false;
        this.terrain = TerrainType.NONE;
        this.biome = BiomeType.GRASSLAND;
    }
    
    public void copyFrom(Cell other) {
        this.height = other.height;
        this.heightErosion = other.heightErosion;
        this.sediment = other.sediment;
        this.gradient = other.gradient;
        this.regionMoisture = other.regionMoisture;
        this.regionTemperature = other.regionTemperature;
        this.continentId = other.continentId;
        this.continentEdge = other.continentEdge;
        this.terrainRegionId = other.terrainRegionId;
        this.terrainRegionEdge = other.terrainRegionEdge;
        this.biomeRegionId = other.biomeRegionId;
        this.biomeRegionEdge = other.biomeRegionEdge;
        this.macroBiomeId = other.macroBiomeId;
        this.riverMask = other.riverMask;
        this.continentX = other.continentX;
        this.continentZ = other.continentZ;
        this.erosionMask = other.erosionMask;
        this.terrain = other.terrain;
        this.biome = other.biome;
        this.erosion = other.erosion;
        this.weirdness = other.weirdness;
        this.temperature = other.temperature;
        this.moisture = other.moisture;
        this.beachNoise = other.beachNoise;
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
