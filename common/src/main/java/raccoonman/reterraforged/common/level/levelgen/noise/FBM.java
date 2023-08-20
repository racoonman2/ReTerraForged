package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;

public record FBM(Noise noise, int octaves, float gain, float lacunarity) implements Noise {
	public static final Codec<FBM> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("noise").forGetter(FBM::noise),
		Codec.INT.fieldOf("octaves").forGetter(FBM::octaves),
		Codec.FLOAT.fieldOf("gain").forGetter(FBM::gain),
		Codec.FLOAT.fieldOf("lacunarity").forGetter(FBM::lacunarity)
	).apply(instance, FBM::new));
	
	@Override
	public Codec<FBM> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float sum = this.noise.getValue(x, y, seed);
		float amp = 1.0F;
		float sumAmp = amp;
        for(int i = 1; i < this.octaves; ++i) {
            amp *= this.gain;
            x *= this.lacunarity;
            y *= this.lacunarity;

            sum += this.noise.getValue(x, y, seed) * amp;
            sumAmp += amp;
        }
        return sum / sumAmp;
	}	
}
