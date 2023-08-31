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

package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;

/**
 * @author dags <dags@dags.me>
 */
public class Clamp extends Modifier {
	public static final Codec<Clamp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("min").forGetter((m) -> m.min),
		Noise.HOLDER_HELPER_CODEC.fieldOf("max").forGetter((m) -> m.max)
	).apply(instance, Clamp::new));

    private final Noise min;
    private final Noise max;

    public Clamp(Noise source, float min, float max) {
        this(source, Source.constant(min), Source.constant(max));
    }

    public Clamp(Noise source, Noise min, Noise max) {
        super(source);
        this.min = min;
        this.max = max;
    }

    @Override
    public float minValue() {
        return min.minValue();
    }

    @Override
    public float maxValue() {
        return max.maxValue();
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        float min = this.min.compute(x, y, seed);
        float max = this.max.compute(x, y, seed);
        if (noiseValue < min) {
            return min;
        }
        if (noiseValue > max) {
            return max;
        }
        return noiseValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Clamp clamp = (Clamp) o;

        if (!min.equals(clamp.min)) return false;
        return max.equals(clamp.max);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + min.hashCode();
        result = 31 * result + max.hashCode();
        return result;
    }
    
    @Override
	public Codec<Clamp> codec() {
		return CODEC;
	}
}
