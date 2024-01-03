package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.Interpolation;

record SimplexRidge(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float[] spectralWeights, float min, float max) implements Noise {
	public static final Codec<PerlinRidge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(PerlinRidge::frequency),
		Codec.INT.fieldOf("octaves").forGetter(PerlinRidge::octaves),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(PerlinRidge::lacunarity),
		Codec.FLOAT.fieldOf("gain").forGetter(PerlinRidge::gain),
		Interpolation.CODEC.fieldOf("interpolation").forGetter(PerlinRidge::interpolation)
	).apply(instance, PerlinRidge::new));
	
	public SimplexRidge(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation) {
		this(frequency, octaves, lacunarity, gain, interpolation, calculateSpectralWeights(lacunarity));
	}
	
	private SimplexRidge(float frequency, int octaves, float lacunarity, float gain, Interpolation interpolation, float[] spectralWeights) {
		this(frequency, octaves, lacunarity, gain, interpolation, spectralWeights, 0.0F, Simplex2.max(octaves, gain));
	}
	
	@Override
	public float compute(float x, float z, int seed) {
        x *= this.frequency;
        z *= this.frequency;
        float value = 0.0F;
        float weight = 1.0F;
        float offset = 1.0F;
        float amp = 2.0F;
        for (int octave = 0; octave < this.octaves; ++octave) {
            float signal = Simplex2.sample(x, z, seed + octave);
            signal = Math.abs(signal);
            signal = offset - signal;
            signal *= signal;
            signal *= weight;
            weight = signal * amp;
            weight = NoiseUtil.clamp(weight, 0.0F, 1.0F);
            value += signal * this.spectralWeights[octave];
            x *= this.lacunarity;
            z *= this.lacunarity;
            amp *= this.gain;
        }
        return NoiseUtil.map(value, this.min, this.max, Math.abs(this.max - this.min));
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
	public Codec<PerlinRidge> codec() {
		return CODEC;
	}
	
	@Override
	public boolean equals(Object o) {
		return o instanceof SimplexRidge other && other.frequency == this.frequency && other.octaves == this.octaves && other.lacunarity == this.lacunarity && other.gain == this.gain && other.interpolation.equals(this.interpolation);
	}
	
	private static float[] calculateSpectralWeights(float lacunarity) {
		float frequency = 1.0F;
		float[] spectralWeights = new float[30];
		for(int i = 0; i < spectralWeights.length; i++) {
			spectralWeights[i] = NoiseUtil.pow(frequency, -1.0F);
			frequency *= lacunarity;
		}
		return spectralWeights;
	}
}
