package raccoonman.reterraforged.world.worldgen.surface.condition;

import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

abstract class ThresholdCondition extends CellCondition {
	private float threshold;
	private Noise variance;
	
	public ThresholdCondition(Context context, float threshold, Noise variance) {
		super(context);
		
		this.threshold = threshold;
		this.variance = variance;
	}

	@Override
	public boolean test(Cell cell, int x, int z) {
		return this.sample(cell) + this.variance.compute(x, z, 0) > this.threshold;
	}
	
	protected abstract float sample(Cell cell);
}
