package raccoonman.reterraforged.world.worldgen.densityfunction;

import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;

public class MutableFunctionContext implements FunctionContext {
	private int blockX, blockY, blockZ;

	public MutableFunctionContext at(int x, int z) {
		this.blockX = x;
		this.blockZ = z;
		return this;
	}
	
	public MutableFunctionContext at(int x, int y, int z) {
		this.blockX = x;
		this.blockY = y;
		this.blockZ = z;
		return this;
	}
	
	public MutableFunctionContext at(double x, double y, double z) {
		return this.at((int) x, (int) y, (int) z);
	}
	
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