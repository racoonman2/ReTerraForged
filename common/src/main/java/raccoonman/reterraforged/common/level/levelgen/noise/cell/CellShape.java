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

package raccoonman.reterraforged.common.level.levelgen.noise.cell;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

//TODO this should just be a domain or something with the jitter param passed as a field
public enum CellShape implements StringRepresentable {
    SQUARE("square"),
    HEXAGON("hexagon") {
    	
        @Override
        public float adjustY(float y) {
            return y * 1.2F; // rough
        }

        @Override
        public float getCellX(int hash, int cx, int cy, float jitter) {
            float ox = (cy & 1) * 0.5F;
            float jx = ox > 0 ? jitter * 0.5F: jitter;
            return getPosX(hash, cx, jx) + ox;
        }
    };

	public static final Codec<CellShape> CODEC = StringRepresentable.fromEnum(CellShape::values);
	
	private String name;
	
	private CellShape(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
    public float adjustX(float x) {
        return x;
    }

    public float adjustY(float y) {
        return y;
    }

    public float getCellX(int cx, int cy, int seed, float jitter) {
        return getPosX(NoiseUtil.hash2D(seed, cx, cy), cx, jitter);
    }

    public float getCellY(int cx, int cy, int seed, float jitter) {
        return getPosY(NoiseUtil.hash2D(seed, cx, cy), cy, jitter);
    }
    
    private static float getPosX(int hash, int cx, float jitter) {
        float offset = NoiseUtil.rand(hash, NoiseUtil.X_PRIME);
        return cx + offset * jitter;
    }

    private static float getPosY(int hash, int cy, float jitter) {
        float offset = NoiseUtil.rand(hash, NoiseUtil.Y_PRIME);
        return cy + offset * jitter;
    }
}