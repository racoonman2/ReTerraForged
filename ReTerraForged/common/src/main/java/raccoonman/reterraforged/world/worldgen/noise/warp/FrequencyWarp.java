package raccoonman.reterraforged.world.worldgen.noise.warp;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.Noise.Visitor;

record FrequencyWarp(Warp input, Noise xFrequency, Noise zFrequency) implements Warp {
	public static final MapCodec<FrequencyWarp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Warp.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(FrequencyWarp::input),
		Noise.HOLDER_HELPER_CODEC.fieldOf("xFrequency").forGetter(FrequencyWarp::xFrequency),
		Noise.HOLDER_HELPER_CODEC.fieldOf("zFrequency").forGetter(FrequencyWarp::zFrequency)
	).apply(instance, FrequencyWarp::new));

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
		float frequency = this.xFrequency.compute(x, z, seed);
		return this.input.getX(x * frequency, z * frequency, seed);
	}

	@Override
	public float getZ(float x, float z, int seed) {
		float frequency = this.zFrequency.compute(x, z, seed);
		return this.input.getZ(x * frequency, z * frequency, seed);
	}

	@Override
	public Warp mapAll(Visitor visitor) {
		return new FrequencyWarp(this.input.mapAll(visitor), this.xFrequency.mapAll(visitor), this.zFrequency.mapAll(visitor));
	}

	@Override
	public MapCodec<FrequencyWarp> codec() {
		return CODEC;
	}
}
