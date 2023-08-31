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
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class Grad extends Modifier {
	public static final Codec<Grad> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter((m) -> m.lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter((m) -> m.upper),
		Noise.HOLDER_HELPER_CODEC.fieldOf("strength").forGetter((m) -> m.strength)
	).apply(instance, Grad::new));

    private final Noise lower;
    private final Noise upper;
    private final Noise strength;

    public Grad(Noise source, Noise lower, Noise upper, Noise strength) {
        super(source);
        this.lower = lower;
        this.upper = upper;
        this.strength = strength;
    }
    
    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        float upperBound = upper.getValue(x, y, seed);
        if (noiseValue > upperBound) {
            return noiseValue;
        }

        float amount = strength.getValue(x, y, seed);
        float lowerBound = lower.getValue(x, y, seed);
        if (noiseValue < lowerBound) {
            return NoiseUtil.pow(noiseValue, 1 - amount);
        }

        float alpha = 1 - ((noiseValue - lowerBound) / (upperBound - lowerBound));
        float power = 1 - (amount * alpha);
        return NoiseUtil.pow(noiseValue, power);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Grad grad = (Grad) o;

        if (!lower.equals(grad.lower)) return false;
        if (!upper.equals(grad.upper)) return false;
        return strength.equals(grad.strength);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + lower.hashCode();
        result = 31 * result + upper.hashCode();
        result = 31 * result + strength.hashCode();
        return result;
    }
    
    @Override
	public Codec<Grad> codec() {
		return CODEC;
	}
}
