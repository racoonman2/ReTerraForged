package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record Blender(Noise control, Noise lower, Noise upper, float blendLower, float blendUpper, float split, Interpolation interpolation) implements Noise {
	public static final Codec<Blender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("control").forGetter(Blender::control),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(Blender::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(Blender::upper),
		Codec.FLOAT.fieldOf("blend_lower").forGetter(Blender::blendLower),
		Codec.FLOAT.fieldOf("blend_upper").forGetter(Blender::blendUpper),
		Codec.FLOAT.fieldOf("blend_split").forGetter(Blender::split),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(Blender::interpolation)
	).apply(instance, Blender::new));
	
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Blender(this.control.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.blendLower, this.blendUpper, this.split, this.interpolation));
	}
	
	@Override
	public Codec<Blender> codec() {
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
        float alpha = this.interpolation.apply((control - this.blendLower) / (this.blendUpper - this.blendLower));
        float lowerVal = this.lower.compute(x, y, seed);
        float upperVal = this.upper.compute(x, y, seed);
        return NoiseUtil.lerp(lowerVal, upperVal, alpha);
    }
}
