package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

//TODO height shouldn't be a holder but this can really only effectively be used on a cache
public record Slope(Holder<DensityFunction> height, float minHeight, float scaler, int radius) implements DensityFunction {
	public static final Codec<Slope> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.CODEC.fieldOf("height").forGetter(Slope::height),
		Codec.FLOAT.fieldOf("min_height").forGetter(Slope::minHeight),
		Codec.FLOAT.fieldOf("scaler").forGetter(Slope::scaler),
		Codec.INT.fieldOf("radius").forGetter(Slope::radius)
	).apply(instance, Slope::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		return this.compute(ctx, new MutableFunctionContext());
	}

	@Override
	public void fillArray(double[] data, ContextProvider provider) {
	}

	@Override
	public DensityFunction mapAll(Visitor v) {
		return v.apply(new Slope(Holder.direct(this.height.value().mapAll(v)), this.minHeight, this.scaler, this.radius));
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}

	@Override
	public KeyDispatchDataCodec<Slope> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
	
	public Slope.Cached cache() {
		return new Cached(this.height.value());
	}

	private double compute(FunctionContext ctx, MutableFunctionContext offsetCtx) {
		int cx = ctx.blockX();
		int cz = ctx.blockZ();
		double totalHeightDif = 0.0D;
		DensityFunction function = this.height.value();
        double height = function.compute(ctx);
        for (int dz = -1; dz <= 2; ++dz) {
            for (int dx = -1; dx <= 2; ++dx) {
                if (dx != 0 || dz != 0) {
                    int x = cx + dx * this.radius;
                    int z = cz + dz * this.radius;
                    offsetCtx.blockX = x;
                    offsetCtx.blockZ = z;
                    double neighborHeight = function.compute(offsetCtx);
                    neighborHeight = Math.max(neighborHeight, this.minHeight);
                    totalHeightDif += Math.abs(height - neighborHeight) / this.radius;
                }
            }
        }
        return Math.min(1.0D, totalHeightDif * this.scaler);
	}
	
	private class Cached implements DensityFunction.SimpleFunction {
		private MutableFunctionContext ctx;
		
		public Cached(DensityFunction height) {
			this.ctx = new MutableFunctionContext();
		}
		
		@Override
		public double compute(FunctionContext ctx) {
			return Slope.this.compute(ctx, this.ctx);
		}
		@Override
		public DensityFunction mapAll(Visitor v) {
			return v.apply(new Slope.Cached(Slope.this.height.value().mapAll(v)));
		}

		@Override
		public double minValue() {
			return 0.0D;
		}

		@Override
		public double maxValue() {
			return 1.0D;
		}

		@Override
		public KeyDispatchDataCodec<Cached> codec() {
			throw new UnsupportedOperationException();
		}
	}
}
