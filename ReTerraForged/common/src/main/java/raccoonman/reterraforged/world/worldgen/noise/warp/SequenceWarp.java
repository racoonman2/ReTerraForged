package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;

public record SequenceWarp(Warp warp1, Warp warp2) implements Warp {
	public static final MapCodec<SequenceWarp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Warp.HOLDER_HELPER_CODEC.fieldOf("warp1").forGetter(SequenceWarp::warp1),
		Warp.HOLDER_HELPER_CODEC.fieldOf("warp2").forGetter(SequenceWarp::warp2)		
	).apply(instance, SequenceWarp::new));
	
	@Override
	public float getOffsetX(float x, float z, int seed) {
		return 0;
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
		return 0;
	}

	@Override
	public float getX(float x, float z, int seed) {
		float warpX = this.warp1.getX(x, z, seed);
		float warpZ = this.warp1.getZ(x, z, seed);
		return this.warp2.getX(warpX, warpZ, seed);
	}

	@Override
	public float getZ(float x, float z, int seed) {
		float warpX = this.warp1.getX(x, z, seed);
		float warpZ = this.warp1.getZ(x, z, seed);
		return this.warp2.getZ(warpX, warpZ, seed);
	}

	@Override
	public Warp mapAll(Visitor visitor) {
		return new SequenceWarp(this.warp1.mapAll(visitor), this.warp2.mapAll(visitor));
	}

	@Override
	public MapCodec<SequenceWarp> codec() {
		return CODEC;
	}
}
