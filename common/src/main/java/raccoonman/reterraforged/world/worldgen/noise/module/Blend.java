package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;

record Blend(Noise alpha, Noise lower, Noise upper, float mid, float range, Interpolation interpolation) implements Noise {
	public static final Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(Blend::alpha),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(Blend::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(Blend::upper),
		Codec.FLOAT.fieldOf("mid").forGetter(Blend::mid),
		Codec.FLOAT.fieldOf("range").forGetter(Blend::range),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(Blend::interpolation)
	).apply(instance, Blend::new));
	
	@Override
	public float minValue() {
		return Math.min(this.lower.minValue(), this.upper.minValue());
	}

	@Override
	public float maxValue() {
		return Math.max(this.lower.maxValue(), this.upper.maxValue());
	}
	
	@Override
	public float compute(float x, float z, int seed) {
        float mid = this.alpha.minValue() + (this.alpha.maxValue() - this.alpha.minValue()) * this.mid;
        float blendLower = Math.max(this.alpha.minValue(), mid - this.range / 2.0F);
        float blendUpper = Math.min(this.alpha.maxValue(), mid + this.range / 2.0F);
        float blendRange = blendUpper - blendLower;
		float alpha = this.alpha.compute(x, z, seed);
        if (alpha < blendLower) {
            return this.lower.compute(x, z, seed);
        }
        if (alpha > blendUpper) {
            return this.upper.compute(x, z, seed);
        }
        return NoiseUtil.lerp(this.lower.compute(x, z, seed), this.upper.compute(x, z, seed), this.interpolation.apply((alpha - blendLower) / blendRange));
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Blend(this.alpha.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.mid, this.range, this.interpolation));
	}

	@Override
	public Codec<Blend> codec() {
		return CODEC;
	}
}
