package raccoonman.reterraforged.world.worldgen.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise.Visitor;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

record DomainWarp(Noise x, Noise z, Noise mappedX, Noise mappedZ, Noise distance) implements Domain {
	public static final Codec<DomainWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("x").forGetter(DomainWarp::x),
		Noise.HOLDER_HELPER_CODEC.fieldOf("z").forGetter(DomainWarp::z),
		Noise.HOLDER_HELPER_CODEC.fieldOf("distance").forGetter(DomainWarp::distance)
	).apply(instance, DomainWarp::new));
	
	public DomainWarp(Noise x, Noise z, Noise distance) {
		this(x, z, map(x), map(z), distance);
	}
	
	@Override
	public float getOffsetX(float x, float z, int seed) {
		return this.mappedX.compute(x, z, seed) * this.distance.compute(x, z, seed);
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
		return this.mappedZ.compute(x, z, seed) * this.distance.compute(x, z, seed);
	}

	@Override
	public Domain mapAll(Visitor visitor) {
		return new DomainWarp(this.x.mapAll(visitor), this.z.mapAll(visitor), this.mappedX.mapAll(visitor), this.mappedZ.mapAll(visitor), this.distance.mapAll(visitor));
	}

	@Override
	public Codec<DomainWarp> codec() {
		return CODEC;
	}
	
    private static Noise map(Noise in) {
    	if (in.minValue() == -0.5F && in.maxValue() == 0.5F) {
    		return in;
    	}
    	return Noises.map(in, -0.5F, 0.5F);
    }
}
