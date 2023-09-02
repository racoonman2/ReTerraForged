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

public class Constant implements Noise {
	public static final Codec<Constant> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("value").forGetter(Constant::value)
	).apply(instance, Constant::new));

	private float value;
	
	public Constant(float value) {
		this.value = value;
	}
	
	public float value() {
		return this.value;
	}
	
    @Override
    public float compute(float x, float y, int seed) {
        return this.value;
    }

    @Override
    public float minValue() {
        return this.value;
    }

    @Override
    public float maxValue() {
        return this.value;
    }
    
    @Override
	public Codec<Constant> codec() {
		return CODEC;
	}
    
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Constant(this.value));
	}
}
