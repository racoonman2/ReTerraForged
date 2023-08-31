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

package raccoonman.reterraforged.common.level.levelgen.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class AddWarp implements Domain {
	public static final Codec<AddWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("warp_1").forGetter((d) -> d.a),
		Domain.CODEC.fieldOf("warp_2").forGetter((d) -> d.b)
	).apply(instance, AddWarp::new));
	
    private final Domain a;
    private final Domain b;

    public AddWarp(Domain a, Domain b) {
        this.a = a;
        this.b = b;
    }
    
    @Override
    public float getOffsetX(float x, float y, int seed) {
        return a.getOffsetX(x, y, seed) + b.getOffsetX(x, y, seed);
    }

    @Override
    public float getOffsetY(float x, float y, int seed) {
        return a.getOffsetY(x, y, seed) + b.getOffsetY(x, y, seed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddWarp addWarp = (AddWarp) o;

        if (!a.equals(addWarp.a)) return false;
        return b.equals(addWarp.b);
    }

    @Override
    public int hashCode() {
        int result = a.hashCode();
        result = 31 * result + b.hashCode();
        return result;
    }

	@Override
	public Codec<AddWarp> codec() {
		return CODEC;
	}
}
