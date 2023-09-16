package raccoonman.reterraforged.common.level.levelgen.density;

import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

public class MutableFunctionContext implements FunctionContext {
	public int blockX, blockY, blockZ;

	@Override
	public int blockX() {
		return this.blockX;
	}

	@Override
	public int blockY() {
		return this.blockY;
	}

	@Override
	public int blockZ() {
		return this.blockZ;
	}
}