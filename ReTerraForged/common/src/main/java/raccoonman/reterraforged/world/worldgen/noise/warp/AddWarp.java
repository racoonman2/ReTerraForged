package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;

record AddWarp(Warp input1, Warp input2) implements Warp {
	public static final MapCodec<AddWarp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Warp.HOLDER_HELPER_CODEC.fieldOf("input1").forGetter(AddWarp::input1),
		Warp.HOLDER_HELPER_CODEC.fieldOf("input2").forGetter(AddWarp::input2)
	).apply(instance, AddWarp::new));
	
	@Override
	public float getOffsetX(float x, float z, int seed) {
		return this.input1.getX(x, z, seed) + this.input2.getX(x, z, seed);
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
		return this.input1.getZ(x, z, seed) + this.input2.getZ(x, z, seed);
	}

	@Override
	public Warp mapAll(Visitor visitor) {
		return new AddWarp(this.input1.mapAll(visitor), this.input2.mapAll(visitor));
	}

	@Override
	public MapCodec<AddWarp> codec() {
		return CODEC;
	}
}
