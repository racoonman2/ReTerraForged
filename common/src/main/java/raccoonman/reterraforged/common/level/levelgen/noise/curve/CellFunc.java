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

package raccoonman.reterraforged.common.level.levelgen.noise.curve;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.util.Vec2f;

/**
 * @author dags <dags@dags.me>
 */
public enum CellFunc implements StringRepresentable {
    CELL_VALUE("cell_value") {
        @Override
        public float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup) {
            return NoiseUtil.valCoord2D(seed, xc, yc);
        }

		@Override
	    public float mapValue(float value, float min, float max, float range) {
	        return NoiseUtil.map(value, min, max, range);
	    }
    },
    NOISE_LOOKUP("noise_lookup") {
        @Override
        public float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup) {
            return lookup.compute(xc + vec2f.x(), yc + vec2f.y(), seed);
        }

        @Override
        public float mapValue(float value, float min, float max, float range) {
            return value;
        }
    },
    DISTANCE("distance") {
        @Override
        public float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup) {
            return distance - 1;
        }

        @Override
        public float mapValue(float value, float min, float max, float range) {
            return 0;
        }
    };
	
	public static final Codec<CellFunc> CODEC = StringRepresentable.fromEnum(CellFunc::values);
	private String name;
	
	private CellFunc(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
    public abstract float apply(int xc, int yc, float distance, int seed, Vec2f vec2f, Noise lookup);

    public abstract float mapValue(float value, float min, float max, float range);
}
