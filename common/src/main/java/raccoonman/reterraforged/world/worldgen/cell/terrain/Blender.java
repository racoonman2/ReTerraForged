package raccoonman.reterraforged.world.worldgen.cell.terrain;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public class Blender implements CellPopulator {
	private Noise control;
	private CellPopulator lower;
	private CellPopulator upper;
	private float blendLower;
	private float blendUpper;
	private float blendRange;
	private float midpoint;

	public Blender(Noise control, CellPopulator lower, CellPopulator upper, float min, float max, float split) {
		this.control = control;
		this.lower = lower;
		this.upper = upper;
		this.blendLower = min;
		this.blendUpper = max;
		this.blendRange = this.blendUpper - this.blendLower;
		this.midpoint = this.blendLower + this.blendRange * split;
	}

	@Override
	public void apply(Cell cell, float x, float y) {
		float select = this.control.compute(x, y, 0);
		if (select < this.blendLower) {
			this.lower.apply(cell, x, y);
			return;
		}
		if (select > this.blendUpper) {
			this.upper.apply(cell, x, y);
			return;
		}
		float alpha = Interpolation.LINEAR.apply((select - this.blendLower) / this.blendRange);
		this.lower.apply(cell, x, y);
		float lowerVal = cell.height;
		Terrain lowerType = cell.terrain;
		this.upper.apply(cell, x, y);
		float upperVal = cell.height;
		cell.height = NoiseUtil.lerp(lowerVal, upperVal, alpha);
		if (select < this.midpoint) {
			cell.terrain = lowerType;
		}
	}
}
