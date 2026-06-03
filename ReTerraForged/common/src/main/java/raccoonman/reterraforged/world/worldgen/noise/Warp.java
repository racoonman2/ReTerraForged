package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Warp(Noise input, raccoonman.reterraforged.world.worldgen.noise.warp.Warp warp) implements Noise {
	public static final MapCodec<Warp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Warp::input),
		raccoonman.reterraforged.world.worldgen.noise.warp.Warp.HOLDER_HELPER_CODEC.fieldOf("warp").forGetter(Warp::warp)
	).apply(instance, Warp::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float warpX = this.warp.getX(x, z, seed);
		float warpZ = this.warp.getZ(x, z, seed);
		return this.input.compute(warpX, warpZ, seed);
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
		return visitor.apply(new Warp(this.input.mapAll(visitor), this.warp.mapAll(visitor)));
	}

	@Override
	public MapCodec<Warp> codec() {
		return CODEC;
	}
}
