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

package raccoonman.reterraforged.common.level.levelgen.noise.selector;

import java.util.List;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.combiner.Combiner;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

/**
 * @author dags <dags@dags.me>
 */
public abstract class Selector extends Combiner {

    protected final Noise control;
    protected final Interpolation interpolation;

    public Selector(Noise control, List<Noise> sources, Interpolation interpolation) {
        super(sources);
        this.control = control;
        this.interpolation = interpolation;
    }

    @Override
    public float getValue(float x, float y, int seed) {
        float select = control.getValue(x, y, seed);
        return selectValue(x, y, select, seed);
    }

    @Override
    protected float minTotal(float result, Noise next) {
        return Math.min(result, next.minValue());
    }

    @Override
    protected float maxTotal(float result, Noise next) {
        return Math.max(result, next.maxValue());
    }

    @Override
    protected float combine(float result, float value) {
        return 0;
    }

    protected float blendValues(float lower, float upper, float alpha) {
        alpha = interpolation.apply(alpha);
        return NoiseUtil.lerp(lower, upper, alpha);
    }

    protected abstract float selectValue(float x, float y, float selector, int seed);
}
