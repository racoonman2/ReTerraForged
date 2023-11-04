package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record NoiseSampler(Holder<Noise> noise, int seed) implements DensityFunction.SimpleFunction {

	@Override
	public double compute(FunctionContext ctx) {
		return this.noise.value().compute(ctx.blockX(), ctx.blockZ(), this.seed);
	}

	@Override
	public double minValue() {
		return this.noise.value().minValue();
	}

	@Override
	public double maxValue() {
		return this.noise.value().maxValue();
	}

	@Override
	public KeyDispatchDataCodec<NoiseSampler> codec() {
		throw new UnsupportedOperationException();
	}
	
	public record Marker(Holder<Noise> noise) implements DensityFunction.SimpleFunction {
		public static final Codec<NoiseSampler.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Noise.CODEC.fieldOf("noise").forGetter(NoiseSampler.Marker::noise)
		).apply(instance, NoiseSampler.Marker::new));

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
		public KeyDispatchDataCodec<NoiseSampler.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
