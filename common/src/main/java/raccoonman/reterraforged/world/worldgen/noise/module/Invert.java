package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record Invert(Noise input) implements Noise {
	public static final Codec<Invert> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Invert::input)
	).apply(instance, Invert::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float min = this.input.minValue();
		float max = this.input.maxValue();
		return max - NoiseUtil.clamp(this.input.compute(x, z, seed), min, max);
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
		return visitor.apply(new Invert(this.input.mapAll(visitor)));
	}

	@Override
	public Codec<Invert> codec() {
		return CODEC;
	}
}
