package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record Steepness(DensityFunction height, float minHeight, float scaler, int radius) implements DensityFunction {
	public static final Codec<Steepness> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("height").forGetter(Steepness::height),
		Codec.FLOAT.fieldOf("min_height").forGetter(Steepness::minHeight),
		Codec.FLOAT.fieldOf("scaler").forGetter(Steepness::scaler),
		Codec.INT.fieldOf("radius").forGetter(Steepness::radius)
	).apply(instance, Steepness::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		return this.compute(ctx, new MutableFunctionContext());
	}

	@Override
	public void fillArray(double[] data, ContextProvider provider) {
	}

	@Override
	public DensityFunction mapAll(Visitor v) {
		return v.apply(new Steepness(this.height.mapAll(v), this.minHeight, this.scaler, this.radius));
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
	public KeyDispatchDataCodec<Steepness> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	public Steepness.Cached cache() {
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
			return Steepness.this.compute(ctx, this.ctx);
		}
		@Override
		public DensityFunction mapAll(Visitor v) {
			return v.apply(new Steepness.Cached(this.height.mapAll(v)));
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
