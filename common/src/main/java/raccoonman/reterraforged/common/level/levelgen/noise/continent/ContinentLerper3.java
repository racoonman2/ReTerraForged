// 
// Decompiled by Procyon v0.5.36
// 

package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record ContinentLerper3(Noise continent, Noise lower, Noise middle, Noise upper, Noise blendLower, Noise midpoint, Noise blendUpper, Interpolation interpolation) implements Noise {
    public static final Codec<ContinentLerper3> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Noise.HOLDER_HELPER_CODEC.fieldOf("continent").forGetter(ContinentLerper3::continent),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(ContinentLerper3::lower),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("middle").forGetter(ContinentLerper3::middle),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(ContinentLerper3::upper),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("blend_lower").forGetter(ContinentLerper3::blendLower),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("midpoint").forGetter(ContinentLerper3::midpoint),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("blend_upper").forGetter(ContinentLerper3::blendUpper),
    	Interpolation.CODEC.fieldOf("interpolation").forGetter(ContinentLerper3::interpolation)
    ).apply(instance, ContinentLerper3::new));

    @Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ContinentLerper3(this.continent.mapAll(visitor), this.lower.mapAll(visitor), this.middle.mapAll(visitor), this.upper.mapAll(visitor), this.blendLower.mapAll(visitor), this.midpoint.mapAll(visitor), this.blendUpper.mapAll(visitor), this.interpolation));
	}

    @Override
    public float compute(float x, float y, int seed) {
    	float blendLower = this.blendLower.compute(x, y, seed);
    	float blendUpper = this.blendUpper.compute(x, y, seed);
    	float midpoint = this.midpoint.compute(x, y, seed);
    	float select = this.continent.compute(x, y, seed);
        
    	if (select < blendLower) {
            return this.lower.compute(x, y, seed);
        }
        if (select > blendUpper) {
            return this.upper.compute(x, y, seed);
        }
        if (select < midpoint) {
            float alpha = this.interpolation.apply((select - blendLower) / (midpoint - blendLower));
            float lowerVal = this.lower.compute(x, y, seed);
            return NoiseUtil.lerp(lowerVal, this.middle.compute(x, y, seed), alpha);
        } else {
            float alpha = this.interpolation.apply((select - midpoint) / (blendUpper - midpoint));
            float lowerVal = this.middle.compute(x, y, seed);
            return NoiseUtil.lerp(lowerVal, this.upper.compute(x, y, seed), alpha);
        }
    }
    
	@Override
	public Codec<ContinentLerper3> codec() {
		return CODEC;
	}
}
