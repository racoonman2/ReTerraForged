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

package raccoonman.reterraforged.common.level.levelgen.noise.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class Sin extends BaseNoise {
	public static final Codec<Sin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter((n) -> n.alpha)
	).apply(instance, Sin::new));
	
    private final Noise alpha;

    public Sin(float frequency, Noise alpha) {
    	super(frequency);
    	this.alpha = alpha;
    }
    
    @Override
    public float compute(float x, float y, int seed) {
        float a = alpha.compute(x, y, seed);
        x *= frequency;
        y *= frequency;

        final float noise;
        if (a == 0) {
            noise = NoiseUtil.sin(x);
        } else if (a == 1) {
            noise =NoiseUtil.sin(y);
        } else {
            float sx = NoiseUtil.sin(x);
            float sy = NoiseUtil.sin(y);
            noise = NoiseUtil.lerp(sx, sy, a);
        }

        return NoiseUtil.map(noise, -1, 1, 2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Sin sin = (Sin) o;

        return alpha.equals(sin.alpha);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + alpha.hashCode();
        return result;
    }
    
    @Override
	public Codec<Sin> codec() {
		return CODEC;
	}
}
