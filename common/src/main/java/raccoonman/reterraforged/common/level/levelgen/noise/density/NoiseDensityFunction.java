package raccoonman.reterraforged.common.level.levelgen.noise.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.noise.Noise;

public record NoiseDensityFunction(Noise noise, int seed) implements DensityFunction.SimpleFunction {

	@Override
	public double compute(FunctionContext ctx) {
		return this.noise.getValue(ctx.blockX(), ctx.blockZ(), this.seed);
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
		public static final Codec<NoiseDensityFunction.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Noise.DIRECT_CODEC.fieldOf("noise").forGetter(NoiseDensityFunction.Marker::noise)
		).apply(instance, NoiseDensityFunction.Marker::new));

		@Override
		public double compute(FunctionContext ctx) {
			throw new UnsupportedOperationException();
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
		public KeyDispatchDataCodec<NoiseDensityFunction.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
