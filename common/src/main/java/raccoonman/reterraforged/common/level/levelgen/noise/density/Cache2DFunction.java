package raccoonman.reterraforged.common.level.levelgen.noise.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record Cache2DFunction(int startX, int startZ, int width, float min, float max, float[] values, DensityFunction source) implements DensityFunction.SimpleFunction {

	@Override
	public double compute(FunctionContext ctx) {
		int index = (ctx.blockZ() - this.startZ) * this.width + (ctx.blockX() - this.startX);
		return index >= 0 && index < this.values.length ? this.values[index] : this.source.compute(ctx);
	}

	@Override
	public double minValue() {
		return this.min;
	}

	@Override
	public double maxValue() {
		return this.max;
	}

	@Override
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		throw new UnsupportedOperationException();
	}

	public record Marker(DensityFunction function) implements DensityFunction.SimpleFunction {
		public static final Codec<Cache2DFunction.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(Cache2DFunction.Marker::function)
		).apply(instance, Cache2DFunction.Marker::new));
		
		@Override
		public double compute(FunctionContext ctx) {
			return this.function.compute(ctx);
		}

		@Override
		public double minValue() {
			return this.function.minValue();
		}

		@Override
		public double maxValue() {
			return this.function.maxValue();
		}

        @Override
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new Marker(this.function.mapAll(visitor)));
        }

		@Override
		public KeyDispatchDataCodec<Cache2DFunction.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}
