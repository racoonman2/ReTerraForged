// 
// Decompiled by Procyon v0.5.36
// 

package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record ContinentLerper2(Noise continent, Noise lower, Noise upper, Noise blendLower, Noise blendUpper, Interpolation interpolation) implements Noise {
	public static final Codec<ContinentLerper2> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("continent").forGetter(ContinentLerper2::continent),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(ContinentLerper2::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(ContinentLerper2::upper),
		Noise.HOLDER_HELPER_CODEC.fieldOf("blend_lower").forGetter(ContinentLerper2::blendLower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("blend_upper").forGetter(ContinentLerper2::blendUpper),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(ContinentLerper2::interpolation)
	).apply(instance, ContinentLerper2::new));

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ContinentLerper2(this.continent.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.blendLower.mapAll(visitor), this.blendUpper.mapAll(visitor), this.interpolation));
	}
	
	@Override
	public Codec<ContinentLerper2> codec() {
		return CODEC;
	}
	
    @Override
    public float compute(float x, float y, int seed) {
    	float blendLower = this.blendLower.compute(x, y, seed);
    	float blendUpper = this.blendUpper.compute(x, y, seed);
    	float continent = this.continent.compute(x, y, seed);
        if (continent < blendLower) {
        	return this.lower.compute(x, y, seed);
        }
        if (continent > blendUpper) {
            return this.upper.compute(x, y, seed);
        }
        float alpha = this.interpolation.apply((continent - blendLower) / (blendUpper - blendLower));
        float lowerVal = this.lower.compute(x, y, seed);
        float upperVal = this.upper.compute(x, y, seed);
        return NoiseUtil.lerp(lowerVal, upperVal, alpha);
    }
}
