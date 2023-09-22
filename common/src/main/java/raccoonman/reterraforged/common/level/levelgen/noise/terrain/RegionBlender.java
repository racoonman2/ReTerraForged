package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;

public record RegionBlender(Noise control, Noise lower, Noise upper, float blendLower, float blendUpper) implements Noise {
	public static final Codec<RegionBlender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("control").forGetter(RegionBlender::control),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(RegionBlender::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(RegionBlender::upper),
		Codec.FLOAT.fieldOf("blend_lower").forGetter(RegionBlender::blendLower),
		Codec.FLOAT.fieldOf("blend_upper").forGetter(RegionBlender::blendUpper)		
	).apply(instance, RegionBlender::new));
	
	@Override
	public Codec<RegionBlender> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        float control = this.control.compute(x, y, seed);
        if (control < this.blendLower) {
        	return this.lower.compute(x, y, seed);
        }
        if (control > this.blendUpper) {
        	return this.upper.compute(x, y, seed);
        }
        float alpha = Interpolation.LINEAR.apply((control - this.blendLower) / (this.blendUpper - this.blendLower));
        float lowerVal = this.lower.compute(x, y, seed);
        float upperVal = this.upper.compute(x, y, seed);
        return NoiseUtil.lerp(lowerVal, upperVal, alpha);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new RegionBlender(this.control.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.blendLower, this.blendUpper));
	}
}
