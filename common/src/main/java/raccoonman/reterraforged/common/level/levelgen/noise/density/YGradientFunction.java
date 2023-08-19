package raccoonman.reterraforged.common.level.levelgen.noise.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public record YGradientFunction(DensityFunction y) implements DensityFunction.SimpleFunction {
	public static final Codec<YGradientFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("y").forGetter(YGradientFunction::y)
	).apply(instance, YGradientFunction::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		int y = ctx.blockY();
		int solidY = NoiseUtil.floor((float) this.y.compute(ctx));
		int top = solidY;
		return  (float) (y < top + 1 ? this.y.maxValue() - ((double) y / top) : this.y.minValue());
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new YGradientFunction(this.y.mapAll(visitor)));
	}

	@Override
	public double minValue() {
		return this.y.minValue();
	}

	@Override
	public double maxValue() {
		return this.y.maxValue();
	}

	@Override
	public KeyDispatchDataCodec<YGradientFunction> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
