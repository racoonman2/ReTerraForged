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

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public class DomainWarp implements Domain {
	public static final Codec<DomainWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("x").forGetter((d) -> d.originalX),
		Noise.HOLDER_HELPER_CODEC.fieldOf("y").forGetter((d) -> d.originalY),
		Noise.HOLDER_HELPER_CODEC.fieldOf("distance").forGetter((d) -> d.distance)
	).apply(instance, DomainWarp::new));
	
	private final Noise originalX, originalY;
    private final Noise x;
    private final Noise y;
    private final Noise distance;

    public DomainWarp(Noise x, Noise y, Noise distance) {
    	this.originalX = x;
    	this.originalY = y;
    	this.x = map(x);
        this.y = map(y);
        this.distance = distance;
    }
    
    @Override
    public float getOffsetX(float x, float y, int seed) {
        return this.x.getValue(x, y, seed) * this.distance.getValue(x, y, seed);
    }

    @Override
    public float getOffsetY(float x, float y, int seed) {
        return this.y.getValue(x, y, seed) * this.distance.getValue(x, y, seed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainWarp that = (DomainWarp) o;

        if (!x.equals(that.x)) return false;
        if (!y.equals(that.y)) return false;
        return distance.equals(that.distance);
    }

    @Override
    public int hashCode() {
        int result = x.hashCode();
        result = 31 * result + y.hashCode();
        result = 31 * result + distance.hashCode();
        return result;
    }

	@Override
	public Codec<DomainWarp> codec() {
		return CODEC;
	}

    // map the module to the range -0.5 to 0.5
    // this ensures warping occurs in the positive and negative directions
    private static Noise map(Noise in) {
        return in.map(-0.5, 0.5);
    }
}
