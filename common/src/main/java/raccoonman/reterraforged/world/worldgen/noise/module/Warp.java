package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.domain.Domain;

record Warp(Noise input, Domain domain) implements Noise {
	public static final Codec<Warp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Warp::input),
		Domain.CODEC.fieldOf("domain").forGetter(Warp::domain)
	).apply(instance, Warp::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return this.input.compute(this.domain.getX(x, z, seed), this.domain.getZ(x, z, seed), seed);
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
		return visitor.apply(new Warp(this.input.mapAll(visitor), this.domain.mapAll(visitor)));
	}

	@Override
	public Codec<Warp> codec() {
		return CODEC;
	}
}
