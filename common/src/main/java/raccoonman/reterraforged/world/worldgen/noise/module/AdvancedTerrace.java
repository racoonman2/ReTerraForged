package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record AdvancedTerrace(Noise source, Noise modulation, Noise mask, Noise slope, float blendMin, float blendMax, int steps, int octaves) implements Noise {
	public static final Codec<AdvancedTerrace> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(AdvancedTerrace::source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("modulation").forGetter(AdvancedTerrace::modulation),
		Noise.HOLDER_HELPER_CODEC.fieldOf("mask").forGetter(AdvancedTerrace::mask),
		Noise.HOLDER_HELPER_CODEC.fieldOf("slope").forGetter(AdvancedTerrace::slope),
		Codec.FLOAT.fieldOf("blend_min").forGetter(AdvancedTerrace::blendMin),
		Codec.FLOAT.fieldOf("blend_max").forGetter(AdvancedTerrace::blendMax),
		Codec.INT.fieldOf("steps").forGetter(AdvancedTerrace::steps),
		Codec.INT.fieldOf("octaves").forGetter(AdvancedTerrace::octaves)
	).apply(instance, AdvancedTerrace::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float value = this.source.compute(x, z, seed);
        if (value <= this.blendMin) {
            return value;
        }
        float mask = this.mask.compute(x, z, seed);
        if (mask == 0.0F) {
            return value;
        }
        float result = value;
        float slope = this.slope.compute(x, z, seed);
        float modulation = this.modulation.compute(x, z, seed);
        for (int i = 1; i <= this.octaves; ++i) {
            result = this.getStepped(result, this.steps * i);
            result = this.getSloped(value, result, slope);
        }
        result = this.getModulated(result, modulation);
        float alpha = this.getAlpha(value);
        if (mask != 1.0F) {
            alpha *= mask;
        }
        return NoiseUtil.lerp(value, result, alpha);
	}

	@Override
	public float minValue() {
		return this.source.minValue();
	}

	@Override
	public float maxValue() {
		return this.source.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return new AdvancedTerrace(this.source.mapAll(visitor), this.modulation.mapAll(visitor), this.mask.mapAll(visitor), this.slope.mapAll(visitor), this.blendMin, this.blendMax, this.steps, this.octaves);
	}

	@Override
	public Codec<AdvancedTerrace> codec() {
		return CODEC;
	}
	
    private float getModulated(float value, float modulation) {
        return (value + modulation) / (this.source.maxValue() + this.modulation.maxValue());
    }
    
    private float getStepped(float value, int steps) {
        value = (float) NoiseUtil.round(value * steps);
        return value / steps;
    }
    
    private float getSloped(float value, float stepped, float slope) {
        final float delta = value - stepped;
        final float amount = delta * slope;
        return stepped + amount;
    }
    
    private float getAlpha(float value) {
        if (value > this.blendMax) {
            return 1.0F;
        }
        return (value - this.blendMin) / (this.blendMax - this.blendMin);
    }
}
