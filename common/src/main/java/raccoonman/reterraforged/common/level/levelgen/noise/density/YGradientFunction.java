package raccoonman.reterraforged.common.level.levelgen.noise.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

//TODO don't hardcode min / max values
public record YGradientFunction(DensityFunction y) implements DensityFunction.SimpleFunction {
	public static final Codec<YGradientFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("y").forGetter(YGradientFunction::y)
	).apply(instance, YGradientFunction::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		int blockY = ctx.blockY();
		int noiseY = NoiseUtil.floor((float) this.y.compute(ctx));
		return blockY < noiseY + 1 ? 1.0F - ((double) blockY / noiseY) : 0.0F;
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new YGradientFunction(this.y.mapAll(visitor)));
	}

	@Override
	public double minValue() {
		return 0.0F;
	}

	@Override
	public double maxValue() {
		return 1.0F;
	}

	@Override
	public KeyDispatchDataCodec<YGradientFunction> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
