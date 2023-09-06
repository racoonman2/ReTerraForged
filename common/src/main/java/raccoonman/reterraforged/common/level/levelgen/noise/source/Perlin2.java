// 
// Decompiled by Procyon v0.5.36
// 

package raccoonman.reterraforged.common.level.levelgen.noise.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;

public class Perlin2 extends NoiseSource {
	public static final Codec<Perlin2> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		Codec.FLOAT.optionalFieldOf("lacunarity", Builder.DEFAULT_LACUNARITY).forGetter((n) -> n.lacunarity),
		Interpolation.CODEC.optionalFieldOf("interp", Builder.DEFAULT_INTERPOLATION).forGetter((n) -> n.interpolation),
		Codec.INT.optionalFieldOf("octaves", Builder.DEFAULT_OCTAVES).forGetter((n) -> n.octaves),
		Codec.FLOAT.optionalFieldOf("gain", Builder.DEFAULT_GAIN).forGetter((n) -> n.gain)
	).apply(instance, Perlin2::new));
	
	private static final float[] SIGNALS = { 1.0F, 0.9F, 0.83F, 0.75F, 0.64F, 0.62F, 0.61F };
	private final int octaves;
	private final float gain;
	private final Interpolation interpolation;
	private final float lacunarity;
	private final float min;	
	private final float max;
	private final float range;

	public Perlin2(float frequency, float lacunarity, Interpolation interpolation, int octaves, float gain) {
		super(frequency);
		this.octaves = octaves;
		this.gain = gain;
		this.interpolation = interpolation;
		this.lacunarity = lacunarity;
		this.min = this.min(octaves, gain);
		this.max = this.max(octaves, gain);
		this.range = Math.abs(this.max - this.min);
	}

	@Override
	public Codec<Perlin2> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
	
    @Override
    public float compute(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float sum = 0.0f;
        float amp = this.gain;
        for (int i = 0; i < this.octaves; ++i) {
            sum += single(x, y, seed + i, this.interpolation) * amp;
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }
        return NoiseUtil.map(sum, this.min, this.max, this.range);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        Perlin2 that = (Perlin2) o;
        return Float.compare(that.min, this.min) == 0 && Float.compare(that.max, this.max) == 0 && Float.compare(that.range, this.range) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + ((this.min != 0.0f) ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + ((this.max != 0.0f) ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + ((this.range != 0.0f) ? Float.floatToIntBits(this.range) : 0);
        return result;
    }
    
    private float min(final int octaves, final float gain) {
        return -this.max(octaves, gain);
    }
    
    private float max(final int octaves, final float gain) {
        final float signal = signal(octaves);
        float sum = 0.0f;
        float amp = gain;
        for (int i = 0; i < octaves; ++i) {
            sum += signal * amp;
            amp *= gain;
        }
        return sum;
    }
    
    public static float single(float x, float y, int seed, Interpolation interp) {
        int x0 = NoiseUtil.floor(x);
        int y0 = NoiseUtil.floor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        float xs = interp.apply(x - x0);
        float ys = interp.apply(y - y0);

        float xd0 = x - x0;
        float yd0 = y - y0;
        float xd1 = xd0 - 1;
        float yd1 = yd0 - 1;

        float xf0 = NoiseUtil.lerp(NoiseUtil.gradCoord2D_24(seed, x0, y0, xd0, yd0), NoiseUtil.gradCoord2D_24(seed, x1, y0, xd1, yd0), xs);
        float xf1 = NoiseUtil.lerp(NoiseUtil.gradCoord2D_24(seed, x0, y1, xd0, yd1), NoiseUtil.gradCoord2D_24(seed, x1, y1, xd1, yd1), xs);

        return NoiseUtil.lerp(xf0, xf1, ys);
    }
    
    private static float signal(final int octaves) {
        final int index = Math.min(octaves, SIGNALS.length - 1);
        return SIGNALS[index];
    }
}
