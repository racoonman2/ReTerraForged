package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;

public class TerrainPopulator implements Populator {
    protected float weight;
    protected Terrain type;
    protected Noise base;
    protected Noise variance;
    protected Noise ridge;
    protected Noise erosion;
    
    public TerrainPopulator(Terrain type, Noise base, Noise variance, Noise ridge, Noise erosion, float weight) {
        this.type = type;
        this.base = base;
        this.variance = variance;
        this.ridge = ridge;
        this.erosion = erosion;
        this.weight = weight;
    }
    
    public float getWeight() {
        return this.weight;
    }
    
    public Noise getVariance() {
        return this.variance;
    }
    
    public Noise getRidge() {
    	return this.ridge;
    }
    
    public Noise getErosion() {
    	return this.erosion;
    }
    
    public Terrain getType() {
        return this.type;
    }
    
    @Override
    public void apply(Cell cell, float x, float z, int seed) {
        float base = this.base.compute(x, z, seed);
        float variance = this.variance.compute(x, z, seed);
        cell.value = base + variance;
        cell.ridgeLevel = this.ridge.compute(x, z, seed);
        cell.erosionLevel = this.erosion.compute(x, z, seed);
        if (cell.value < 0.0F) {
            cell.value = 0.0F;
        }
        cell.terrain = this.type;
    }
    
    public static TerrainPopulator of(Terrain type, Noise variance, Noise ridge, Noise erosion) {
        return new TerrainPopulator(type, Source.ZERO, variance, ridge, erosion, 1.0F);
    }
    
    public static TerrainPopulator of(Terrain type, Noise base, Noise variance, Noise ridge, Noise erosion, TerrainSettings.Terrain settings) {
        if (settings.verticalScale == 1.0F && settings.baseScale == 1.0F) {
            return new TerrainPopulator(type, base, variance, ridge, erosion, settings.weight);
        }
        return new ScaledPopulator(type, base, variance, ridge, erosion, settings.baseScale, settings.verticalScale, settings.weight);
    }
}
