package raccoonman.reterraforged.common.level.levelgen.noise.density;

import com.mojang.serialization.Codec;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public interface MarkerDensityFunction extends DensityFunction.SimpleFunction {

	@Override
	default double compute(FunctionContext var1) {
		throw new UnsupportedOperationException();
	}

	@Override
	default KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return new KeyDispatchDataCodec<>(this.markerCodec());
	}
	
	Codec<? extends DensityFunction> markerCodec();
}
