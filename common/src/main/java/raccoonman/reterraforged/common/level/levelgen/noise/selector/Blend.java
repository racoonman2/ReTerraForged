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
public class Blend extends Selector {
	public static final Codec<Blend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("control").forGetter((s) -> s.control),
		Noise.HOLDER_HELPER_CODEC.fieldOf("source0").forGetter((s) -> s.source0),
		Noise.HOLDER_HELPER_CODEC.fieldOf("source1").forGetter((s) -> s.source1),
		Codec.FLOAT.optionalFieldOf("midpoint", 0.5F).forGetter((s) -> s.midpoint),
		Codec.FLOAT.optionalFieldOf("blendRange", 0.0F).forGetter((s) -> s.blend),
		Interpolation.CODEC.optionalFieldOf("interpolation", Interpolation.LINEAR).forGetter((s) -> s.interpolation)
	).apply(instance, Blend::new));
	
    protected final Noise source0;
    protected final Noise source1;
    protected final float blend;
    protected final float midpoint;

    public Blend(Noise control, Noise source0, Noise source1, float midPoint, float blend, Interpolation interpolation) {
        super(control, ImmutableList.of(source0, source1), interpolation);
        this.blend = blend;
        this.source0 = source0;
        this.source1 = source1;
        this.midpoint = midPoint;
    }

    @Override
    public float selectValue(float x, float y, float select, int seed) {
        float mid = this.control.minValue() + ((this.control.maxValue() - this.control.minValue()) * this.midpoint);
        float blendLower = Math.max(this.control.minValue(), mid - (this.blend / 2.0F));
        float blendUpper = Math.min(this.control.maxValue(), mid + (this.blend / 2.0F));
        float blendRange = blendUpper - blendLower;
        if (select < blendLower) {
            return this.source0.compute(x, y, seed);
        }
        if (select > blendUpper) {
            return this.source1.compute(x, y, seed);
        }
        float alpha = (select - blendLower) / blendRange;
        return this.blendValues(this.source0.compute(x, y, seed), this.source1.compute(x, y, seed), alpha);
    }
    
    @Override
	public Codec<Blend> codec() {
		return CODEC;
	}
    
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Blend(this.control.mapAll(visitor), this.source0.mapAll(visitor), this.source1.mapAll(visitor), this.midpoint, this.blend, this.interpolation));
	}
}
