package raccoonman.reterraforged.world.worldgen.terrain.populator;

import raccoonman.reterraforged.data.preset.settings.TerrainSettings;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;

public record TerrainPopulator(Terrain type, Noise base, Noise height, Noise erosion, Noise weirdness, float baseScale, float heightScale, float weight) implements CellPopulator, WeightedPopulator {
    
	public TerrainPopulator(Terrain type, Noise base, Noise height, Noise erosion, Noise weirdness, float weight) {
		this(type, base, height, erosion, weirdness, 1.0F, 1.0F, weight);
	}
	
    @Override
    public void apply(Cell cell, float x, float z) {
        float base = this.base.compute(x, z, 0) * this.baseScale;
        float height = this.height.compute(x, z, 0) * this.heightScale;

        cell.terrain = this.type;
        cell.height = Math.max(base + height, 0.0F);
        cell.erosion = this.erosion.compute(x, z, 0);
        cell.weirdness = this.weirdness.compute(x, z, 0);
    }
    
    public static TerrainPopulator make(Terrain type, Noise base, Noise height, Noise erosion, Noise weirdness, TerrainSettings.Terrain settings) {
    	return new TerrainPopulator(type, base, height, erosion, weirdness, settings.baseScale, settings.verticalScale, settings.weight);
    }
}
