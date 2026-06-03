package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;
import raccoonman.reterraforged.world.worldgen.noise.Noises;

record NoiseWarp(Noise x, Noise z, Noise mappedX, Noise mappedZ, Noise distance) implements Warp {
	public static final MapCodec<NoiseWarp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("x").forGetter(NoiseWarp::x),
		Noise.HOLDER_HELPER_CODEC.fieldOf("z").forGetter(NoiseWarp::z),
		Noise.HOLDER_HELPER_CODEC.fieldOf("distance").forGetter(NoiseWarp::distance)
	).apply(instance, NoiseWarp::new));
	
	public NoiseWarp(Noise x, Noise z, Noise distance) {
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
	public Warp mapAll(Visitor visitor) {
		return new NoiseWarp(this.x.mapAll(visitor), this.z.mapAll(visitor), this.mappedX.mapAll(visitor), this.mappedZ.mapAll(visitor), this.distance.mapAll(visitor));
	}

	@Override
	public MapCodec<NoiseWarp> codec() {
		return CODEC;
	}
	
    private static Noise map(Noise in) {
    	if (in.minValue() == -0.5F && in.maxValue() == 0.5F) {
    		return in;
    	}
    	return Noises.map(in, -0.5F, 0.5F);
    }
}
