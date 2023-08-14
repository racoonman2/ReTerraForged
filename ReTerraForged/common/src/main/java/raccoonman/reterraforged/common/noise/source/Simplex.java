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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.util.Noise2D;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class Simplex extends BaseNoise {
	public static final Codec<Simplex> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		Codec.FLOAT.optionalFieldOf("lacunarity", Builder.DEFAULT_LACUNARITY).forGetter((n) -> n.lacunarity),
		Codec.INT.optionalFieldOf("octaves", Builder.DEFAULT_OCTAVES).forGetter((n) -> n.octaves),
		Codec.FLOAT.optionalFieldOf("gain", Builder.DEFAULT_GAIN).forGetter((n) -> n.gain)
	).apply(instance, Simplex::new));
	
    private static final float[] SIGNALS = { 1.00F, 0.989F, 0.810F, 0.781F, 0.708F, 0.702F, 0.696F };
	private final float lacunarity;
	private final int octaves;
	private final float gain;
    private final float min;
    private final float max;
    private final float range;

    public Simplex(float frequency, float lacunarity, int octaves, float gain) {
        super(frequency);
        this.lacunarity = lacunarity;
        this.octaves = octaves;
        this.gain = gain;
        this.min = -max(octaves, gain);
        this.max = max(octaves, gain);
        this.range = max - min;
    }

    @Override
    public float getValue(float x, float y, int seed) {
        x *= frequency;
        y *= frequency;

        float sum = 0;
        float amp = 1;

        for (int i = 0; i < octaves; i++) {
            sum += Noise2D.singleSimplex(x, y, seed + i) * amp;
            x *= lacunarity;
            y *= lacunarity;
            amp *= gain;
        }

        return NoiseUtil.map(sum, min, max, range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Simplex that = (Simplex) o;

        if (Float.compare(that.min, min) != 0) return false;
        if (Float.compare(that.max, max) != 0) return false;
        return Float.compare(that.range, range) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        return result;
    }
    
    @Override
	public Codec<Simplex> codec() {
		return CODEC;
	}

    protected static float max(int octaves, float gain) {
        float signal = signal(octaves);

        float sum = 0;
        float amp = 1;
        for (int i = 0; i < octaves; i++) {
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
