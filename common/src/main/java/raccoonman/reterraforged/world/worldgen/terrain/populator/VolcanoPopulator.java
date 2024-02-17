package raccoonman.reterraforged.world.worldgen.terrain.populator;

import raccoonman.reterraforged.world.worldgen.biome.Erosion;
import raccoonman.reterraforged.world.worldgen.biome.Weirdness;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.terrain.region.RegionSelector;

public record VolcanoPopulator(float weight, Noise ground, Noise cone, Noise height, Noise lowlands, float inversionPoint, float blendLower, float blendUpper, float blendRange, Levels levels) implements CellPopulator, RegionSelector.Weighted {
    
    @Override
    public void apply(Cell cell, float x, float z) {
        float value = this.cone.compute(x, z, 0);
        float limit = this.height.compute(x, z, 0);
        float maxHeight = limit * this.inversionPoint;
        if (value > maxHeight) {
            float steepnessModifier = 1.0F;
            float delta = (value - maxHeight) * steepnessModifier;
            float range = limit - maxHeight;
            float alpha = delta / range;
            if (alpha > 0.925F) {
                cell.terrain = TerrainType.VOLCANO_PIPE;
            }
            value = maxHeight - maxHeight / 5.0F * alpha;
        } else if (value < this.blendLower) {
            float lowlands = this.lowlands.compute(x, z, 0);
            value += lowlands;
            cell.terrain = TerrainType.VOLCANO;
        } else if (value < this.blendUpper) {
            float alpha2 = 1.0F - (value - this.blendLower) / this.blendRange;
            float lowlands = this.lowlands.compute(x, z, 0);
            value += lowlands * alpha2;
            cell.terrain = TerrainType.VOLCANO;
        }
        cell.height = this.ground.compute(x, z, 0) + value;
        cell.weirdness = Weirdness.LOW_SLICE_VARIANT_ASCENDING.midpoint();
        cell.erosion = Erosion.LEVEL_4.midpoint();
    }
    
    @Override
    public CellPopulator mapNoise(Noise.Visitor visitor) {
    	return new VolcanoPopulator(this.weight, this.ground.mapAll(visitor), this.cone.mapAll(visitor), this.height.mapAll(visitor), this.lowlands.mapAll(visitor), this.inversionPoint, this.blendLower, this.blendUpper, this.blendRange, this.levels);
    }
    
    public static void modifyVolcanoType(Cell cell, Levels levels) {
        if (cell.terrain == TerrainType.VOLCANO_PIPE && (cell.height < levels.water || cell.riverDistance < 0.85F)) {
            cell.terrain = TerrainType.VOLCANO;
        }
    }
}
