package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Temperature(float frequency, int power) implements Noise {
	public static final MapCodec<Temperature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Temperature::frequency),
		Codec.INT.fieldOf("power").forGetter(Temperature::power)
	).apply(instance, Temperature::new));
	
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
	public MapCodec<Temperature> codec() {
		return CODEC;
	}
}
