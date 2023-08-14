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

package raccoonman.reterraforged.common.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class DirectionWarp implements Domain {
	public static final Codec<DirectionWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("direction").forGetter((d) -> d.direction),
		Noise.DIRECT_CODEC.fieldOf("strength").forGetter((d) -> d.strength)
	).apply(instance, DirectionWarp::new));
	
    private final Noise direction;
    private final Noise strength;

    public DirectionWarp(Noise direction, Noise strength) {
        this.direction = direction;
        this.strength = strength;
    }
    
    @Override
    public float getOffsetX(float x, float y, int seed) {
        float angle = direction.getValue(x, y, seed) * NoiseUtil.PI2;
        return NoiseUtil.sin(angle) * strength.getValue(x, y, seed);
    }

    @Override
    public float getOffsetY(float x, float y, int seed) {
        float angle = direction.getValue(x, y, seed) * NoiseUtil.PI2;
        return NoiseUtil.cos(angle) * strength.getValue(x, y, seed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DirectionWarp that = (DirectionWarp) o;

        if (!direction.equals(that.direction)) return false;
        return strength.equals(that.strength);
    }

    @Override
    public int hashCode() {
        int result = direction.hashCode();
        result = 31 * result + strength.hashCode();
        return result;
    }
    
	@Override
	public Codec<DirectionWarp> codec() {
		return CODEC;
	}
}
