package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

//TODO do block scaling here
//TODO don't hardcode min / max values
public record YGradient(DensityFunction y) implements DensityFunction.SimpleFunction {
	public static final Codec<YGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("y").forGetter(YGradient::y)
	).apply(instance, YGradient::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		int blockY = ctx.blockY();
		int noiseY = NoiseUtil.floor((float) this.y.compute(ctx));
		return blockY <= noiseY ? this.y.maxValue() - ((double) blockY / noiseY) : 0.0F;
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new YGradient(this.y.mapAll(visitor)));
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
	public KeyDispatchDataCodec<YGradient> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}
}
