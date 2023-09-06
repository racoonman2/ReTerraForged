package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record NoiseWrapper(Noise noise, int seed) implements DensityFunction.SimpleFunction {

	@Override
	public double compute(FunctionContext ctx) {
		return this.noise.compute(ctx.blockX(), ctx.blockZ(), this.seed);
	}

	@Override
	public double minValue() {
		return this.noise.minValue();
	}

	@Override
	public double maxValue() {
		return this.noise.maxValue();
	}

	@Override
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		throw new UnsupportedOperationException();
	}

	public record Marker(Noise noise) implements DensityFunction.SimpleFunction {
		public static final Codec<NoiseWrapper.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Noise.HOLDER_HELPER_CODEC.fieldOf("noise").forGetter(NoiseWrapper.Marker::noise)
		).apply(instance, NoiseWrapper.Marker::new));

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
		public KeyDispatchDataCodec<NoiseWrapper.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
