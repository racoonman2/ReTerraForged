/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class Temperature implements Noise {
	public static final Codec<Temperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter((m) -> m.frequency),
		Codec.INT.fieldOf("power").forGetter((m) -> m.power)
	).apply(instance, Temperature::new));
	
    private final int power;
    private final float frequency;

    public Temperature(float frequency, int power) {
        this.frequency = frequency;
        this.power = power;
    }

    @Override
    public float getValue(float x, float y, int seed) {
        float sin = NoiseUtil.sin(y *= this.frequency);
        sin = NoiseUtil.clamp(sin, -1.0f, 1.0f);
        float value = NoiseUtil.pow(sin, this.power);
        value = NoiseUtil.copySign(value, sin);
        return NoiseUtil.map(value, -1.0f, 1.0f, 2.0f);
    }

	@Override
	public Codec<Temperature> codec() {
		return CODEC;
	}
}

