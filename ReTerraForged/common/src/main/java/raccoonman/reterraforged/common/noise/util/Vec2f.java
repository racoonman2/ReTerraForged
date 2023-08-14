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

package raccoonman.reterraforged.common.noise.util;

import java.util.Objects;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class Vec2f {
	public static final Codec<Vec2f> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("x", 0.0F).forGetter((v) -> v.x),
		Codec.FLOAT.optionalFieldOf("y", 0.0F).forGetter((v) -> v.y)
	).apply(instance, Vec2f::new));
	
    public static final Vec2f ZERO = new Vec2f(0, 0);

    public final float x;
    public final float y;

    public Vec2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public int getBlockX() {
        return (int) x;
    }

    public int getBlockY() {
        return (int) y;
    }

    public Vec2f add(float x, float y) {
        return new Vec2f(this.x + x, this.y + y);
    }

    public float dist2(float x, float y) {
        float dx = this.x - x;
        float dy = this.y - y;
        return (dx * dx) + (dy * dy);
    }

    public Vec2i toInt() {
        return new Vec2i(getBlockX(), getBlockY());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2f vec2f = (Vec2f) o;
        return Float.compare(vec2f.x, x) == 0 &&
                Float.compare(vec2f.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
