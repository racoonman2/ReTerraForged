package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;

public record Simplex(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float min, float max) implements Noise {
	public static final Codec<Simplex> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Simplex::frequency),
		Codec.INT.fieldOf("octaves").forGetter(Simplex::octaves),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(Simplex::lacunarity),
		Codec.FLOAT.fieldOf("gain").forGetter(Simplex::gain),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(Simplex::interpolation)
	).apply(instance, Simplex::new));
	
	private static final float[] SIGNALS = new float[] { 
		1.0F, 0.989F, 0.81F, 0.781F, 0.708F, 0.702F, 0.696F 
	};
	
    public Simplex(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation) {
    	this(frequency, octaves, lacunarity, gain, interpolation, -max(octaves, gain), max(octaves, gain));
    }
    
	@Override
	public float compute(float x, float z, int seed) {
        x *= this.frequency;
        z *= this.frequency;
        float sum = 0.0F;
        float amp = 1.0F;
        for (int i = 0; i < this.octaves; ++i) {
            sum += sample(x, z, seed + i) * amp;
            x *= this.lacunarity;
            z *= this.lacunarity;
            amp *= this.gain;
        }
        return NoiseUtil.map(sum, this.min, this.max, (this.max - this.min));
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
	public Codec<Simplex> codec() {
		return CODEC;
	}
    
    public static float sample(float x, float y, int seed) {
        return singleSimplex(x, y, seed, 79.869484F);
    }
    
    public static float singleSimplex(float x, float y, int seed, float scaler) {
        float t = (x + y) * 0.36602542F;
        int i = NoiseUtil.floor(x + t);
        int j = NoiseUtil.floor(y + t);
        t = (i + j) * 0.21132487F;
        float X0 = i - t;
        float Y0 = j - t;
        float x2 = x - X0;
        float y2 = y - Y0;
        int i2;
        int j2;
        if (x2 > y2) {
            i2 = 1;
            j2 = 0;
        } else {
            i2 = 0;
            j2 = 1;
        }
        float x3 = x2 - i2 + 0.21132487F;
        float y3 = y2 - j2 + 0.21132487F;
        float x4 = x2 - 1.0F + 0.42264974F;
        float y4 = y2 - 1.0F + 0.42264974F;
        t = 0.5F - x2 * x2 - y2 * y2;
        float n0;
        if (t < 0.0F) {
            n0 = 0.0F;
        } else {
            t *= t;
            n0 = t * t * NoiseUtil.gradCoord2D_24(seed, i, j, x2, y2);
        }
        t = 0.5F - x3 * x3 - y3 * y3;
        float n2;
        if (t < 0.0F) {
            n2 = 0.0F;
        } else {
            t *= t;
            n2 = t * t * NoiseUtil.gradCoord2D_24(seed, i + i2, j + j2, x3, y3);
        }
        t = 0.5F - x4 * x4 - y4 * y4;
        float n3;
        if (t < 0.0F) {
            n3 = 0.0F;
        } else {
            t *= t;
            n3 = t * t * NoiseUtil.gradCoord2D_24(seed, i + 1, j + 1, x4, y4);
        }
        return scaler * (n0 + n2 + n3);
    }
    
    private static float max(int octaves, float gain) {
        float signal = signal(octaves);
        float sum = 0.0F;
        float amp = 1.0F;
        for (int i = 0; i < octaves; ++i) {
            sum += amp * signal;
            amp *= gain;
        }
        return sum;
    }
    
    private static float signal(int octaves) {
        int index = Math.min(octaves, SIGNALS.length - 1);
        return SIGNALS[index];
    }
}
