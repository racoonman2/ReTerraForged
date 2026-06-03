package raccoonman.reterraforged.world.worldgen.densityfunction;

import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;
import raccoonman.reterraforged.world.worldgen.layer.terrain.Terrain;

public class TerrainFunction implements MappedFunction, SimpleFunction {
	private Terrain terrain;
	private float[] data;
	private int minBlockX;
	private int minBlockZ;
	
	public TerrainFunction(Terrain terrain, float[] data, int minBlockX, int minBlockZ) {
		this.terrain = terrain;
		this.data = data;
		this.minBlockX = minBlockX;
		this.minBlockZ = minBlockZ;
	}
	
	@Override
	public double compute(FunctionContext ctx) {
		int blockX = ctx.blockX() - this.minBlockX;
		int blockZ = ctx.blockZ() - this.minBlockZ;
		int cellIndex = this.terrain.cellIndex(blockX, blockZ);
		return Terrain.getScaledHeight(this.data[cellIndex]);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}
}
