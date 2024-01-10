package raccoonman.reterraforged.world.worldgen.cell.terrain.populator;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class IslandPopulator implements CellPopulator {
	private CellPopulator ocean;
    private IslandType upper;
	private Interpolation interpolation;
    private float blendLower;
    private float blendUpper;
    private float blendRange;
    private Noise islandThresholdNoise;
    private Noise islandChanceVarianceNoise;
    
    public IslandPopulator(Levels levels, CellPopulator ocean, float min, float max) {
        this(levels, ocean, min, max, Interpolation.LINEAR);
    }
    
    public IslandPopulator(Levels levels, CellPopulator ocean, float min, float max, Interpolation interpolation) {
        this.ocean = ocean;
        this.upper = upperPopulator(levels, -5);
        this.interpolation = interpolation;
        this.blendLower = min;
        this.blendUpper = max;
        this.blendRange = this.blendUpper - this.blendLower;
        
        Noise islandThresholdNoise = Noises.simplex(3526, 1200, 1);
        islandThresholdNoise = Noises.warpPerlin(islandThresholdNoise, 3526, 1200, 3, 600);
        islandThresholdNoise = Noises.clamp(islandThresholdNoise, 0.0F, 1.0F);
        islandThresholdNoise = Noises.map(islandThresholdNoise, 0.7F, 0.8F);
        this.islandThresholdNoise = islandThresholdNoise;
        
        Noise islandChanceVarianceNoise = Noises.simplex(54326, 1, 3);
        islandChanceVarianceNoise = Noises.clamp(islandChanceVarianceNoise, 0.0F, 1.0F);
        islandChanceVarianceNoise = Noises.map(islandChanceVarianceNoise, -0.05F, 0.2F);
        this.islandChanceVarianceNoise = islandChanceVarianceNoise;
    }
    
    @Override
    public void apply(Cell cell, float x, float z) {
    	float islandThresholdMin = this.islandThresholdNoise.compute(x, z, 0);
    	float islandThresholdMax = islandThresholdMin + 4.0F;

    	float regionVarianceAlpha = cell.terrainRegionId > this.islandChanceVarianceNoise.compute(cell.continentX, cell.continentZ, 0) ? 0.0F : 1.0F;
    	float regionEdgeAlpha = NoiseUtil.clamp(cell.terrainRegionEdge, islandThresholdMin, islandThresholdMax);
    	regionEdgeAlpha = NoiseUtil.map(regionEdgeAlpha, 0.0F, 1.0F, 2.0F);
    	
    	float islandAlpha = cell.continentDistance * regionVarianceAlpha * regionEdgeAlpha;
        if (islandAlpha < this.blendLower) {
            this.ocean.apply(cell, x, z);
            return;
        }
        if (islandAlpha > this.blendUpper) {
            this.upper.apply(cell, x, z, islandAlpha);
            return;
        }
        float alpha = this.interpolation.apply((islandAlpha - this.blendLower) / this.blendRange);
        this.ocean.apply(cell, x, z);
        float lowerHeight = cell.height;
        this.upper.apply(cell, x, z, islandAlpha);
        float upperHeight = cell.height;
        cell.height = NoiseUtil.lerp(lowerHeight, upperHeight, alpha);
    }
    
    private static IslandType upperPopulator(Levels levels, int depth) {
    	// TODO sample noise for this, thisll give us the islands we want
    	float archipelagoMaxAlpha = 0.01F;
		float archipelagoMin = levels.water(5);
		float archipelagoMax = levels.water(depth);
    	return (cell, x, y, islandAlpha) -> {
    		float archipelagoAlpha = islandAlpha;
    		if(archipelagoAlpha > archipelagoMaxAlpha) {
    			archipelagoAlpha -= (archipelagoAlpha - archipelagoMaxAlpha);
    		}

    		cell.terrain = TerrainType.MUSHROOM_FIELDS;
    		cell.height = NoiseUtil.lerp(archipelagoMin, archipelagoMax, archipelagoAlpha);
    	};
    }
    
    public interface IslandType {
    	void apply(Cell cell, float x, float z, float islandAlpha);
    }
}
