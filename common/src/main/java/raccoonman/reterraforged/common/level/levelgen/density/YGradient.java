package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

//TODO don't hardcode min / max values
public record YGradient(DensityFunction y, DensityFunction scale, int gradientSize) implements DensityFunction.SimpleFunction {
	public static final Codec<YGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("y").forGetter(YGradient::y),
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("scale").forGetter(YGradient::scale),
		Codec.INT.fieldOf("gradient_size").forGetter(YGradient::gradientSize)
	).apply(instance, YGradient::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		double scale = this.scale.compute(ctx);

		double blockY = ctx.blockY();
		double noiseY = NoiseUtil.floor(this.y.compute(ctx) * scale);
		double delta = noiseY - blockY;
		return (delta / this.gradientSize) / scale; // TODO: don't hardcode this value either
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new YGradient(this.y.mapAll(visitor), this.scale.mapAll(visitor), this.gradientSize));
	}

	@Override
	public double minValue() {
		return -1.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}

	@Override
	public KeyDispatchDataCodec<YGradient> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
