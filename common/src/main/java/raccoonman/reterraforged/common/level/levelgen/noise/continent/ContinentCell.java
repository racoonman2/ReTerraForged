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

package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import raccoonman.reterraforged.common.level.levelgen.cell.CellPoint;
import raccoonman.reterraforged.common.level.levelgen.cell.CellShape;
import raccoonman.reterraforged.common.level.levelgen.cell.CellSource;
import raccoonman.reterraforged.common.util.MathUtil;
import raccoonman.reterraforged.common.util.pos.PosUtil;
import raccoonman.reterraforged.common.util.storage.LongCache;
import raccoonman.reterraforged.common.util.storage.LossyCache;
import raccoonman.reterraforged.common.util.storage.ObjectPool;

//TODO clean this up
public class ContinentCell {
	@Deprecated
    private static final int CONTINENT_SAMPLE_SCALE = 400;
	@Deprecated
	private static final int SAMPLE_SEED_OFFSET = 6569;
	private static final int CELL_POINT_CACHE_SIZE = 2048;

	private final float jitter;

	//TODO should be private
    public final CellShape cellShape;
    private final CellSource cellSource;

    private final ObjectPool<CellPoint> cellPool = ObjectPool.forCacheSize(CELL_POINT_CACHE_SIZE, CellPoint::new);
    private final LongCache<CellPoint> cellCache = LossyCache.concurrent(CELL_POINT_CACHE_SIZE, CellPoint[]::new, this.cellPool::restore);

    public ContinentCell(float jitter, CellShape cellShape, CellSource cellSource) {
        this.jitter = jitter;
        this.cellShape = cellShape;
        this.cellSource = cellSource;
    }
    
    public CellPoint getCell(int cx, int cy, int seed) {
        long index = PosUtil.pack(cx, cy);
        return this.cellCache.computeIfAbsent(index, (k) -> this.computeCell(seed, k));
    }

    public CellPoint computeCell(int seed, long index) {
        return computeCell(seed, index, 0, 0, cellPool.take());
    }

    public CellPoint computeCell(int seed, long index, int xOffset, int yOffset, CellPoint cell) {
        int cx = PosUtil.unpackLeft(index) + xOffset;
        int cy = PosUtil.unpackRight(index) + yOffset;

        int hash = MathUtil.hash(seed, cx, cy);
        float px = cellShape.getCellX(hash, cx, cy, this.jitter);
        float py = cellShape.getCellY(hash, cx, cy, this.jitter);

        cell.px = px;
        cell.py = py;

        float target = 4000F;
        float freq = (CONTINENT_SAMPLE_SCALE / target);

        sampleCell(seed + SAMPLE_SEED_OFFSET, px, py, this.cellSource, 2, freq, 2.75f, 0.3f, cell);

        return cell;
    }

    private static void sampleCell(int seed, float x, float y, CellSource cellSource, int octaves, float frequency, float lacunarity, float gain, CellPoint cell) {
        x *= frequency;
        y *= frequency;

        float sum = cellSource.getValue(x, y, seed);
        float amp = 1.0F;
        float sumAmp = amp;

        cell.lowOctaveNoise = sum;

        for(int i = 1; i < octaves; ++i) {
            amp *= gain;
            x *= lacunarity;
            y *= lacunarity;

            sum += cellSource.getValue(x, y, seed) * amp;
            sumAmp += amp;
        }

        cell.noise = sum / sumAmp;
    }
}
