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
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;

public class Perlin extends NoiseSource {
	public static final Codec<Perlin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		Codec.FLOAT.optionalFieldOf("lacunarity", Builder.DEFAULT_LACUNARITY).forGetter((n) -> n.lacunarity),
		Interpolation.CODEC.optionalFieldOf("interp", Builder.DEFAULT_INTERPOLATION).forGetter((n) -> n.interpolation),
		Codec.INT.optionalFieldOf("octaves", Builder.DEFAULT_OCTAVES).forGetter((n) -> n.octaves),
		Codec.FLOAT.optionalFieldOf("gain", Builder.DEFAULT_GAIN).forGetter((n) -> n.gain)
	).apply(instance, Perlin::new));
	
    private static final float[] SIGNALS = { 1F, 0.900F, 0.83F, 0.75F, 0.64F, 0.62F, 0.61F };
    private final int octaves;
    private final float gain;
    private final Interpolation interpolation;
    private final float lacunarity;
    protected final float min;
    protected final float max;
    protected final float range;

    public Perlin(float frequency, float lacunarity, Interpolation interpolation, int octaves, float gain) {
        super(frequency);
        this.octaves = octaves;
        this.gain = gain;
        this.interpolation = interpolation;
        this.lacunarity = lacunarity;
        this.min = this.min(octaves, gain);
        this.max = this.max(octaves, gain);
        this.range = Math.abs(this.max - this.min);
    }

    @Override
    public float compute(float x, float y, int seed) {
        x *= frequency;
        y *= frequency;

        float sum = 0;
        float amp = gain;

        for (int i = 0; i < octaves; i++) {
            sum += single(x, y, seed + i, interpolation) * amp;
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

        Perlin that = (Perlin) o;

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
	public Codec<Perlin> codec() {
		return CODEC;
	}
    
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Perlin(this.frequency, this.lacunarity, this.interpolation, this.octaves, this.gain));
	}

    protected float min(int octaves, float gain) {
        return -max(octaves, gain);
    }

    protected float max(int octaves, float gain) {
        float signal = signal(octaves);
        float sum = 0;
        float amp = gain;
        for (int i = 0; i < octaves; i++) {
            sum += signal * amp;
            amp *= gain;
        }
        return sum;
    }

    public static float single(float x, float y, int seed, Interpolation interp) {
        int x0 = NoiseUtil.floor(x);
        int y0 = NoiseUtil.floor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;

        float xs = interp.apply(x - x0);
        float ys = interp.apply(y - y0);

        float xd0 = x - x0;
        float yd0 = y - y0;
        float xd1 = xd0 - 1;
        float yd1 = yd0 - 1;

        float xf0 = NoiseUtil.lerp(NoiseUtil.gradCoord2D(seed, x0, y0, xd0, yd0), NoiseUtil.gradCoord2D(seed, x1, y0, xd1, yd0), xs);
        float xf1 = NoiseUtil.lerp(NoiseUtil.gradCoord2D(seed, x0, y1, xd0, yd1), NoiseUtil.gradCoord2D(seed, x1, y1, xd1, yd1), xs);

        return NoiseUtil.lerp(xf0, xf1, ys);
    }

    private static float signal(int octaves) {
        int index = Math.min(octaves, SIGNALS.length - 1);
        return SIGNALS[index];
    }
}
