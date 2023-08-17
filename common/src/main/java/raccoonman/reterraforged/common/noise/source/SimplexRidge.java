/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.noise.source;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.util.Noise2D;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class SimplexRidge extends BaseNoise {
	public static final Codec<SimplexRidge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		Codec.FLOAT.optionalFieldOf("lacunarity", Builder.DEFAULT_LACUNARITY).forGetter((n) -> n.lacunarity),
		Codec.INT.optionalFieldOf("octaves", Builder.DEFAULT_OCTAVES).forGetter((n) -> n.octaves),
		Codec.FLOAT.optionalFieldOf("gain", Builder.DEFAULT_GAIN).forGetter((n) -> n.gain)
	).apply(instance, SimplexRidge::new));
	
    private static final int RIDGED_MAX_OCTAVE = 30;
    private final float lacunarity;
    private final int octaves;
    private final float gain;
    private final float[] spectralWeights;
    private final float min;
    private final float max;
    private final float range;

    public SimplexRidge(float frequency, float lacunarity, int octaves, float gain) {
        super(frequency);
        this.lacunarity = lacunarity;
        this.octaves = octaves;
        this.gain = gain;
        this.spectralWeights = new float[RIDGED_MAX_OCTAVE];

        float h = 1.0F;
        float freq = 1.0F;
        for (int i = 0; i < RIDGED_MAX_OCTAVE; i++) {
            spectralWeights[i] = NoiseUtil.pow(freq, -h);
            freq *= lacunarity;
        }

        min = 0;
        max = Simplex.max(octaves, gain);
        range = Math.abs(max - min);
    }

    @Override
    public float getValue(float x, float y, int seed) {
        x *= frequency;
        y *= frequency;

        float signal;
        float value = 0.0F;
        float weight = 1.0F;

        float offset = 1.0F;
        float amp = 2.0F;

        for (int octave = 0; octave < octaves; octave++) {
            signal = Noise2D.singleSimplex(x, y, seed + octave);
            signal = Math.abs(signal);
            signal = offset - signal;
            signal *= signal;

            signal *= weight;
            weight = signal * amp;
            weight = NoiseUtil.clamp(weight, 0, 1);
            value += (signal * spectralWeights[octave]);

            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }
        return NoiseUtil.map(value, min, max, range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SimplexRidge)) return false;
        if (!super.equals(o)) return false;

        SimplexRidge that = (SimplexRidge) o;

        if (Float.compare(that.min, min) != 0) return false;
        if (Float.compare(that.max, max) != 0) return false;
        if (Float.compare(that.range, range) != 0) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(spectralWeights);
        result = 31 * result + (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        return result;
    }
    
    @Override
	public Codec<SimplexRidge> codec() {
		return CODEC;
	}
}