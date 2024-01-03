package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;	

public record LegacyTemperature(float frequency, int power) implements Noise {
	public static final Codec<LegacyTemperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(LegacyTemperature::frequency),
		Codec.INT.fieldOf("power").forGetter(LegacyTemperature::power)
	).apply(instance, LegacyTemperature::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		z *= this.frequency;
		float sin = NoiseUtil.sin(z);
		sin = NoiseUtil.clamp(sin, -1.0F, 1.0F);
		float value = NoiseUtil.pow(sin, this.power);
		value = NoiseUtil.copySign(value, sin);
		return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
	}

	@Override
	public float minValue() {
		return 0.0F;
	}

	@Override
	public float maxValue() {
		return 1.0F;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}

	@Override
	public Codec<LegacyTemperature> codec() {
		return CODEC;
	}
}
