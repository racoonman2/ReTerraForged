package raccoonman.reterraforged.world.worldgen.densityfunction;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public interface MarkerFunction extends DensityFunction.SimpleFunction {

	@Override
	default double compute(FunctionContext ctx) {
		throw new UnsupportedOperationException();
	}
		
	@Override
	default double minValue() {
		return Float.NEGATIVE_INFINITY;
	}

	@Override
	default double maxValue() {
		return Float.POSITIVE_INFINITY;
	}
	
	public interface Mapped extends DensityFunction.SimpleFunction {

		@Override
		default KeyDispatchDataCodec<NoiseSampler> codec() {
			throw new UnsupportedOperationException();
		}
	}
}
