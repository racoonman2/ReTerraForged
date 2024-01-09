package raccoonman.reterraforged.world.worldgen.cell.terrain.populator;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;

public class IslandPopulator implements CellPopulator {
	private CellPopulator lower;
    private CellPopulator upper;
    private Interpolation interpolation;
    private float blendLower;
    private float blendUpper;
    private float blendRange;
    
    public IslandPopulator(CellPopulator lower, CellPopulator upper, float min, float max) {
        this(lower, upper, min, max, Interpolation.LINEAR);
    }
    
    public IslandPopulator(CellPopulator lower, CellPopulator upper, float min, float max, Interpolation interpolation) {
        this.lower = lower;
        this.upper = upper;
        this.interpolation = interpolation;
        this.blendLower = min;
        this.blendUpper = max;
        this.blendRange = this.blendUpper - this.blendLower;
    }
    
    @Override
    public void apply(Cell cell, float x, float y) {
    	float islandAlpha = cell.continentDistance;
    	if(cell.continentEdge > 0.0F || cell.terrainRegionEdge < 0.875F || cell.terrainRegionId > 0.2F) {
        	islandAlpha = 0.0F;
    	}
    	
        if (islandAlpha < this.blendLower) {
            this.lower.apply(cell, x, y);
            return;
        }
        if (islandAlpha > this.blendUpper) {
            this.upper.apply(cell, x, y);
            return;
        }
        float alpha = this.interpolation.apply((islandAlpha - this.blendLower) / this.blendRange);
        this.lower.apply(cell, x, y);
        float lowerHeight = cell.height;
        this.upper.apply(cell, x, y);
        float upperHeight = cell.height;
        cell.height = NoiseUtil.lerp(lowerHeight, upperHeight, alpha);
    }
}
