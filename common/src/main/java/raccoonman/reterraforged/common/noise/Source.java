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

package raccoonman.reterraforged.common.noise;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.noise.func.CellFunc;
import raccoonman.reterraforged.common.noise.func.DistanceFunc;
import raccoonman.reterraforged.common.noise.func.EdgeFunc;
import raccoonman.reterraforged.common.noise.source.Builder;
import raccoonman.reterraforged.common.noise.source.Constant;
import raccoonman.reterraforged.common.noise.source.Line;
import raccoonman.reterraforged.common.noise.source.Rand;
import raccoonman.reterraforged.common.util.CodecUtil;

public enum Source {
    BILLOW(Builder::billow),
    CELL(Builder::cell),
    CELL_EDGE(Builder::cellEdge),
    CONST(Builder::constant),
    CUBIC(Builder::cubic),
    PERLIN(Builder::perlin),
    RIDGE(Builder::ridge),
    SIMPLEX(Builder::legacySimplex),
    SIMPLEX2(Builder::simplex),
    SIMPLEX_RIDGE(Builder::simplex),
    SIN(Builder::sin),
    RAND(Builder::rand);

	public static final Codec<Source> CODEC = CodecUtil.forEnum(Source::valueOf);
	
    public static final Noise ONE = new Constant(1F);
    public static final Noise ZERO = new Constant(0F);
    public static final Noise HALF = new Constant(0.5F);

    private final Function<Builder, Noise> fn;

    Source(Function<Builder, Noise> fn) {
        this.fn = fn;
    }

    public Noise build(Builder builder) {
        return fn.apply(builder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder build(int scale, int octaves) {
        return builder().scale(scale).octaves(octaves);
    }

	public static Noise perlin(double freq, int octaves) {
        return Source.builder().frequency(freq).octaves(octaves).perlin();
    }

    public static Noise perlin(int scale, int octaves) {
        return Source.builder().scale(scale).octaves(octaves).perlin();
    }

    public static Noise simplex(int scale, int octaves) {
        return Source.builder().scale(scale).octaves(octaves).legacySimplex();
    }

    public static Noise billow(int scale, int octaves) {
        return Source.builder().scale(scale).octaves(octaves).billow();
    }

    public static Noise ridge(int scale, int octaves) {
        return Source.builder().scale(scale).octaves(octaves).ridge();
    }

    public static Noise simplexRidge(int scale, int octaves) {
        return Source.builder().scale(scale).octaves(octaves).simplexRidge();
    }

    public static Noise cubic(int scale, int octaves) {
        return Source.builder().scale(scale).octaves(octaves).cubic();
    }

    public static Noise cell(int scale) {
        return Source.cell(scale, CellFunc.CELL_VALUE);
    }

    public static Noise cell(int scale, DistanceFunc distFunc) {
        return Source.builder().scale(scale).distFunc(distFunc).cell();
    }

    public static Noise cell(int scale, CellFunc cellFunc) {
        return Source.builder().scale(scale).cellFunc(cellFunc).cell();
    }

    public static Noise cell(int scale, DistanceFunc distFunc, CellFunc cellFunc) {
        return Source.builder().scale(scale).distFunc(distFunc).cellFunc(cellFunc).cell();
    }

    public static Noise cellNoise(int scale, Noise source) {
        return builder().scale(scale).cellFunc(CellFunc.NOISE_LOOKUP).source(source).cell();
    }

    public static Noise cellNoise(int scale, DistanceFunc distFunc, Noise source) {
        return builder().scale(scale)
                .cellFunc(CellFunc.NOISE_LOOKUP)
                .distFunc(distFunc)
                .source(source)
                .cell();
    }
    
    public static Noise cellEdge(int scale) {
        return Source.builder().scale(scale).cellEdge();
    }

    public static Noise cellEdge(int scale, EdgeFunc func) {
        return Source.builder().scale(scale).edgeFunc(func).cellEdge();
    }

    public static Noise cellEdge(int scale, DistanceFunc distFunc, EdgeFunc edgeFunc) {
        return Source.builder().scale(scale).distFunc(distFunc).edgeFunc(edgeFunc).cellEdge();
    }

    public static Rand rand(int scale) {
        return Source.build(scale, 0).rand();
    }

    public static Noise sin(int scale, Noise source) {
        return Source.builder().scale(scale).source(source).sin();
    }

    public static Line line(double x1, double y1, double x2, double y2, double radius, double fadeIn, double fadeOut) {
        return line(x1, y1, x2, y2, Source.constant(radius * radius), Source.constant(fadeIn), Source.constant(fadeOut));
    }

    public static Line line(double x1, double y1, double x2, double y2, double radius, double fadeIn, double fadeOut, double feather) {
        return line(x1, y1, x2, y2, Source.constant(radius * radius), Source.constant(fadeIn), Source.constant(fadeOut), feather);
    }

    public static Line line(double x1, double y1, double x2, double y2, Noise radius2, Noise fadeIn, Noise fadeOut) {
        return line(x1, y1, x2, y2, radius2, fadeIn, fadeOut, 0.33);
    }

    public static Line line(double x1, double y1, double x2, double y2, Noise radius2, Noise fadeIn, Noise fadeOut, double feather) {
        return new Line((float) x1, (float) y1, (float) x2, (float) y2, radius2, fadeIn, fadeOut, (float) feather);
    }

    public static Noise constant(double value) {
        if (value == 0) {
            return ZERO;
        }
        if (value == 0.5) {
            return HALF;
        }
        if (value == 1) {
            return ONE;
        }
        return new Constant((float) value);
    }
}
