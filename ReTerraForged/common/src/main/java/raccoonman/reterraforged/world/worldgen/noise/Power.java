package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Power(Noise input, float power) implements Noise {
	public static final MapCodec<Power> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Power::input),
		Codec.FLOAT.fieldOf("power").forGetter(Power::power)
	).apply(instance, Power::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return NoiseUtil.pow(this.input.compute(x, z, seed), this.power);
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
		return visitor.apply(new Power(this.input.mapAll(visitor), this.power));
	}

	@Override
	public MapCodec<Power> codec() {
		return CODEC;
	}
}
