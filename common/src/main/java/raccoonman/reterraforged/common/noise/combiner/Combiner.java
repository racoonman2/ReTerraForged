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

package raccoonman.reterraforged.common.noise.combiner;

import java.util.Arrays;

import raccoonman.reterraforged.common.noise.Noise;

/**
 * @author dags <dags@dags.me>
 *
 * Combiners output values that may be the result of one or more input Modules
 */
public abstract class Combiner implements Noise {
    private final float min;
    private final float max;
    protected final Noise[] sources;

    public Combiner(Noise... sources) {
        float min = 0F;
        float max = 0F;
        if (sources.length > 0) {
            min = sources[0].minValue();
            max = sources[0].maxValue();
            for (int i = 1; i < sources.length; i++) {
                Noise next = sources[i];
                min = minTotal(min, next);
                max = maxTotal(max, next);
            }
        }
        this.min = min;
        this.max = max;
        this.sources = sources;
    }

    @Override
    public float getValue(float x, float y, int seed) {
        float result = 0F;
        if (sources.length > 0) {
            result = sources[0].getValue(x, y, seed);
            for (int i = 1; i < sources.length; i++) {
                Noise module = sources[i];
                float value = module.getValue(x, y, seed);
                result = combine(result, value);
            }
        }
        return result;
    }

    @Override
    public float minValue() {
        return min;
    }

    @Override
    public float maxValue() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Combiner combiner = (Combiner) o;

        if (Float.compare(combiner.min, min) != 0) return false;
        if (Float.compare(combiner.max, max) != 0) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(sources, combiner.sources);
    }

    @Override
    public int hashCode() {
        int result = (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + Arrays.hashCode(sources);
        return result;
    }

    protected abstract float minTotal(float result, Noise next);

    protected abstract float maxTotal(float result, Noise next);

    protected abstract float combine(float result, float value);
}
