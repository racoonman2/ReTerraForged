// 
// Decompiled by Procyon v0.5.36
// 

package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record ContinentLerper3(Noise continent, Noise lower, Noise middle, Noise upper, float blendLower, float midpoint, float blendUpper, Interpolation interpolation) implements Noise {
    public static final Codec<ContinentLerper3> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Noise.HOLDER_HELPER_CODEC.fieldOf("continent").forGetter(ContinentLerper3::continent),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(ContinentLerper3::lower),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("middle").forGetter(ContinentLerper3::middle),
    	Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(ContinentLerper3::upper),
    	Codec.FLOAT.fieldOf("blend_lower").forGetter(ContinentLerper3::blendLower),
    	Codec.FLOAT.fieldOf("midpoint").forGetter(ContinentLerper3::midpoint),
    	Codec.FLOAT.fieldOf("blend_upper").forGetter(ContinentLerper3::blendUpper),
    	Interpolation.CODEC.fieldOf("interpolation").forGetter(ContinentLerper3::interpolation)
    ).apply(instance, ContinentLerper3::new));

    @Override
    public float compute(float x, float y, int seed) {
        float select = this.continent.compute(x, y, seed);
        if (select < this.blendLower) {
            return this.lower.compute(x, y, seed);
        }
        if (select > this.blendUpper) {
            return this.upper.compute(x, y, seed);
        }
        if (select < this.midpoint) {
            float alpha = this.interpolation.apply((select - this.blendLower) / (this.midpoint - this.blendLower));
            float lowerVal = this.lower.compute(x, y, seed);
            return NoiseUtil.lerp(lowerVal, this.middle.compute(x, y, seed), alpha);
        } else {
            float alpha = this.interpolation.apply((select - this.midpoint) / (this.blendUpper - this.midpoint));
            float lowerVal = this.middle.compute(x, y, seed);
            return NoiseUtil.lerp(lowerVal, this.upper.compute(x, y, seed), alpha);
        }
    }
    
	@Override
	public Codec<ContinentLerper3> codec() {
		return CODEC;
	}
}
