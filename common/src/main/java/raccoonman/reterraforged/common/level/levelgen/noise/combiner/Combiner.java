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

package raccoonman.reterraforged.common.level.levelgen.noise.combiner;

import java.util.List;

import com.google.common.collect.ImmutableList;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

/**
 * @author dags <dags@dags.me>
 *
 * Combiners output values that may be the result of one or more input Modules
 */
public abstract class Combiner implements Noise {
    private final float min;
    private final float max;
    protected final List<Noise> modules;

    public Combiner(List<Noise> modules) {
    	modules = ImmutableList.copyOf(modules);
        this.modules = modules;
    	float min = 0F;
        float max = 0F;
        if (modules.size() > 0) {
            min = modules.get(0).minValue();
            max = modules.get(0).maxValue();
            for (int i = 1; i < modules.size(); i++) {
                Noise next = modules.get(i);
                min = this.minTotal(min, next);
                max = this.maxTotal(max, next);
            }
        }
        this.min = min;
        this.max = max;
    }

    @Override
    public float compute(float x, float y, int seed) {
        float result = 0.0F;
        if (this.modules.size() > 0) {
            result = this.modules.get(0).compute(x, y, seed);
            for (int i = 1; i < this.modules.size(); i++) {
                Noise module = this.modules.get(i);
                float value = module.compute(x, y, seed);
                result = this.combine(result, value);
            }
        }
        return result;
    }

    @Override
    public float minValue() {
        return this.min;
    }

    @Override
    public float maxValue() {
    	return this.max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;

        Combiner combiner = (Combiner) o;

        if (Float.compare(combiner.min, this.min) != 0) return false;
        if (Float.compare(combiner.max, this.max) != 0) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return combiner.modules.equals(this.modules);
    }

    @Override
    public int hashCode() {
        int result = (this.min != +0.0f ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + (this.max != +0.0f ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + this.modules.hashCode();
        return result;
    }

    protected abstract float minTotal(float result, Noise next);

    protected abstract float maxTotal(float result, Noise next);

    protected abstract float combine(float result, float value);
}
