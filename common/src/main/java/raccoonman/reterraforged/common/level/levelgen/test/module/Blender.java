package raccoonman.reterraforged.common.level.levelgen.test.module;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;

public class Blender extends Select implements Populator {
	private Populator lower;
	private Populator upper;
	private float blendLower;
	private float blendUpper;
	private float blendRange;
	private float midpoint;

	public Blender(Noise control, Populator lower, Populator upper, float min, float max, float split) {
		super(control);
		this.lower = lower;
		this.upper = upper;
		this.blendLower = min;
		this.blendUpper = max;
		this.blendRange = this.blendUpper - this.blendLower;
		this.midpoint = this.blendLower + this.blendRange * split;
	}

	@Override
	public void apply(Cell cell, float x, float y, int seed) {
		float select = this.getSelect(cell, x, y, seed);
		if (select < this.blendLower) {
			this.lower.apply(cell, x, y, seed);
			return;
		}
		if (select > this.blendUpper) {
			this.upper.apply(cell, x, y, seed);
			return;
		}
		float alpha = Interpolation.LINEAR.apply((select - this.blendLower) / this.blendRange);
		this.lower.apply(cell, x, y, seed);
		float lowerVal = cell.value;
		Terrain lowerType = cell.terrain;
		this.upper.apply(cell, x, y, seed);
		float upperVal = cell.value;
		cell.value = NoiseUtil.lerp(lowerVal, upperVal, alpha);
		if (select < this.midpoint) {
			cell.terrain = lowerType;
		}
	}
}
