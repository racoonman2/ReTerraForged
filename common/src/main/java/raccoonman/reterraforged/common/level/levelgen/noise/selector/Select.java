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

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;

/**
 * @author dags <dags@dags.me>
 */
public class Select extends Selector {
	public static final Codec<Select> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("control").forGetter((s) -> s.control),
		Noise.HOLDER_HELPER_CODEC.fieldOf("source0").forGetter((s) -> s.source0),
		Noise.HOLDER_HELPER_CODEC.fieldOf("source1").forGetter((s) -> s.source1),
		Codec.FLOAT.optionalFieldOf("lower_bound", 0.0F).forGetter((s) -> s.lowerBound),
		Codec.FLOAT.optionalFieldOf("upper_bound", 1.0F).forGetter((s) -> s.upperBound),
		Codec.FLOAT.optionalFieldOf("falloff", 0.0F).forGetter((s) -> s.edgeFalloff),
		Interpolation.CODEC.optionalFieldOf("interp", Interpolation.LINEAR).forGetter((s) -> s.interpolation)
	).apply(instance, Select::new));

    protected final Noise control;
    protected final Noise source0;
    protected final Noise source1;

    protected final float lowerBound;
    protected final float upperBound;
    protected final float edgeFalloff;
    protected final float lowerCurveMin;
    protected final float lowerCurveMax;
    protected final float lowerCurveRange;
    protected final float upperCurveMin;
    protected final float upperCurveMax;
    protected final float upperCurveRange;

    public Select(Noise control, Noise source0, Noise source1, float lowerBound, float upperBound, float edgeFalloff, Interpolation interpolation) {
        super(control, ImmutableList.of(source0, source1), interpolation);
        this.control = control;
        this.source0 = source0;
        this.source1 = source1;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.edgeFalloff = edgeFalloff;
        this.lowerCurveMin = lowerBound - edgeFalloff;
        this.lowerCurveMax = lowerBound + edgeFalloff;
        this.lowerCurveRange = lowerCurveMax - lowerCurveMin;
        this.upperCurveMin = upperBound - edgeFalloff;
        this.upperCurveMax = upperBound + edgeFalloff;
        this.upperCurveRange = upperCurveMax - upperCurveMin;
    }
    
    @Override
    public float selectValue(float x, float y, float value, int seed) {
        if (edgeFalloff == 0) {
            if (value < lowerCurveMax) {
                return source0.getValue(x, y, seed);
            }

            if (value > upperCurveMin) {
                return source1.getValue(x, y, seed);
            }

            return source0.getValue(x, y, seed);
        }

        if (value < lowerCurveMin) {
            return source0.getValue(x, y, seed);
        }

        // curve
        if (value < lowerCurveMax) {
            float alpha = (value - lowerCurveMin) / lowerCurveRange;
            return blendValues(source0.getValue(x, y, seed), source1.getValue(x, y, seed), alpha);
        }

        if (value < upperCurveMin) {
            return source1.getValue(x, y, seed);
        }

        if (value < upperCurveMax) {
            float alpha = (value - upperCurveMin) / upperCurveRange;
            return blendValues(source1.getValue(x, y, seed), source0.getValue(x, y, seed), alpha);
        }

        return source0.getValue(x, y, seed);
    }
    
    @Override
	public Codec<Select> codec() {
		return CODEC;
	}
}
