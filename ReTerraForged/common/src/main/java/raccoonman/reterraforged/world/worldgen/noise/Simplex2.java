package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Simplex2(float frequency, int octaves, float lacunarity, float gain, float min, float max) implements Noise {
	public static final MapCodec<Simplex2> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Simplex2::frequency),
		Codec.INT.fieldOf("octaves").forGetter(Simplex2::octaves),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(Simplex2::lacunarity),
		Codec.FLOAT.fieldOf("gain").forGetter(Simplex2::gain)
	).apply(instance, Simplex2::new));
	
	private static final float[] SIGNALS = new float[] { 
		1.0F, 0.989F, 0.81F, 0.781F, 0.708F, 0.702F, 0.696F 
	};
	
    public Simplex2(float frequency, int octaves, float lacunarity, float gain) {
    	this(frequency, octaves, lacunarity, gain, -bound(octaves, gain), bound(octaves, gain));
    }

	@Override
	public float compute(float x, float z, int seed) {
        x *= this.frequency;
        z *= this.frequency;
        float sum = 0.0F;
        float amplitude = 1.0F;
        for (int i = 0; i < this.octaves; i++) {
        	sum += sample(x, z, seed + i) * amplitude;
            x *= this.lacunarity;
            z *= this.lacunarity;
            amplitude *= this.gain;
        }
        return NoiseUtil.map(sum, this.min, this.max, this.max - this.min);
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
	public MapCodec<Simplex2> codec() {
		return CODEC;
	}
    
    public static float sample(float x, float y, int seed) {
        return sample(x, y, seed, 99.83685f);
    }
    
    public static float sample(float x, float y, int seed, float scaler) {
        float t = (x + y) * 0.36602542f;
        final int i = NoiseUtil.floor(x + t);
        final int j = NoiseUtil.floor(y + t);
        t = (i + j) * 0.21132487f;
        final float X0 = i - t;
        final float Y0 = j - t;
        final float x2 = x - X0;
        final float y2 = y - Y0;
        int i2;
        int j2;
        if (x2 > y2) {
            i2 = 1;
            j2 = 0;
        }
        else {
            i2 = 0;
            j2 = 1;
        }
        final float x3 = x2 - i2 + 0.21132487f;
        final float y3 = y2 - j2 + 0.21132487f;
        final float x4 = x2 - 1.0f + 0.42264974f;
        final float y4 = y2 - 1.0f + 0.42264974f;
        t = 0.5f - x2 * x2 - y2 * y2;
        float n0;
        if (t < 0.0f) {
            n0 = 0.0f;
        }
        else {
            t *= t;
            n0 = t * t * NoiseUtil.gradCoord2D_24(seed, i, j, x2, y2);
        }
        t = 0.5f - x3 * x3 - y3 * y3;
        float n2;
        if (t < 0.0f) {
            n2 = 0.0f;
        }
        else {
            t *= t;
            n2 = t * t * NoiseUtil.gradCoord2D_24(seed, i + i2, j + j2, x3, y3);
        }
        t = 0.5f - x4 * x4 - y4 * y4;
        float n3;
        if (t < 0.0f) {
            n3 = 0.0f;
        }
        else {
            t *= t;
            n3 = t * t * NoiseUtil.gradCoord2D_24(seed, i + 1, j + 1, x4, y4);
        }
        return scaler * (n0 + n2 + n3);
    }
    
    private static float bound(int octaves, float gain) {
        float signal = signal(octaves);
        float sum = 0.0F;
        float amp = 1.0F;
        for (int i = 0; i < octaves; i++) {
            sum += amp * signal;
            amp *= gain;
        }
        return sum;
    }
    
    protected static float signal(int octaves) {
        int index = Math.min(octaves, SIGNALS.length - 1);
        return SIGNALS[index];
    }
}
