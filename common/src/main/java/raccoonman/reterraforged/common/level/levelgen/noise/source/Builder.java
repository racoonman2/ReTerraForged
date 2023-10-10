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

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.CellFunc;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;

/**
 * @author dags <dags@dags.me>
 */
@Deprecated
public class Builder {
	public static final int DEFAULT_SHIFT = 0;
    public static final int DEFAULT_OCTAVES = 1;
    public static final float DEFAULT_GAIN = 0.5F;
    public static final float DEFAULT_RIDGE_GAIN = 0.975F;
    public static final float DEFAULT_LACUNARITY = 2F;
    public static final float DEFAULT_FREQUENCY = 1F;
    public static final float DEFAULT_DISTANCE = 1F;
    public static final CellFunc DEFAULT_CELL_FUNC = CellFunc.CELL_VALUE;
    public static final EdgeFunction DEFAULT_EDGE_FUNC = EdgeFunction.DISTANCE_2;
    public static final DistanceFunction DEFAULT_DIST_FUNC = DistanceFunction.EUCLIDEAN;
    public static final Interpolation DEFAULT_INTERPOLATION = Interpolation.CURVE3;

    private int shift = DEFAULT_SHIFT;
    private int octaves = DEFAULT_OCTAVES;
    private float gain = Float.MAX_VALUE;
    private float lacunarity = DEFAULT_LACUNARITY;
    private float frequency = DEFAULT_FREQUENCY;
    private float distance = DEFAULT_DISTANCE;
    private Noise source = Source.ZERO;
    private CellFunc cellFunc = DEFAULT_CELL_FUNC;
    private EdgeFunction edgeFunc = DEFAULT_EDGE_FUNC;
    private DistanceFunction distFunc = DEFAULT_DIST_FUNC;
    private Interpolation interpolation = DEFAULT_INTERPOLATION;

    public int getShift() {
    	return shift;
    }
    
    public int getOctaves() {
        return octaves;
    }

    public float getGain() {
        if (gain == Float.MAX_VALUE) {
            gain = DEFAULT_GAIN;
        }
        return gain;
    }

    public float getFrequency() {
        return frequency;
    }

    public float getDisplacement() {
        return distance;
    }

    public float getLacunarity() {
        return lacunarity;
    }

    public Interpolation getInterp() {
        return interpolation;
    }

    public CellFunc getCellFunc() {
        return cellFunc;
    }

    public EdgeFunction getEdgeFunc() {
        return edgeFunc;
    }

    public DistanceFunction getDistFunc() {
        return distFunc;
    }

    public Noise getSource() {
        return source;
    }
    
    public Builder shift(int shift) {
    	this.shift = shift;
    	return this;
    }

    public Builder octaves(int octaves) {
        this.octaves = octaves;
        return this;
    }

    public Builder gain(double gain) {
        this.gain = (float) gain;
        return this;
    }

    public Builder lacunarity(double lacunarity) {
        this.lacunarity = (float) lacunarity;
        return this;
    }

    public Builder scale(int frequency) {
        this.frequency = 1F / frequency;
        return this;
    }

    public Builder frequency(double frequency) {
        this.frequency = (float) frequency;
        return this;
    }

    public Builder displacement(double displacement) {
        this.distance = (float) displacement;
        return this;
    }

    public Builder interp(Interpolation interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    public Builder cellFunc(CellFunc cellFunc) {
        this.cellFunc = cellFunc;
        return this;
    }

    public Builder edgeFunc(EdgeFunction cellType) {
        this.edgeFunc = cellType;
        return this;
    }

    public Builder distFunc(DistanceFunction cellDistance) {
        this.distFunc = cellDistance;
        return this;
    }

    public Builder source(Noise source) {
        this.source = source;
        return this;
    }

    public Noise perlin() {
        return new Perlin(this.frequency, this.lacunarity, this.interpolation, this.octaves, this.getGain()).shift(this.shift);
    }
    
    public Noise perlin2() {
        return new Perlin2(this.frequency, this.lacunarity, this.interpolation, this.octaves, this.getGain()).shift(this.shift);
    }

    public Noise simplex() {
        return new Simplex(this.frequency, this.lacunarity, this.octaves, this.getGain()).shift(this.shift);
    }

    public Noise simplex2() {
        return new Simplex2(this.frequency, this.lacunarity, this.octaves, this.getGain()).shift(this.shift);
    }

    public Noise ridge() {
        return new Ridge(this.frequency, this.interpolation, this.lacunarity, this.octaves, this.getGain()).shift(this.shift);
    }

    public Noise simplexRidge() {
        return new SimplexRidge(this.frequency, this.lacunarity, this.octaves, this.getGain()).shift(this.shift);
    }

    public Noise billow() {
        return new Billow(this.frequency, this.interpolation, this.lacunarity, this.octaves, this.getGain()).shift(this.shift);
    }

    public Noise cubic() {
        return new Cubic(this.frequency, this.lacunarity, this.octaves, this.getGain()).shift(this.shift);
    }

    public Noise cell() {
        return new Cell(this.frequency, this.source, this.cellFunc, this.distFunc, this.distance).shift(this.shift);
    }

    public Noise cellEdge() {
        return new CellEdge(this.frequency, this.edgeFunc, this.distFunc, this.distance).shift(this.shift);
    }

    public Noise sin() {
        return new Sin(this.frequency, this.source).shift(this.shift);
    }

    public Noise constant() {
        return new Constant(this.frequency).shift(this.shift);
    }

    public Noise rand() {
        return new Rand(this.frequency).shift(this.shift);
    }

    public Noise build(Source source) {
        return source.build(this);
    }
}
