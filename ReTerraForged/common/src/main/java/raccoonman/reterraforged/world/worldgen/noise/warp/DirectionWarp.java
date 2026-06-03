package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record DirectionWarp(Noise direction, Noise strength) implements Warp {
	public static final MapCodec<DirectionWarp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("direction").forGetter(DirectionWarp::direction),
		Noise.HOLDER_HELPER_CODEC.fieldOf("strength").forGetter(DirectionWarp::strength)
	).apply(instance, DirectionWarp::new));
	
	@Override
	public float getOffsetX(float x, float z, int seed) {
        float angle = this.direction.compute(x, z, seed) * 6.2831855F;
        return NoiseUtil.sin(angle) * this.strength.compute(x, z, seed);
	}

	@Override
	public float getOffsetZ(float x, float z, int seed) {
        float angle = this.direction.compute(x, z, seed) * 6.2831855F;
        return NoiseUtil.cos(angle) * this.strength.compute(x, z, seed);
	}

	@Override
	public Warp mapAll(Visitor visitor) {
		return new DirectionWarp(this.direction.mapAll(visitor), this.strength.mapAll(visitor));
	}

	@Override
	public MapCodec<DirectionWarp> codec() {
		return CODEC;
	}
}
