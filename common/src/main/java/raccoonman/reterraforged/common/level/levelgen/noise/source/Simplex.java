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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public class Simplex extends NoiseSource {
	public static final Codec<Simplex> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		Codec.FLOAT.optionalFieldOf("lacunarity", Builder.DEFAULT_LACUNARITY).forGetter((n) -> n.lacunarity),
		Codec.INT.optionalFieldOf("octaves", Builder.DEFAULT_OCTAVES).forGetter((n) -> n.octaves),
		Codec.FLOAT.optionalFieldOf("gain", Builder.DEFAULT_GAIN).forGetter((n) -> n.gain)
	).apply(instance, Simplex::new));
	
    private static final float[] SIGNALS = { 1.00F, 0.989F, 0.810F, 0.781F, 0.708F, 0.702F, 0.696F };
    private static final float F2 = 0.366025403784439F;
    private static final float G2 = 0.211324865405187F;
    private static final float LEGACY_SIMPLEX = 79.86948357042918F;
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
        this.range = this.max - this.min;
    }

    @Override
    public float compute(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;

        float sum = 0;
        float amp = 1;

        for (int i = 0; i < this.octaves; i++) {
            sum += singleLegacy(x, y, seed + i) * amp;
            x *= this.lacunarity;
            y *= this.lacunarity;
            amp *= this.gain;
        }
        
        return NoiseUtil.map(sum, this.min, this.max, this.range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Simplex that = (Simplex) o;

        if (Float.compare(that.min, this.min) != 0) return false;
        if (Float.compare(that.max, this.max) != 0) return false;
        return Float.compare(that.range, this.range) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (this.min != +0.0f ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + (this.max != +0.0f ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + (this.range != +0.0f ? Float.floatToIntBits(this.range) : 0);
        return result;
    }
    
    @Override
	public Codec<Simplex> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Simplex(this.frequency, this.lacunarity, this.octaves, this.gain));
	}
	
	public static float singleLegacy(float x, float y, int seed) {
        return single(x, y, seed, LEGACY_SIMPLEX);
    }

    public static float single(float x, float y, int seed, float scaler) {
        float t = (x + y) * F2;
        int i = NoiseUtil.floor(x + t);
        int j = NoiseUtil.floor(y + t);

        t = (i + j) * G2;
        float X0 = i - t;
        float Y0 = j - t;

        float x0 = x - X0;
        float y0 = y - Y0;

        int i1, j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        float x1 = x0 - i1 + G2;
        float y1 = y0 - j1 + G2;
        float x2 = x0 - 1 + 2 * G2;
        float y2 = y0 - 1 + 2 * G2;

        float n0, n1, n2;

        t = 0.5F - x0 * x0 - y0 * y0;
        if (t < 0) {
            n0 = 0;
        } else {
            t *= t;
            n0 = t * t * NoiseUtil.gradCoord2D_24(seed, i, j, x0, y0);
        }

        t = 0.5F - x1 * x1 - y1 * y1;
        if (t < 0) {
            n1 = 0;
        } else {
            t *= t;
            n1 = t * t * NoiseUtil.gradCoord2D_24(seed, i + i1, j + j1, x1, y1);
        }

        t = 0.5F - x2 * x2 - y2 * y2;
        if (t < 0) {
            n2 = 0;
        } else {
            t *= t;
            n2 = t * t * NoiseUtil.gradCoord2D_24(seed, i + 1, j + 1, x2, y2);
        }

        return scaler * (n0 + n1 + n2);
    }

    private static float max(int octaves, float gain) {
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
