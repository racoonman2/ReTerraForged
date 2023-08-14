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

/**
 * @author dags <dags@dags.me>
 */
public class Cache extends Modifier {
	public static final Codec<Cache> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter((m) -> m.source)
	).apply(instance, Cache::new));
	
    private final Value value = new Value();

    public Cache(Noise source) {
        super(source);
    }

    @Override
    public float minValue() {
        return source.minValue();
    }

    @Override
    public float maxValue() {
        return source.maxValue();
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        return 0;
    }

    @Override
    public float getValue(float x, float y, int seed) {
        Value value = this.value;
        if (value.matches(x, y)) {
            return value.value;
        }
        return value.set(x, y, source.getValue(x, y, seed));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Cache cache = (Cache) o;

        return value.equals(cache.value);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
	public Codec<Cache> codec() {
		return CODEC;
	}
    
    private static class Value {

        private float x = 0;
        private float y = 0;
        private float value = 0;
        private boolean empty = true;

        private boolean matches(float x, float y) {
            return !empty && x == this.x && y == this.y;
        }

        private float set(float x, float y, float value) {
            this.x = x;
            this.y = y;
            this.value = value;
            this.empty = false;
            return value;
        }
    }
}
