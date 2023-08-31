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
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class LegacyTerrace extends Modifier {
	public static final Codec<LegacyTerrace> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower_curve").forGetter((m) -> m.lowerCurve),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper_curve").forGetter((m) -> m.upperCurve),
		Codec.INT.optionalFieldOf("steps", 1).forGetter((m) -> m.steps.length),
		Codec.FLOAT.optionalFieldOf("blend_range", 1.0F).forGetter((m) -> m.blend)
	).apply(instance, LegacyTerrace::new));
	
    private final int maxIndex;
    private final float blend;
    private final Step[] steps;
    private final Noise lowerCurve;
    private final Noise upperCurve;

    public LegacyTerrace(Noise source, Noise lowerCurve, Noise upperCurve, int steps, float blendRange) {
        super(source);
        this.blend = blendRange;
        this.maxIndex = steps - 1;
        this.steps = new Step[steps];
        this.lowerCurve = lowerCurve;
        this.upperCurve = upperCurve;
        float min = source.minValue();
        float max = source.maxValue();
        float range = max - min;
        float spacing = range / (steps - 1);

        for (int i = 0; i < steps; i++) {
            float value = i * spacing;
            this.steps[i] = new Step(value, spacing, blendRange);
        }
    }

    @Override
    public float compute(float x, float y, int seed) {
        float value = source.compute(x, y, seed);
        value = NoiseUtil.clamp(value, 0, 1);
        return modify(x, y, value, seed);
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        int index = NoiseUtil.round(noiseValue * maxIndex);
        Step step = steps[index];
        if (noiseValue < step.lowerBound) {
            if (index > 0) {
                Step lower = steps[index - 1];
                float alpha = (noiseValue - lower.upperBound) / (step.lowerBound - lower.upperBound);
                alpha = 1 - Interpolation.CURVE3.apply(alpha);
                float range = step.value - lower.value;
                return step.value - (alpha * range * upperCurve.compute(x, y, seed));
            }
        } else if (noiseValue > step.upperBound) {
            if (index < maxIndex) {
                Step upper = steps[index + 1];
                float alpha = (noiseValue - step.upperBound) / (upper.lowerBound - step.upperBound);
                alpha = Interpolation.CURVE3.apply(alpha);
                float range = upper.value - step.value;
                return step.value + (alpha * range * lowerCurve.compute(x, y, seed));
            }
        }
        return step.value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        LegacyTerrace that = (LegacyTerrace) o;

        if (maxIndex != that.maxIndex) return false;
        if (Float.compare(that.blend, blend) != 0) return false;
        if (!lowerCurve.equals(that.lowerCurve)) return false;
        return upperCurve.equals(that.upperCurve);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + maxIndex;
        result = 31 * result + (blend != +0.0f ? Float.floatToIntBits(blend) : 0);
        result = 31 * result + lowerCurve.hashCode();
        result = 31 * result + upperCurve.hashCode();
        return result;
    }
    
    @Override
	public Codec<LegacyTerrace> codec() {
		return CODEC;
	}
    
    private static class Step {

        private final float value;
        private final float lowerBound;
        private final float upperBound;

        private Step(float value, float distance, float blendRange) {
            this.value = value;
            float blend = distance * blendRange;
            float bound = (distance - blend) / 2F;
            lowerBound = value - bound;
            upperBound = value + bound;
        }
    }
}
