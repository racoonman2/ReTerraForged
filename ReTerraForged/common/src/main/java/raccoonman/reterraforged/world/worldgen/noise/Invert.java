package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Invert(Noise input) implements Noise {
	public static final MapCodec<Invert> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
	public MapCodec<Invert> codec() {
		return CODEC;
	}
}
