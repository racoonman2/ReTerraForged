package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public record BlendFunction(DensityFunction lower, DensityFunction upper, DensityFunction alpha) implements SimpleFunction {
	public static final MapCodec<BlendFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(BlendFunction::lower),
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(BlendFunction::upper),
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(BlendFunction::alpha)
	).apply(instance, BlendFunction::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		double alpha = this.alpha.compute(ctx);
        if (alpha == 0.0D) {
        	return this.lower.compute(ctx);
        }
        if (alpha == 1.0D) {
        	return this.upper.compute(ctx);
        }
        double lower = this.lower.compute(ctx);
        double upper = this.upper.compute(ctx);
        return NoiseUtil.lerp(lower, upper, alpha);
	}

	@Override
	public DensityFunction mapAll(DensityFunction.Visitor visitor) {
		return visitor.apply(new BlendFunction(this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.alpha.mapAll(visitor)));
	}
	
	@Override
	public double minValue() {
		return this.lower.minValue();
	}
	
	@Override
	public double maxValue() {
		return this.upper.maxValue();
	}

	@Override
	public KeyDispatchDataCodec<BlendFunction> codec() {
		return KeyDispatchDataCodec.of(CODEC);
	}
}
