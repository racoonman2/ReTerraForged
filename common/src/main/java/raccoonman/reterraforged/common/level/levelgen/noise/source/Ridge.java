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

package raccoonman.reterraforged.common.level.levelgen.noise.source;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.Noise2D;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class Ridge extends BaseNoise {
	public static final Codec<Ridge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter((n) -> n.frequency),
		Interpolation.CODEC.fieldOf("interpolation").forGetter((r) -> r.interpolation),
		Codec.FLOAT.fieldOf("lacunarity").forGetter((n) -> n.lacunarity),
		Codec.INT.fieldOf("octaves").forGetter((n) -> n.octaves),
		Codec.FLOAT.fieldOf("gain").forGetter((n) -> n.gain)
	).apply(instance, Ridge::new));

    private static final int RIDGED_MAX_OCTAVE = 30;

    protected final Interpolation interpolation;
    protected final float lacunarity;
    protected final int octaves;
    protected float gain;
    private final float[] spectralWeights;
    private final float min;
    private final float max;
    private final float range;

    public Ridge(float frequency, Interpolation interp, float lacunarity, int octaves, float gain) {
    	super(frequency);
    	this.interpolation = interp;
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
        max = calculateBound(0.0F, octaves, gain);
        range = Math.abs(max - min);
    }

    @Override
    public float compute(float x, float y, int seed) {
        x *= frequency;
        y *= frequency;

        float signal;
        float value = 0.0F;
        float weight = 1.0F;

        float offset = 1.0F;
        float amp = 2.0F;

        for (int octave = 0; octave < octaves; octave++) {
            signal = Noise2D.singlePerlin(x, y, seed + octave, interpolation);
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
        if (!(o instanceof Ridge)) return false;
        if (!super.equals(o)) return false;

        Ridge that = (Ridge) o;

        if (Float.compare(that.min, min) != 0) return false;
        if (Float.compare(that.max, max) != 0) return false;
        if (Float.compare(that.range, range) != 0) return false;
        if (interpolation != that.interpolation) return false;
        return Arrays.equals(spectralWeights, that.spectralWeights);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + interpolation.hashCode();
        result = 31 * result + Arrays.hashCode(spectralWeights);
        result = 31 * result + (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        return result;
    }
    
    // FIXME this currently can't return Codec<RidgeNoise> because BillowNoise extends this
    @Override
	public Codec<? extends Ridge> codec() {
		return CODEC;
	}

    private float calculateBound(float signal, int octaves, float gain) {
        float value = 0.0F;
        float weight = 1.0F;

        float amp = 2.0F;
        float offset = 1.0F;

        for (int curOctave = 0; curOctave < octaves; curOctave++) {
            float noise = signal;
            noise = Math.abs(noise);
            noise = offset - noise;
            noise *= noise;
            noise *= weight;
            weight = noise * amp;
            weight = Math.min(1F, Math.max(0F, weight));
            value += (noise * spectralWeights[curOctave]);
            amp *= gain;
        }

        return value;
    }

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Ridge(this.frequency, this.interpolation, this.lacunarity, this.octaves, this.gain));
	}
}