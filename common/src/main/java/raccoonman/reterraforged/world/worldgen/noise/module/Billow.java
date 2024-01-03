package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;

record Billow(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float[] spectralWeights, float min, float max) implements Noise {
	public static final Codec<Billow> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Billow::frequency),
		Codec.INT.fieldOf("octaves").forGetter(Billow::octaves),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(Billow::lacunarity),
		Codec.FLOAT.fieldOf("gain").forGetter(Billow::gain),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(Billow::interpolation)
	).apply(instance, Billow::new));
	
	public Billow(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation) {
		this(frequency, octaves, lacunarity, gain, interpolation, calculateSpectralWeights(octaves, lacunarity));
	}
	
	private Billow(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float[] spectralWeights) {
		this(frequency, octaves, lacunarity, gain, interpolation, spectralWeights, 0.0F, calculateMaxBound(spectralWeights, gain));
	}
	
	public Billow {
		octaves = Math.min(octaves, 30);
	}
	
	@Override
	public float compute(float x, float z, int seed) {
        x *= this.frequency;
        z *= this.frequency;
        float amp = 2.0F;
        float value = 0.0F;
        float weight = 1.0F;
        for (int octave = 0; octave < this.octaves; ++octave) {
            float signal = Perlin.sample(x, z, seed + octave, this.interpolation);
            signal = 1.0F - Math.abs(signal);
            signal *= signal;
            signal *= weight;
            weight = signal * amp;
            weight = NoiseUtil.clamp(weight, 0.0F, 1.0F);
            value += signal * this.spectralWeights[octave];
            x *= this.lacunarity;
            z *= this.lacunarity;
            amp *= this.gain;
        }
        return 1.0F - NoiseUtil.map(value, this.min, this.max, Math.abs(this.max - this.min));
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
	public Codec<Billow> codec() {
		return CODEC;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof Billow other && other.frequency == this.frequency && other.octaves == this.octaves && other.lacunarity == this.lacunarity && other.gain == this.gain && other.interpolation.equals(this.interpolation);
	}
	
	private static float[] calculateSpectralWeights(int octaves, float lacunarity) {
		float frequency = 1.0F;
		float[] spectralWeights = new float[octaves];
		for(int i = 0; i < spectralWeights.length; i++) {
			spectralWeights[i] = NoiseUtil.pow(frequency, -1.0F);
			frequency *= lacunarity;
		}
		return spectralWeights;
	}
	
	private static float calculateMaxBound(float[] spectralWeights, float gain) {
        float amp = 2.0F;
        float value = 0.0F;
        float weight = 1.0F;
        for (int curOctave = 0; curOctave < spectralWeights.length; ++curOctave) {
            float noise = 1.0F;
            noise *= weight;
            weight = noise * amp;
            weight = Math.min(1.0F, Math.max(0.0F, weight));
            value += noise * spectralWeights[curOctave];
            amp *= gain;
        }
        return value;
	}
}
