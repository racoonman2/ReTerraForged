package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record Clamp(Noise input, Noise min, Noise max) implements Noise {
	public static final Codec<Clamp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Clamp::input),
		Noise.HOLDER_HELPER_CODEC.fieldOf("min").forGetter(Clamp::min),
		Noise.HOLDER_HELPER_CODEC.fieldOf("max").forGetter(Clamp::max)
	).apply(instance, Clamp::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return NoiseUtil.clamp(this.input.compute(x, z, seed), this.min.compute(x, z, seed), this.max.compute(x, z, seed));
	}

	@Override
	public float minValue() {
		return this.min.minValue();
	}

	@Override
	public float maxValue() {
		return this.max.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Clamp(this.input.mapAll(visitor), this.min.mapAll(visitor), this.max.mapAll(visitor)));
	}

	@Override
	public Codec<Clamp> codec() {
		return CODEC;
	}
}
