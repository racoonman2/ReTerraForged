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

package raccoonman.reterraforged.common.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.func.CurveFunction;
import raccoonman.reterraforged.common.noise.func.MidPointCurve;

public class Curve extends Modifier {
	public static final Codec<Curve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter((m) -> m.source),
		CurveFunction.CODEC.fieldOf("func").forGetter((m) -> m.func)
	).apply(instance, Curve::new));
	
    private final CurveFunction func;

    public Curve(Noise source, CurveFunction func) {
        super(source);
        this.func = func;
    }

    public Curve(Noise source, float mid, float steepness) {
        this(source, new MidPointCurve(mid, steepness));
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        return func.apply(noiseValue);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Curve curve = (Curve) o;

        return func.equals(curve.func);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + func.hashCode();
        return result;
    }
    
    @Override
	public Codec<Curve> codec() {
		return CODEC;
	}
}
