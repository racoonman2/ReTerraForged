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
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.CellFunc;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;

public class Cell extends NoiseSource {
	public static final Codec<Cell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter((c) -> c.frequency),
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((c) -> c.lookup),
		CellFunc.CODEC.fieldOf("cell_func").forGetter((c) -> c.cellFunc),
		DistanceFunction.CODEC.fieldOf("dist_func").forGetter((c) -> c.distFunc),
		Codec.FLOAT.fieldOf("distance").forGetter((c) -> c.distance)
	).apply(instance, Cell::new));
	
    private final Noise lookup;
    private final CellFunc cellFunc;
    private final DistanceFunction distFunc;
    private final float distance;
    private final float min;
    private final float max;
    private final float range;

    public Cell(float frequency, Noise lookup, CellFunc cellFunc, DistanceFunction distFunc, float distance) {
    	super(frequency);
    	this.lookup = lookup;
        this.cellFunc = cellFunc;
        this.distFunc = distFunc;
        this.distance = distance;
        this.min = min(this.cellFunc, this.lookup);
        this.max = max(this.cellFunc, this.lookup);
        this.range = this.max - this.min;
    }

    @Override
    public float compute(float x, float y, int seed) {
        x *= frequency;
        y *= frequency;
        float value = single(x, y, seed, distance, cellFunc, distFunc, lookup);
        return cellFunc.mapValue(value, min, max, range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Cell cellNoise = (Cell) o;

        if (Float.compare(cellNoise.min, min) != 0) return false;
        if (Float.compare(cellNoise.max, max) != 0) return false;
        if (Float.compare(cellNoise.range, range) != 0) return false;
        if (Float.compare(cellNoise.distance, distance) != 0) return false;
        if (!lookup.equals(cellNoise.lookup)) return false;
        if (cellFunc != cellNoise.cellFunc) return false;
        return distFunc == cellNoise.distFunc;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + lookup.hashCode();
        result = 31 * result + cellFunc.hashCode();
        result = 31 * result + distFunc.hashCode();
        result = 31 * result + (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        result = 31 * result + (distance != +0.0f ? Float.floatToIntBits(distance) : 0);
        return result;
    }
    
    @Override
	public Codec<Cell> codec() {
		return CODEC;
	}
    
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Cell(this.frequency, this.lookup.mapAll(visitor), this.cellFunc, this.distFunc, this.distance));
	}
	
	public static float single(float x, float y, int seed, float distance, CellFunc cellFunc, DistanceFunction distanceFunc, Noise lookup) {
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);

        int cellX = xi;
        int cellY = yi;
        Vec2f vec2f = null;
        float nearest = Float.MAX_VALUE;

        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int cx = xi + dx;
                int cy = yi + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);

                float deltaX = cx + vec.x() * distance - x;
                float deltaY = cy + vec.y() * distance - y;
                float dist = distanceFunc.apply(deltaX, deltaY);

                if (dist < nearest) {
                    nearest = dist;
                    vec2f = vec;
                    cellX = cx;
                    cellY = cy;
                }
            }
        }

        return cellFunc.apply(cellX, cellY, nearest, seed, vec2f, lookup);
    }

    private static float min(CellFunc func, Noise lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.minValue();
        }
        if (func == CellFunc.DISTANCE) {
            return -1;
        }
        return -1;
    }

    private static float max(CellFunc func, Noise lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.maxValue();
        }
        if (func == CellFunc.DISTANCE) {
            return 0.25F;
        }
        return 1;
    }
}
