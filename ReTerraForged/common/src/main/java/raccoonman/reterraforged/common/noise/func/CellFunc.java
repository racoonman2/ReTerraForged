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

package raccoonman.reterraforged.common.noise.func;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.noise.util.Vec2f;
import raccoonman.reterraforged.common.util.CodecUtil;

/**
 * @author dags <dags@dags.me>
 */
public enum CellFunc {
    CELL_VALUE {
        @Override
        public float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup) {
            return NoiseUtil.valCoord2D(seed, xc, yc);
        }
    },
    NOISE_LOOKUP {
        @Override
        public float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup) {
            return lookup.getValue(xc + vec2f.x, yc + vec2f.y, seed);
        }

        @Override
        public float mapValue(float value, float min, float max, float range) {
            return value;
        }
    },
    DISTANCE {
        @Override
        public float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup) {
            return distance - 1;
        }

        @Override
        public float mapValue(float value, float min, float max, float range) {
            return 0;
        }
    };
	
	public static final Codec<CellFunc> CODEC = CodecUtil.forEnum(CellFunc::valueOf);

    public abstract float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup);

    public float mapValue(float value, float min, float max, float range) {
        return NoiseUtil.map(value, min, max, range);
    }
}
