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

import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunc;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.util.Noise2D;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class CellEdge extends BaseNoise {
	public static final Codec<CellEdge> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("frequency", Builder.DEFAULT_FREQUENCY).forGetter((n) -> n.frequency),
		EdgeFunction.CODEC.optionalFieldOf("edge_func", Builder.DEFAULT_EDGE_FUNC).forGetter((n) -> n.edgeFunc),
		DistanceFunc.CODEC.optionalFieldOf("dist_func", Builder.DEFAULT_DIST_FUNC).forGetter((n) -> n.distFunc),
		Codec.FLOAT.optionalFieldOf("distance", Builder.DEFAULT_DISTANCE).forGetter((n) -> n.distance)
	).apply(instance, CellEdge::new));
	
    private final EdgeFunction edgeFunc;
    private final DistanceFunc distFunc;
    private final float distance;

    public CellEdge(float frequency, EdgeFunction edgeFunc, DistanceFunc distFunc, float distance) {
    	super(frequency);
    	this.edgeFunc = edgeFunc;
        this.distFunc = distFunc;
        this.distance = distance;
    }
    
    @Override
    public float compute(float x, float y, int seed) {
        x *= frequency;
        y *= frequency;
        float value = Noise2D.cellEdge(x, y, seed, distance, edgeFunc, distFunc);
        return NoiseUtil.map(value, edgeFunc.min(), edgeFunc.max(), edgeFunc.range());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CellEdge that = (CellEdge) o;

        if (Float.compare(that.distance, distance) != 0) return false;
        if (edgeFunc != that.edgeFunc) return false;
        return distFunc == that.distFunc;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + edgeFunc.hashCode();
        result = 31 * result + distFunc.hashCode();
        result = 31 * result + (distance != +0.0f ? Float.floatToIntBits(distance) : 0);
        return result;
    }
    
    @Override
	public Codec<CellEdge> codec() {
		return CODEC;
	}
}
