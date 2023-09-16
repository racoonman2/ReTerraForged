package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record XZGradient(DensityFunction height, float minHeight, float scaler, int radius) implements DensityFunction {
	public static final Codec<XZGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("height").forGetter(XZGradient::height),
		Codec.FLOAT.fieldOf("min_height").forGetter(XZGradient::minHeight),
		Codec.FLOAT.fieldOf("scaler").forGetter(XZGradient::scaler),
		Codec.INT.fieldOf("radius").forGetter(XZGradient::radius)
	).apply(instance, XZGradient::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		return this.compute(ctx, new MutableFunctionContext());
	}

	@Override
	public void fillArray(double[] data, ContextProvider provider) {
	}

	@Override
	public DensityFunction mapAll(Visitor v) {
		return v.apply(new XZGradient(this.height.mapAll(v), this.minHeight, this.scaler, this.radius));
	}

	@Override
	public double minValue() {
		return this.height.minValue();
	}

	@Override
	public double maxValue() {
		return this.height.maxValue();
	}

	@Override
	public KeyDispatchDataCodec<XZGradient> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	public XZGradient.Cached cache() {
		return new Cached(this.height);
	}

	private double compute(FunctionContext ctx, MutableFunctionContext offsetCtx) {
		int cx = ctx.blockX();
		int cz = ctx.blockZ();
		double totalHeightDif = 0.0D;
        double height = this.height.compute(ctx);
        for (int dz = -1; dz <= 2; ++dz) {
            for (int dx = -1; dx <= 2; ++dx) {
                if (dx != 0 || dz != 0) {
                    int x = cx + dx * this.radius;
                    int z = cz + dz * this.radius;
                    offsetCtx.blockX = x;
                    offsetCtx.blockZ = z;
                    double neighborHeight = this.height.compute(offsetCtx);
                    neighborHeight = Math.max(neighborHeight, this.minHeight);
                    totalHeightDif += Math.abs(height - neighborHeight) / this.radius;
                }
            }
        }
        return Math.min(1.0D, totalHeightDif * this.scaler);
	}
	
	private class Cached implements DensityFunction.SimpleFunction {
		private DensityFunction height;
		private MutableFunctionContext ctx;
		
		public Cached(DensityFunction height) {
			this.height = height;
			this.ctx = new MutableFunctionContext();
		}
		
		@Override
		public double compute(FunctionContext ctx) {
			return XZGradient.this.compute(ctx, this.ctx);
		}
		@Override
		public DensityFunction mapAll(Visitor v) {
			return v.apply(new XZGradient.Cached(this.height.mapAll(v)));
		}

		@Override
		public double minValue() {
			return this.height.minValue();
		}

		@Override
		public double maxValue() {
			return this.height.maxValue();
		}

		@Override
		public KeyDispatchDataCodec<? extends DensityFunction> codec() {
			throw new UnsupportedOperationException();
		}
	}
}
