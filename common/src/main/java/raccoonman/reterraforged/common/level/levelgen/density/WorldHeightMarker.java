package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record WorldHeightMarker() implements DensityFunction.SimpleFunction {
	public static final Codec<WorldHeightMarker> CODEC = Codec.unit(WorldHeightMarker::new);

	@Override
	public double compute(FunctionContext ctx) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public double minValue() {
		return Float.NEGATIVE_INFINITY;
	}

	@Override
	public double maxValue() {
		return Float.POSITIVE_INFINITY;
	}

	@Override
	public KeyDispatchDataCodec<WorldHeightMarker> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}