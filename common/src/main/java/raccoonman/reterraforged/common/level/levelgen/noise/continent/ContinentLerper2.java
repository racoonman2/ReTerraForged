package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;

public record ContinentLerper2(Noise continentEdge, Noise lower, Noise upper, float blendLower, float blendUpper, Interpolation interpolation) implements Noise {
	public static final Codec<ContinentLerper2> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("continent_edge").forGetter(ContinentLerper2::continentEdge),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(ContinentLerper2::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(ContinentLerper2::upper),
		Codec.FLOAT.fieldOf("blend_lower").forGetter(ContinentLerper2::blendLower),
		Codec.FLOAT.fieldOf("blend_upper").forGetter(ContinentLerper2::blendUpper),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(ContinentLerper2::interpolation)
	).apply(instance, ContinentLerper2::new));
	
	@Override
	public Codec<ContinentLerper2> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		float continentEdge = this.continentEdge.compute(x, y, seed);
        if (continentEdge < this.blendLower) {
        	return this.lower.compute(x, y, seed);
        }
        if (continentEdge > this.blendUpper) {
        	return this.upper.compute(x, y, seed);
        }
        float alpha = this.interpolation.apply((continentEdge - this.blendLower) / (this.blendUpper - this.blendLower));
        float lowerVal = this.lower.compute(x, y, seed);
        float upperVal = this.upper.compute(x, y, seed);
        return NoiseUtil.lerp(lowerVal, upperVal, alpha);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ContinentLerper2(this.continentEdge.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.blendLower, this.blendUpper, this.interpolation));
	}
}
