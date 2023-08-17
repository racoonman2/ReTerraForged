package raccoonman.reterraforged.common.level.levelgen.terrain;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public interface Terrain extends DensityFunction.SimpleFunction {
	public static final float MIN = 0.0F;
	public static final float MAX = 1.0F;
	
	NoiseLevels levels();
	
	float getBase(int x, int z);
	
	float getHeight(int x, int z);
	
	float getWater(int x, int z);
	
	float getGradient(int x, int z);
	
	default int getBaseLevel(int x, int z) {
		return NoiseUtil.floor(this.getBase(x, z));
	}
	
	default int getHeightLevel(int x, int z) {
		return NoiseUtil.floor(this.getHeight(x, z));
	}
	
	default int getWaterLevel(int x, int z) {
		return this.getWater(x, z) == 0.0F ? this.getBaseLevel(x, z) : this.levels().seaLevel();
	}
	
	default float getNormalGradient(int x, int z, float normal) {
		return NoiseUtil.clamp(this.getGradient(x, z) * normal, 0.0F, 1.0F);
	}
	
	@Override
	default double compute(FunctionContext ctx) {
		int x = ctx.blockX();
		int y = ctx.blockY();
		int z = ctx.blockZ();
		int solidY = this.getHeightLevel(x, z);
		int waterY = this.getWaterLevel(x, z);
		int top = Math.max(solidY, waterY);
		return (float) (y < top + 1 ? MAX - ((double) y / top) : MIN);
	}
	
	@Override
	default double minValue() {
		return MIN;
	}
	
	@Override
	default double maxValue() {
		return MAX;
	}

	@Override
	default KeyDispatchDataCodec<? extends DensityFunction> codec() {
		throw new UnsupportedOperationException();
	}
}
