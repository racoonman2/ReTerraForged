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

package raccoonman.reterraforged.common.noise.selector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.func.Interpolation;

/**
 * @author dags <dags@dags.me>
 */
public class Base extends Selector {
	public static final Codec<Base> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("base").forGetter((s) -> s.base),
		Noise.DIRECT_CODEC.fieldOf("control").forGetter((s) -> s.control),
		Codec.FLOAT.optionalFieldOf("falloff", 0.0F).forGetter((s) -> s.falloff),
		Interpolation.CODEC.optionalFieldOf("interpolation", Interpolation.LINEAR).forGetter((s) -> s.interpolation)
	).apply(instance, Base::new));
	
    private final Noise base;
    protected final float min;
    protected final float max;
    protected final float maxValue;
    protected final float falloff;

    public Base(Noise base, Noise control, float falloff, Interpolation interpolation) {
        super(control, new Noise[] { base, control }, interpolation);
        this.base = base;
        this.min = base.maxValue();
        this.max = base.maxValue() + falloff;
        this.falloff = falloff;
        this.maxValue = Math.max(base.maxValue(), control.maxValue());
    }
    
    @Override
    protected float selectValue(float x, float y, float upperValue, int seed) {
        if (upperValue < max) {
            float lowerValue = base.getValue(x, y, seed);
            if (falloff > 0) {
                float clamp = Math.max(min, upperValue);
                float alpha = (max - clamp) / falloff;
                return blendValues(upperValue, lowerValue, alpha);
            }
            return lowerValue;
        }
        return upperValue;
    }

    @Override
    public float minValue() {
        return base.minValue();
    }

    @Override
    public float maxValue() {
        return maxValue;
    }

	@Override
	public Codec<Base> codec() {
		return CODEC;
	}
}
