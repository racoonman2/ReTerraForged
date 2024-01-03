package raccoonman.reterraforged.world.worldgen.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.module.Noise.Visitor;

public record AddWarp(Domain input1, Domain input2) implements Domain {
	public static final Codec<AddWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("input1").forGetter(AddWarp::input1),
		Domain.CODEC.fieldOf("input2").forGetter(AddWarp::input2)
	).apply(instance, AddWarp::new));
	
	@Override
	public float getOffsetX(float x, float z, int seed) {
		return this.input1.getOffsetX(x, z, seed) + this.input2.getOffsetX(x, z, seed);
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
		return this.input1.getOffsetZ(x, z, seed) + this.input2.getOffsetZ(x, z, seed);
	}

	@Override
	public Domain mapAll(Visitor visitor) {
		return new AddWarp(this.input1.mapAll(visitor), this.input2.mapAll(visitor));
	}

	@Override
	public Codec<AddWarp> codec() {
		return CODEC;
	}
}
