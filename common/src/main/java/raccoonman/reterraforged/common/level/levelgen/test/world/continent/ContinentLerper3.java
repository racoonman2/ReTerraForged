package raccoonman.reterraforged.common.level.levelgen.test.world.continent;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;

public class ContinentLerper3 implements Populator {
    private Populator lower;
    private Populator middle;
    private Populator upper;
    private Interpolation interpolation;
    private float midpoint;
    private float blendLower;
    private float blendUpper;
    private float lowerRange;
    private float upperRange;
    
    public ContinentLerper3(Populator lower, Populator middle, Populator upper, float min, float mid, float max) {
        this(lower, middle, upper, min, mid, max, Interpolation.CURVE3);
    }
    
    public ContinentLerper3(Populator lower, Populator middle, Populator upper, float min, float mid, float max, Interpolation interpolation) {
        this.lower = lower;
        this.upper = upper;
        this.middle = middle;
        this.interpolation = interpolation;
        this.midpoint = mid;
        this.blendLower = min;
        this.blendUpper = max;
        this.lowerRange = this.midpoint - this.blendLower;
        this.upperRange = this.blendUpper - this.midpoint;
    }
    
    @Override
    public void apply(Cell cell, float x, float y, int seed) {
        float select = cell.continentEdge;
        if (select < this.blendLower) {
            this.lower.apply(cell, x, y, seed);
            return;
        }
        if (select > this.blendUpper) {
            this.upper.apply(cell, x, y, seed);
            return;
        }
        if (select < this.midpoint) {
            float alpha = this.interpolation.apply((select - this.blendLower) / this.lowerRange);
            this.lower.apply(cell, x, y, seed);
            float lowerVal = cell.value;
            this.middle.apply(cell, x, y, seed);
            cell.value = NoiseUtil.lerp(lowerVal, cell.value, alpha);
        } else {
            float alpha = this.interpolation.apply((select - this.midpoint) / this.upperRange);
            this.middle.apply(cell, x, y, seed);
            float lowerVal = cell.value;
            this.upper.apply(cell, x, y, seed);
            cell.value = NoiseUtil.lerp(lowerVal, cell.value, alpha);
        }
    }
}
