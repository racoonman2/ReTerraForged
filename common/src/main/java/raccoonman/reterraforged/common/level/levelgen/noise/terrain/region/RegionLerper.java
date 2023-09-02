package raccoonman.reterraforged.common.level.levelgen.noise.terrain.region;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record RegionLerper(Noise alpha, Noise lower, Noise upper) implements Noise {
	public static final Codec<RegionLerper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(RegionLerper::alpha),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(RegionLerper::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(RegionLerper::upper)
	).apply(instance, RegionLerper::new));

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new RegionLerper(this.alpha, this.lower, this.upper));
	}
	
	@Override
	public Codec<RegionLerper> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        final float alpha = this.alpha.compute(x, y, seed);
        if (alpha == 0.0F) {
            return this.lower.compute(x, y, seed);
        }
        if (alpha == 1.0F) {
            return this.upper.compute(x, y, seed);
        }
        float lowerValue = this.lower.compute(x, y, seed);
        float upperValue = this.upper.compute(x, y, seed);
        return NoiseUtil.lerp(lowerValue, upperValue, alpha);
	}
}
