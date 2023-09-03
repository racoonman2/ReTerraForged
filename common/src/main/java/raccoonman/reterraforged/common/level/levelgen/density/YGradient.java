package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

//TODO do block scaling here
//TODO don't hardcode min / max values
public record YGradient(DensityFunction y, int surfaceExtent) implements DensityFunction.SimpleFunction {
	public static final Codec<YGradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("y").forGetter(YGradient::y),
		Codec.INT.fieldOf("surface_extent").forGetter(YGradient::surfaceExtent)
	).apply(instance, YGradient::new));
	
	@Override
	public double compute(FunctionContext ctx) {
		double blockY = ctx.blockY();
		double noiseY = NoiseUtil.floor((float) this.y.compute(ctx));
		return blockY > noiseY - this.surfaceExtent && blockY <= noiseY ? (noiseY - blockY) / this.surfaceExtent : blockY < noiseY ? 1.0F : 0.0F;
//		return blockY <= noiseY ? 1.0F - (blockY / noiseY) : this.above.compute(ctx);
	}

	private static float gradient() {
		return 0;
	}
	
	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new YGradient(this.y.mapAll(visitor), this.surfaceExtent));
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
