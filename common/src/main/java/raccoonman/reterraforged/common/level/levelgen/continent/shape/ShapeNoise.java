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

package raccoonman.reterraforged.common.level.levelgen.continent.shape;

import raccoonman.reterraforged.common.level.levelgen.cell.CellPoint;
import raccoonman.reterraforged.common.level.levelgen.continent.ContinentCellNoise;
import raccoonman.reterraforged.common.level.levelgen.continent.ContinentPoints;
import raccoonman.reterraforged.common.level.levelgen.continent.config.ContinentConfig;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.TerrainSample;
import raccoonman.reterraforged.common.level.levelgen.settings.ControlPoints;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.pos.PosUtil;

public class ShapeNoise {
    private static final int RADIUS = 2;

    private final float baseFalloff;
    private final float continentFalloff;

    public final float threshold;
    public final float baseFalloffMin;
    public final float baseFalloffMax;

    private final ContinentCellNoise continent;
    private final FalloffPoint[] falloffPoints;
    private final ThreadLocal<CellLocal[]> cellBuffer = ThreadLocal.withInitial(CellLocal::init);

    public ShapeNoise(ContinentCellNoise continent, ContinentConfig config, ControlPoints controlPoints) {
        this.continent = continent;
        this.baseFalloff = config.noise.baseNoiseFalloff;
        this.continentFalloff = config.noise.continentNoiseFalloff;
        this.falloffPoints = ContinentPoints.getFalloff(controlPoints);
        this.threshold = config.shape.threshold;
        this.baseFalloffMin = config.shape.threshold + config.shape.baseFalloffMin;
        this.baseFalloffMax = config.shape.threshold + config.shape.baseFalloffMax;
    }

    public float getThresholdValue(CellPoint cell) {
        return cell.noise < this.threshold ? 0F : 1F;
    }

    public float getFalloff(float continentNoise) {
        return ContinentPoints.getFalloff(continentNoise, falloffPoints);
    }

    public float getBaseNoise(float value) {
        float min = baseFalloffMin;
        float max = baseFalloffMax;
        return NoiseUtil.map(value, min, max, max - min);
    }

    @Deprecated
    public TerrainSample sample(float x, float y, TerrainSample sample, int seed) {
        var centre = continent.getNearestCell(seed, x, y);
        int centreX = PosUtil.unpackLeft(centre);
        int centreY = PosUtil.unpackRight(centre);

        // Note: Must adjust inputs AFTER getting nearest cell
        x = continent.cellShape.adjustX(x);
        y = continent.cellShape.adjustY(y);

        int minX = centreX - RADIUS;
        int minY = centreY - RADIUS;
        int maxX = centreX + RADIUS;
        int maxY = centreY + RADIUS;

        float min0 = Float.MAX_VALUE;
        float min1 = Float.MAX_VALUE;
        var buffer = cellBuffer.get();

        for (int cy = minY, i = 0; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++, i++) {
                var cell = continent.getCell(seed, cx, cy);
                var local = buffer[i];

                float distance = NoiseUtil.sqrt(NoiseUtil.dist2(x, y, cell.px, cell.py));

                local.cell = cell;
                local.context = distance;

                if (distance < min0) {
                    min1 = min0;
                    min0 = distance;
                } else if (distance < min1) {
                    min1 = distance;
                }
            }
        }

        return sampleEdges(min0, min1, buffer, sample);
    }

    @Deprecated
    private TerrainSample sampleEdges(float min0, float min1, CellLocal[] buffer, TerrainSample sample) {
        float borderDistance = (min0 + min1) * 0.5F;
        float baseBlend = borderDistance * baseFalloff;
        float continentBlend = borderDistance * continentFalloff;

        float sumBase = 0f;
        float sumContinent = 0f;

        float sumBaseWeight = 0f;
        float sumContinentWeight = 0f;

        for (var local : buffer) {
            float dist = local.context;
            float baseValue = local.cell.lowOctaveNoise;
            float continentValue = getThresholdValue(local.cell);

            float baseWeight = getWeight(dist, min0, baseBlend);
            float continentWeight = getWeight(dist, min0, continentBlend);

            sumBase += baseValue * baseWeight;
            sumContinent += continentValue * continentWeight;

            sumBaseWeight += baseWeight;
            sumContinentWeight += continentWeight;
        }

        sample.baseNoise = getBaseNoise(sumBase / sumBaseWeight);
        sample.continentNoise = getFalloff(sumContinent / sumContinentWeight);

        return sample;
    }

    private static float getWeight(float dist, float origin, float blendRange) {
        float delta = dist - origin;
        if (delta <= 0) return 1F;
        if (delta >= blendRange) return 0F;
        return 1 - (delta / blendRange);
    }

    protected static class CellLocal {
        public CellPoint cell;
        public float context;

        protected static CellLocal[] init() {
            int size = 1 + RADIUS * 2;
            var cells = new CellLocal[size * size];
            for (int i = 0; i < cells.length; i++) {
                cells[i] = new CellLocal();
            }
            return cells;
        }
    }
}
