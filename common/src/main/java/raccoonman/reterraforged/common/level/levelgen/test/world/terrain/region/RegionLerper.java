package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.region;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;

public class RegionLerper implements Populator {
    private Populator lower;
    private Populator upper;
    
    public RegionLerper(Populator lower, Populator upper) {
        this.lower = lower;
        this.upper = upper;
    }
    
    @Override
    public void apply(Cell cell, float x, float y, int seed) {
        float alpha = cell.terrainRegionEdge;
        if (alpha == 0.0F) {
            this.lower.apply(cell, x, y, seed);
            return;
        }
        if (alpha == 1.0F) {
            this.upper.apply(cell, x, y, seed);
            return;
        }
        this.lower.apply(cell, x, y, seed);
        float lowerValue = cell.value;
        this.upper.apply(cell, x, y, seed);
        float upperValue = cell.value;
        cell.value = NoiseUtil.lerp(lowerValue, upperValue, alpha);
    }
}
