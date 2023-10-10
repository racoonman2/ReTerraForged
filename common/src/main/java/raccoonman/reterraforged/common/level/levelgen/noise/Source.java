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

package raccoonman.reterraforged.common.level.levelgen.noise;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.CellFunc;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Builder;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Constant;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;

//@Deprecated <- this should be deprecated but this has to stay for now because preset settings rely on it
public enum Source implements StringRepresentable {
    BILLOW(Builder::billow),
    CELL(Builder::cell),
    CELL_EDGE(Builder::cellEdge),
    CONSTANT(Builder::constant),
    CUBIC(Builder::cubic),
    PERLIN(Builder::perlin),
    PERLIN2(Builder::perlin2),
    RIDGE(Builder::ridge),
    SIMPLEX(Builder::simplex),
    SIMPLEX2(Builder::simplex2),
    SIMPLEX_RIDGE(Builder::simplexRidge),
    SIN(Builder::sin),
    RAND(Builder::rand);

	public static final Codec<Source> CODEC = StringRepresentable.fromEnum(Source::values);
	
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

    @Override
    public String getSerializedName() {
    	return this.name();
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder build(final int seed, final int scale, final int octaves) {
        return builder().shift(seed).scale(scale).octaves(octaves);
    }
    
    public static Noise perlin(final int seed, final double freq, final int octaves) {
        return builder().shift(seed).frequency(freq).octaves(octaves).perlin();
    }
    
    public static Noise perlin(final int seed, final int scale, final int octaves) {
        return builder().shift(seed).scale(scale).octaves(octaves).perlin();
    }
    
    public static Noise simplex(final int seed, final int scale, final int octaves) {
        return builder().shift(seed).scale(scale).octaves(octaves).simplex();
    }
    
    public static Noise billow(final int seed, final int scale, final int octaves) {
        return builder().shift(seed).scale(scale).octaves(octaves).billow();
    }
    
    public static Noise ridge(final int seed, final int scale, final int octaves) {
        return builder().shift(seed).scale(scale).octaves(octaves).ridge();
    }
    
    public static Noise simplexRidge(final int seed, final int scale, final int octaves) {
        return builder().shift(seed).scale(scale).octaves(octaves).simplexRidge();
    }
    
    public static Noise cubic(final int seed, final int scale, final int octaves) {
        return builder().shift(seed).scale(scale).octaves(octaves).cubic();
    }
    
    public static Noise cell(final int seed, final int scale) {
        return cell(seed, scale, CellFunc.CELL_VALUE);
    }
    
    public static Noise cell(final int seed, final int scale, final DistanceFunction distFunc) {
        return builder().shift(seed).scale(scale).distFunc(distFunc).cell();
    }
    
    public static Noise cell(final int seed, final int scale, final CellFunc cellFunc) {
        return builder().shift(seed).scale(scale).cellFunc(cellFunc).cell();
    }
    
    public static Noise cell(final int seed, final int scale, final DistanceFunction distFunc, final CellFunc cellFunc) {
        return builder().shift(seed).scale(scale).distFunc(distFunc).cellFunc(cellFunc).cell();
    }
    
    public static Noise cellNoise(final int seed, final int scale, final Noise source) {
        return builder().shift(seed).scale(scale).cellFunc(CellFunc.NOISE_LOOKUP).source(source).cell();
    }
    
    public static Noise cellNoise(final int seed, final int scale, final DistanceFunction distFunc, final Noise source) {
        return builder().shift(seed).scale(scale).cellFunc(CellFunc.NOISE_LOOKUP).distFunc(distFunc).source(source).cell();
    }
    
    public static Noise cellEdge(final int seed, final int scale) {
        return builder().shift(seed).scale(scale).cellEdge();
    }
    
    public static Noise cellEdge(final int seed, final int scale, final EdgeFunction func) {
        return builder().shift(seed).scale(scale).edgeFunc(func).cellEdge();
    }
    
    public static Noise cellEdge(final int seed, final int scale, final DistanceFunction distFunc, final EdgeFunction edgeFunc) {
        return builder().shift(seed).scale(scale).distFunc(distFunc).edgeFunc(edgeFunc).cellEdge();
    }
    
    public static Noise rand(final int seed, final int scale) {
        return build(seed, scale, 0).rand();
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
