package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record Gradient(Noise input, Noise lower, Noise upper, Noise strength) implements Noise {
	public static final Codec<Gradient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Gradient::input),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(Gradient::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(Gradient::upper),
		Noise.HOLDER_HELPER_CODEC.fieldOf("strength").forGetter(Gradient::strength)
	).apply(instance, Gradient::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float noiseValue = this.input.compute(x, z, seed);
        float upperBound = this.upper.compute(x, z, seed);
        if (noiseValue > upperBound) {
            return noiseValue;
        }
        float amount = this.strength.compute(x, z, seed);
        float lowerBound = this.lower.compute(x, z, seed);
        if (noiseValue < lowerBound) {
            return NoiseUtil.pow(noiseValue, 1.0F - amount);
        }
        float alpha = 1.0F - (noiseValue - lowerBound) / (upperBound - lowerBound);
        float power = 1.0F - amount * alpha;
        return NoiseUtil.pow(noiseValue, power);
	}

	@Override
	public float minValue() {
		return this.input.minValue();
	}

	@Override
	public float maxValue() {
		return this.input.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Gradient(this.input.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.strength.mapAll(visitor)));
	}

	@Override
	public Codec<Gradient> codec() {
		return CODEC;
	}
}
