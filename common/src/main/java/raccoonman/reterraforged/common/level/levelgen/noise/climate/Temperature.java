/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record Temperature(float frequency, int power) implements Noise {
	public static final Codec<Temperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Temperature::frequency),
		Codec.INT.fieldOf("power").forGetter(Temperature::power)
	).apply(instance, Temperature::new));

	@Override
	public float minValue() {
		return -1.0F;
	}
	
	@Override
	public float maxValue() {
		return 1.0F;
	}
	
    @Override
    public float compute(float x, float y, int seed) {
        float sin = NoiseUtil.sin(y *= this.frequency);
        sin = NoiseUtil.clamp(sin, -1.0F, 1.0F);
        float value = NoiseUtil.pow(sin, this.power);
        value = NoiseUtil.copySign(value, sin);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }

	@Override
	public Codec<Temperature> codec() {
		return CODEC;
	}
}

