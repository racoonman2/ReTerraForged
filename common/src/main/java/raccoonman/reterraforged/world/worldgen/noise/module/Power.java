package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record Power(Noise input, float power) implements Noise {
	public static final Codec<Power> CODEC = RecordCodecBuilder.create(instance -> instance.group(
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
	public Codec<Power> codec() {
		return CODEC;
	}
}
