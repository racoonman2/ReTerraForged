package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record Temperature(int power, float frequency) implements Noise {
    public static final Codec<Temperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Codec.INT.fieldOf("power").forGetter(Temperature::power),
    	Codec.FLOAT.fieldOf("frequency").forGetter(Temperature::frequency)
    ).apply(instance, Temperature::new));
	
    @Override
    public float compute(float x, float y, int seed) {
        y *= this.frequency;
        float sin = NoiseUtil.sin(y);
        sin = NoiseUtil.clamp(sin, -1.0F, 1.0F);
        float value = NoiseUtil.pow(sin, this.power);
        value = NoiseUtil.copySign(value, sin);
        return NoiseUtil.map(value, -1.0F, 1.0F);
    }

	@Override
	public Codec<Temperature> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
}
