package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

//TODO this should replace the other classes that have this builtin
//ex. Perlin should be replaced with Perlin + Fractal
//this way we don't have to repeat octaves/gain/lacunarity in every noise source
public record Fractal(Noise source, int octaves, float gain, float lacunarity) implements Noise {
	public static final Codec<Fractal> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Fractal::source),
		Codec.INT.fieldOf("octaves").forGetter(Fractal::octaves),
		Codec.FLOAT.fieldOf("gain").forGetter(Fractal::gain),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(Fractal::lacunarity)
	).apply(instance, Fractal::new));

	@Override
	public float minValue() {
		return this.source.minValue();
	}
	
	@Override
	public float maxValue() {
		return this.source.maxValue();
	}
	
	@Override
	public Codec<Fractal> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float sum = this.source.getValue(x, y, seed);
		float amp = this.source.maxValue();
		float sumAmp = amp;
        for(int i = 1; i < this.octaves; ++i) {
            amp *= this.gain;
            x *= this.lacunarity;
            y *= this.lacunarity;

            sum += this.source.getValue(x, y, seed) * amp;
            sumAmp += amp;
        }
        return sum / sumAmp;
	}	
}
