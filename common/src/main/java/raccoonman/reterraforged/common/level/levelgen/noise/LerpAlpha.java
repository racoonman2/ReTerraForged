package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record LerpAlpha(Noise lower, Noise upper, Noise alpha, Noise min, Noise max) implements Noise {
	public static final Codec<LerpAlpha> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(LerpAlpha::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(LerpAlpha::upper),
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(LerpAlpha::alpha),
		Noise.HOLDER_HELPER_CODEC.fieldOf("min").forGetter(LerpAlpha::min),
		Noise.HOLDER_HELPER_CODEC.fieldOf("max").forGetter(LerpAlpha::max)		
	).apply(instance, LerpAlpha::new));
	
	@Override
	public Codec<LerpAlpha> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		float lower = this.lower.compute(x, y, seed);
		float upper = this.upper.compute(x, y, seed);
		float alpha = this.alpha.compute(x, y, seed);
		float min = this.min.compute(x, y, seed);
		float max = this.max.compute(x, y, seed);
		return NoiseUtil.lerp(lower, upper, (alpha - min) / (max - min));
	}
}
