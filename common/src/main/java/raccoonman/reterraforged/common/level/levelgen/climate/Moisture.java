/*
 * Decompiled with CFR 0.150.
 */
package raccoonman.reterraforged.common.level.levelgen.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

// this is just a normal noise module
@Deprecated(forRemoval = true)
public class Moisture implements Noise {
	public static final Codec<Moisture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter((m) -> m.originalSource),
		Codec.INT.fieldOf("power").forGetter((m) -> m.power)
	).apply(instance, Moisture::new));
	
	private final Noise originalSource;
    private final Noise source;
    private final int power;

    public Moisture(int seed, int scale, int power) {
        this(Source.simplex(scale, 1).clamp(0.125, 0.875).map(0.0, 1.0).shift(seed), power);
    }

    public Moisture(Noise source, int power) {
    	this.originalSource = source;
    	this.source = source.freq(0.5, 1.0);
        this.power = power;
    }

    @Override
    public float getValue(float x, float y, int seed) {
        float noise = this.source.getValue(x, y, seed);
        if (this.power < 2) {
            return noise;
        }
        noise = (noise - 0.5f) * 2.0f;
        float value = NoiseUtil.pow(noise, this.power);
        value = NoiseUtil.copySign(value, noise);
        return NoiseUtil.map(value, -1.0f, 1.0f, 2.0f);
    }

	@Override
	public Codec<Moisture> codec() {
		return CODEC;
	}
}

