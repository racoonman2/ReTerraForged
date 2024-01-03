package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Alpha(Noise input, Noise alpha) implements Noise {
	public static final Codec<Alpha> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Alpha::input),
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(Alpha::alpha)
	).apply(instance, Alpha::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float input = this.input.compute(x, z, seed);
		float alpha = this.alpha.compute(x, z, seed);
		return input * alpha + (1.0F - alpha);
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
		return visitor.apply(new Alpha(this.input.mapAll(visitor), this.alpha.mapAll(visitor)));
	}

	@Override
	public Codec<Alpha> codec() {
		return CODEC;
	}
}
