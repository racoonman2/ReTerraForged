package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record RegionLerper(Noise border, Noise lower, Noise upper) implements Noise {
	public static final Codec<RegionLerper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("border").forGetter(RegionLerper::border),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(RegionLerper::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(RegionLerper::upper)
	).apply(instance, RegionLerper::new));
	
	@Override
	public Codec<RegionLerper> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        float alpha = this.border.compute(x, y, seed);
        if (alpha == 0.0F) {
            this.lower.compute(x, y, seed);
        }
        if (alpha == 1.0F) {
            this.upper.compute(x, y, seed);
        }
        float lowerValue = this.lower.compute(x, y, seed);
        float upperValue = this.upper.compute(x, y, seed);
        return NoiseUtil.lerp(lowerValue, upperValue, alpha);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new RegionLerper(this.border.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor)));
	}
}
