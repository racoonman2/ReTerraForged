package raccoonman.reterraforged.common.level.levelgen.test.world.continent;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;

public class ContinentLerper2 implements Populator {
    private Populator lower;
    private Populator upper;
    private Interpolation interpolation;
    private float blendLower;
    private float blendUpper;
    private float blendRange;
    
    public ContinentLerper2(Populator lower, Populator upper, float min, float max) {
        this(lower, upper, min, max, Interpolation.LINEAR);
    }
    
    public ContinentLerper2(Populator lower, Populator upper, float min, float max, Interpolation interpolation) {
        this.lower = lower;
        this.upper = upper;
        this.interpolation = interpolation;
        this.blendLower = min;
        this.blendUpper = max;
        this.blendRange = this.blendUpper - this.blendLower;
    }
    
    @Override
    public void apply(Cell cell, float x, float y, int seed) {
        if (cell.continentEdge < this.blendLower) {
            this.lower.apply(cell, x, y, seed);
            return;
        }
        if (cell.continentEdge > this.blendUpper) {
            this.upper.apply(cell, x, y, seed);
            return;
        }
        float alpha = this.interpolation.apply((cell.continentEdge - this.blendLower) / this.blendRange);
        this.lower.apply(cell, x, y, seed);
        float lowerVal = cell.value;
        this.upper.apply(cell, x, y, seed);
        float upperVal = cell.value;
        cell.value = NoiseUtil.lerp(lowerVal, upperVal, alpha);
    }
}
