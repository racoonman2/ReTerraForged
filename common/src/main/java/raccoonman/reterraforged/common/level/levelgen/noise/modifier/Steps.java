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
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.CurveFunction;

/**
 * @author dags <dags@dags.me>
 */
public class Steps extends Modifier {
	public static final Codec<Steps> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("steps").forGetter((m) -> m.steps),
		Noise.HOLDER_HELPER_CODEC.fieldOf("slope_min").forGetter((m) -> m.slopeMin),
		Noise.HOLDER_HELPER_CODEC.fieldOf("slope_max").forGetter((m) -> m.slopeMax),
		CurveFunction.CODEC.fieldOf("curve").forGetter((m) -> m.curve)
	).apply(instance, Steps::new));
	
    private final Noise steps;
    private final Noise slopeMin;
    private final Noise slopeMax;
    private final CurveFunction curve;

    public Steps(Noise source, Noise steps, Noise slopeMin, Noise slopeMax, CurveFunction slopeCurve) {
        super(source);
        this.steps = steps;
        this.curve = slopeCurve;
        this.slopeMin = slopeMin;
        this.slopeMax = slopeMax;
    }

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Steps(this.source.mapAll(visitor), this.steps.mapAll(visitor), this.slopeMin.mapAll(visitor), this.slopeMax.mapAll(visitor), this.curve));
	}
    
    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        float min = this.slopeMin.compute(x, y, seed);
        float max = this.slopeMax.compute(x, y, seed);
        float stepCount = this.steps.compute(x, y, seed);

        float range = max - min;
        if (range <= 0.0F) {
            // round the noise down to the nearest step height
            return (int) (noiseValue * stepCount) / stepCount;
        } else {
            // invert noise
            noiseValue = 1 - noiseValue;

            // round the noise down to the nearest step height
            float value = (int)(noiseValue * stepCount) / stepCount;

            // derive an alpha value from the difference between noise & step heights
            float delta = (noiseValue - value);

            // alpha is equal to the delta divided by the size of one step (ie delta / (1F / stepCount))
            // this can be simplified to delta * stepCount
            // map this to the defined blend range
            float alpha = NoiseUtil.map(delta * stepCount, min, max, range);

            // lerp from step to noise value with curve applied to alpha & un-invert the result
            return 1 - NoiseUtil.lerp(value, noiseValue, this.curve.apply(alpha));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Steps steps1 = (Steps) o;

        if (!steps.equals(steps1.steps)) return false;
        if (!slopeMin.equals(steps1.slopeMin)) return false;
        if (!slopeMax.equals(steps1.slopeMax)) return false;
        return curve.equals(steps1.curve);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + steps.hashCode();
        result = 31 * result + slopeMin.hashCode();
        result = 31 * result + slopeMax.hashCode();
        result = 31 * result + curve.hashCode();
        return result;
    }
    
    @Override
	public Codec<Steps> codec() {
		return CODEC;
	}
}
