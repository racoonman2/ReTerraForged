package raccoonman.reterraforged.common.level.levelgen.densityfunctions;

import net.minecraft.world.level.levelgen.DensityFunction;

public class MutablePointContext implements DensityFunction.FunctionContext {
	public int x;
	public int y;
	public int z;
	
	@Override
	public int blockX() {
		return this.x;
	}

	@Override
	public int blockY() {
		return this.y;
	}

	@Override
	public int blockZ() {
		return this.z;
	}
}
