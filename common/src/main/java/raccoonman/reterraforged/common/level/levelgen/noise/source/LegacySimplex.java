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

import raccoonman.reterraforged.common.level.levelgen.noise.util.Noise2D;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class LegacySimplex extends BaseNoise {
	public static final Codec<LegacySimplex> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		Codec.FLOAT.optionalFieldOf("lacunarity", Builder.DEFAULT_LACUNARITY).forGetter((n) -> n.lacunarity),
		Codec.INT.optionalFieldOf("octaves", Builder.DEFAULT_OCTAVES).forGetter((n) -> n.octaves),
		Codec.FLOAT.optionalFieldOf("gain", Builder.DEFAULT_GAIN).forGetter((n) -> n.gain)
	).apply(instance, LegacySimplex::new));
	
    private static final float[] SIGNALS = { 1.00F, 0.989F, 0.810F, 0.781F, 0.708F, 0.702F, 0.696F };
	private final float lacunarity;
	private final int octaves;
	private final float gain;
    private final float min;
    private final float max;
    private final float range;

    public LegacySimplex(float frequency, float lacunarity, int octaves, float gain) {
        super(frequency);
        this.lacunarity = lacunarity;
        this.octaves = octaves;
        this.gain = gain;
        this.min = -max(octaves, gain);
        this.max = max(octaves, gain);
        this.range = this.max - this.min;
    }

    @Override
    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;

        float sum = 0;
        float amp = 1;

        for (int i = 0; i < this.octaves; i++) {
            sum += Noise2D.singleLegacySimplex(x, y, seed + i) * amp;
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

        LegacySimplex that = (LegacySimplex) o;

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
	public Codec<LegacySimplex> codec() {
		return CODEC;
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
