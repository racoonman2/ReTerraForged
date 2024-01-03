package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;

public record Simplex2(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float min, float max) implements Noise {
	public static final Codec<Simplex2> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Simplex2::frequency),
		Codec.INT.fieldOf("octaves").forGetter(Simplex2::octaves),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(Simplex2::lacunarity),
		Codec.FLOAT.fieldOf("gain").forGetter(Simplex2::gain),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(Simplex2::interpolation)
	).apply(instance, Simplex2::new));
	
	private static final float[] SIGNALS = new float[] { 
		1.0F, 0.989F, 0.81F, 0.781F, 0.708F, 0.702F, 0.696F
	};
	
    public Simplex2(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation) {
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
	public Codec<Simplex2> codec() {
		return CODEC;
	}
    
	public static float sample(float x, float y, int seed) {
        return Simplex.singleSimplex(x, y, seed, 99.83685F);
    }
    
    public static float max(int octaves, float gain) {
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
