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

package raccoonman.reterraforged.common.noise.source;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.func.CellFunc;
import raccoonman.reterraforged.common.noise.func.DistanceFunc;
import raccoonman.reterraforged.common.noise.func.EdgeFunc;
import raccoonman.reterraforged.common.noise.func.Interpolation;

/**
 * @author dags <dags@dags.me>
 */

public class Builder {
    public static final int DEFAULT_OCTAVES = 1;
    public static final float DEFAULT_GAIN = 0.5F;
    public static final float DEFAULT_RIDGE_GAIN = 0.975F;
    public static final float DEFAULT_LACUNARITY = 2F;
    public static final float DEFAULT_FREQUENCY = 1F;
    public static final float DEFAULT_DISTANCE = 1F;
    public static final CellFunc DEFAULT_CELL_FUNC = CellFunc.CELL_VALUE;
    public static final EdgeFunc DEFAULT_EDGE_FUNC = EdgeFunc.DISTANCE_2;
    public static final DistanceFunc DEFAULT_DIST_FUNC = DistanceFunc.EUCLIDEAN;
    public static final Interpolation DEFAULT_INTERPOLATION = Interpolation.CURVE3;

    private int octaves = DEFAULT_OCTAVES;
    private float gain = Float.MAX_VALUE;
    private float lacunarity = DEFAULT_LACUNARITY;
    private float frequency = DEFAULT_FREQUENCY;
    private float distance = DEFAULT_DISTANCE;
    private Noise source = Source.ZERO;
    private CellFunc cellFunc = DEFAULT_CELL_FUNC;
    private EdgeFunc edgeFunc = DEFAULT_EDGE_FUNC;
    private DistanceFunc distFunc = DEFAULT_DIST_FUNC;
    private Interpolation interpolation = DEFAULT_INTERPOLATION;

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

    public EdgeFunc getEdgeFunc() {
        return edgeFunc;
    }

    public DistanceFunc getDistFunc() {
        return distFunc;
    }

    public Noise getSource() {
        return source;
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

    public Builder edgeFunc(EdgeFunc cellType) {
        this.edgeFunc = cellType;
        return this;
    }

    public Builder distFunc(DistanceFunc cellDistance) {
        this.distFunc = cellDistance;
        return this;
    }

    public Builder source(Noise source) {
        this.source = source;
        return this;
    }

    public BaseNoise perlin() {
        return new Perlin(this.frequency, this.lacunarity, this.interpolation, this.octaves, this.getGain());
    }

    public BaseNoise legacySimplex() {
        return new LegacySimplex(this.frequency, this.lacunarity, this.octaves, this.getGain());
    }

    public BaseNoise simplex() {
        return new Simplex(this.frequency, this.lacunarity, this.octaves, this.getGain());
    }

    public BaseNoise ridge() {
        return new Ridge(this.frequency, this.interpolation, this.lacunarity, this.octaves, this.getGain());
    }

    public BaseNoise simplexRidge() {
        return new SimplexRidge(this.frequency, this.lacunarity, this.octaves, this.getGain());
    }

    public BaseNoise billow() {
        return new Billow(this.frequency, this.interpolation, this.lacunarity, this.octaves, this.getGain());
    }

    public BaseNoise cubic() {
        return new Cubic(this.frequency, this.lacunarity, this.octaves, this.getGain());
    }

    public BaseNoise cell() {
        return new Cell(this.frequency, this.source, this.cellFunc, this.distFunc, this.distance);
    }

    public BaseNoise cellEdge() {
        return new CellEdge(this.frequency, this.edgeFunc, this.distFunc, this.distance);
    }

    public BaseNoise sin() {
        return new Sin(this.frequency, this.source);
    }

    public Noise constant() {
        return new Constant(this.frequency);
    }

    public Rand rand() {
        return new Rand(this.frequency);
    }

    public Noise build(Source source) {
        return source.build(this);
    }
}
