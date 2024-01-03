package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record Boost(Noise input, int iterations) implements Noise {
	public static final Codec<Boost> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Boost::input),
		Codec.INT.fieldOf("iterations").forGetter(Boost::iterations)
	).apply(instance, Boost::new));
	
	public Boost {
		input = Noises.map(input, 0.0F, 1.0F);
		iterations = Math.max(1, iterations);
	}
	
	@Override
	public float compute(float x, float z, int seed) {
		float value = this.input.compute(x, z, seed);
        for (int i = 0; i < this.iterations; ++i) {
            value = NoiseUtil.pow(value, 1.0F - value);
        }
        return value;
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
		return visitor.apply(new Boost(this.input.mapAll(visitor), this.iterations));
	}

	@Override
	public Codec<Boost> codec() {
		return CODEC;
	}
}
