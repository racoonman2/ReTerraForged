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

public class Alpha extends Modifier {
	public static final Codec<Alpha> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter((m) -> m.alpha)
	).apply(instance, Alpha::new));
	
    private final Noise alpha;

    public Alpha(Noise source, Noise alpha) {
        super(source);
        this.alpha = alpha;
    }
    
    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        float a = alpha.compute(x, y, seed);
        return (noiseValue * a) + (1 - a);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Alpha alpha1 = (Alpha) o;

        return alpha.equals(alpha1.alpha);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alpha.hashCode();
        return result;
    }
    
    @Override
	public Codec<Alpha> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Alpha(this.source.mapAll(visitor), this.alpha.mapAll(visitor)));
	}
}
