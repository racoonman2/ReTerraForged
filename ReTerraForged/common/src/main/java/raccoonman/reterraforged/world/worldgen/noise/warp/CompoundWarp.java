package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;

public record CompoundWarp(Warp input1, Warp input2) implements Warp {
	public static final MapCodec<CompoundWarp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Warp.HOLDER_HELPER_CODEC.fieldOf("input1").forGetter(CompoundWarp::input1),
		Warp.HOLDER_HELPER_CODEC.fieldOf("input2").forGetter(CompoundWarp::input2)
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
	public Warp mapAll(Visitor visitor) {
		return new CompoundWarp(this.input1.mapAll(visitor), this.input2.mapAll(visitor));
	}

	@Override
	public MapCodec<CompoundWarp> codec() {
		return CODEC;
	}
}
