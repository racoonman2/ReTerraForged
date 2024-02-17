package raccoonman.reterraforged.world.worldgen.terrain;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public class MountainChainPopulator implements CellPopulator {
	private CellPopulator lower;
	private CellPopulator upper;
	private float blendLower;
	private float blendUpper;
	private float blendRange;
	
	public MountainChainPopulator(CellPopulator lower, CellPopulator upper, float min, float max) {
		this.lower = lower;
		this.upper = upper;
		this.blendLower = min;
		this.blendUpper = max;
		this.blendRange = this.blendUpper - this.blendLower;
	}

	@Override
	public void apply(Cell cell, float x, float y) {
		float select = cell.mountainChainAlpha;

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
		float lowerHeight = cell.height;
		float lowerErosion = cell.erosion;
		float lowerWeirdness = cell.weirdness;
		this.upper.apply(cell, x, y);
		float upperHeight = cell.height;
		float upperErosion = cell.erosion;
		float upperWeirdness = cell.weirdness;
		cell.height = NoiseUtil.lerp(lowerHeight, upperHeight, alpha);
		cell.erosion = NoiseUtil.lerp(lowerErosion, upperErosion, alpha);
		cell.weirdness = NoiseUtil.lerp(lowerWeirdness, upperWeirdness, alpha);
	}

    @Override
    public CellPopulator mapNoise(Noise.Visitor visitor) {
    	return new MountainChainPopulator(this.lower.mapNoise(visitor), this.upper.mapNoise(visitor), this.blendLower, this.blendUpper);
    }
}
