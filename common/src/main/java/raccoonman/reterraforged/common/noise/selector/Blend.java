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
public class Blend extends Selector {
	public static final Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("control").forGetter((s) -> s.control),
		Noise.DIRECT_CODEC.fieldOf("source0").forGetter((s) -> s.source0),
		Noise.DIRECT_CODEC.fieldOf("source1").forGetter((s) -> s.source1),
		Codec.FLOAT.optionalFieldOf("midpoint", 0.5F).forGetter((s) -> s.midpoint),
		Codec.FLOAT.optionalFieldOf("blendRange", 0.0F).forGetter((s) -> s.blendRange),
		Interpolation.CODEC.optionalFieldOf("interpolation", Interpolation.LINEAR).forGetter((s) -> s.interpolation)
	).apply(instance, Blend::new));
	
    protected final Noise source0;
    protected final Noise source1;
    protected final float blend;
    protected final float midpoint;
    protected final float blendLower;
    protected final float blendUpper;
    protected final float blendRange;

    public Blend(Noise control, Noise source0, Noise source1, float midPoint, float blendRange, Interpolation interpolation) {
        super(control, new Noise[]{source0, source1}, interpolation);
        float mid = control.minValue() + ((control.maxValue() - control.minValue()) * midPoint);
        this.blend = blendRange;
        this.source0 = source0;
        this.source1 = source1;
        this.midpoint = midPoint;
        this.blendLower = Math.max(control.minValue(), mid - (blendRange / 2F));
        this.blendUpper = Math.min(control.maxValue(), mid + (blendRange / 2F));
        this.blendRange = blendUpper - blendLower;
    }

    @Override
    public float selectValue(float x, float y, float select, int seed) {
        if (select < blendLower) {
            return source0.getValue(x, y, seed);
        }
        if (select > blendUpper) {
            return source1.getValue(x, y, seed);
        }
        float alpha = (select - blendLower) / blendRange;
        return blendValues(source0.getValue(x, y, seed), source1.getValue(x, y, seed), alpha);
    }
    
    @Override
	public Codec<Blend> codec() {
		return CODEC;
	}
}
