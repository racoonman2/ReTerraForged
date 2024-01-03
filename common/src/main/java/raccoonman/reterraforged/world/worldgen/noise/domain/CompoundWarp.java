package raccoonman.reterraforged.world.worldgen.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.module.Noise.Visitor;

public record CompoundWarp(Domain input1, Domain input2) implements Domain {
	public static final Codec<CompoundWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("input1").forGetter(CompoundWarp::input1),
		Domain.CODEC.fieldOf("input2").forGetter(CompoundWarp::input2)
	).apply(instance, CompoundWarp::new));
	
	@Override
	public float getOffsetX(float x, float z, int seed) {
        float ax = this.input1.getX(x, z, seed);
        float ay = this.input1.getZ(x, z, seed);
        return this.input2.getOffsetX(ax, ay, seed);
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
        float ax = this.input1.getX(x, z, seed);
        float ay = this.input1.getZ(x, z, seed);
        return this.input2.getOffsetZ(ax, ay, seed);
	}

	@Override
	public Domain mapAll(Visitor visitor) {
		return new CompoundWarp(this.input1.mapAll(visitor), this.input2.mapAll(visitor));
	}

	@Override
	public Codec<CompoundWarp> codec() {
		return CODEC;
	}
}
