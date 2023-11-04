package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;

public class ScaledPopulator extends TerrainPopulator {
    private float baseScale;
    private float varianceScale;
    
    public ScaledPopulator(Terrain type, Noise base, Noise variance, Noise ridge, Noise erosion, float baseScale, float varianceScale, float weight) {
        super(type, base, variance, ridge, erosion, weight);
        this.baseScale = baseScale;
        this.varianceScale = varianceScale;
    }
    
    @Override
    public void apply(Cell cell, float x, float z, int seed) {
        float base = this.base.compute(x, z, seed) * this.baseScale;
        float variance = this.variance.compute(x, z, seed) * this.varianceScale;
        cell.value = base + variance;
        cell.ridgeLevel = this.ridge.compute(x, z, seed);
        cell.erosionLevel = this.erosion.compute(x, z, seed);
        if (cell.value < 0.0F) {
            cell.value = 0.0F;
        }
        cell.terrain = this.type;
    }
}
