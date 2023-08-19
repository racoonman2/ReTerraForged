/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
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

package raccoonman.reterraforged.common.level.levelgen.cell;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.util.CodecUtil;
import raccoonman.reterraforged.common.util.MathUtil;

public enum CellShape {
    SQUARE,
    HEXAGON {
        @Override
        public float adjustY(float y) {
            return y * 1.2f; // rough
        }

        @Override
        public float getCellX(int hash, int cx, int cy, float jitter) {
            float ox = (cy & 1) * 0.5f;
            float jx = ox > 0 ? jitter * 0.5f: jitter;
            return MathUtil.getPosX(hash, cx, jx) + ox;
        }
    };

	public static final Codec<CellShape> CODEC = CodecUtil.forEnum(CellShape::valueOf);
	
    public float adjustX(float x) {
        return x;
    }

    public float adjustY(float y) {
        return y;
    }

    public float getCellX(int hash, int cx, int cy, float jitter) {
        return MathUtil.getPosX(hash, cx, jitter);
    }

    public float getCellY(int hash, int cx, int cy, float jitter) {
        return MathUtil.getPosY(hash, cy, jitter);
    }
}