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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.cell.CellShape;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.MathUtil;
import raccoonman.reterraforged.common.util.pos.PosUtil;
import raccoonman.reterraforged.common.util.storage.LongCache;
import raccoonman.reterraforged.common.util.storage.LossyCache;

public class Continent implements Noise {
	public static final Codec<Continent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("falloff").forGetter((c) -> c.falloff),
		Codec.FLOAT.fieldOf("jitter").forGetter((c) -> c.jitter),
		CellShape.CODEC.fieldOf("cell_shape").forGetter((c) -> c.cellShape),
		Noise.DIRECT_CODEC.fieldOf("cell_source").forGetter((c) -> c.cellSource)
	).apply(instance, Continent::new));
	
    private static final int RADIUS = 2;

    private final float falloff;
    private final float jitter;
    private final CellShape cellShape;
    private final Noise cellSource;
    private final LongCache<Float> cellCache = LossyCache.concurrent(2048, Float[]::new);

    private final ThreadLocal<CellLocal[]> cellBuffer = ThreadLocal.withInitial(CellLocal::makeBuffer);

    public Continent(float falloff, float jitter, CellShape cellShape, Noise cellSource) {
        this.falloff = falloff;
        this.jitter = jitter;
        this.cellShape = cellShape;
        this.cellSource = cellSource;
    }
    
	@Override
	public Codec<Continent> codec() {
		return CODEC;
	}

    @Override
    public float getValue(float x, float y, int seed) {
        long centre = this.getNearestCell(x, y, seed);
        int centreX = PosUtil.unpackLeft(centre);
        int centreY = PosUtil.unpackRight(centre);

        // Note: Must adjust inputs AFTER getting nearest cell
        x = this.cellShape.adjustX(x);
        y = this.cellShape.adjustY(y);

        int minX = centreX - RADIUS;
        int minY = centreY - RADIUS;
        int maxX = centreX + RADIUS;
        int maxY = centreY + RADIUS;

        float min0 = Float.MAX_VALUE;
        float min1 = Float.MAX_VALUE;
        var buffer = this.cellBuffer.get();

        for (int cy = minY, i = 0; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++, i++) {
            	int hash = MathUtil.hash(seed, cx, cy);
                float px = this.cellShape.getCellX(hash, cx, cy, this.jitter);
                float py = this.cellShape.getCellY(hash, cx, cy, this.jitter);
                var cell = this.getCached(cx, cy, px, py, seed);
                var local = buffer[i];

                float distance = NoiseUtil.sqrt(NoiseUtil.dist2(x, y, px, py));

                local.noise = cell;
                local.distance = distance;

                if (distance < min0) {
                    min1 = min0;
                    min0 = distance;
                } else if (distance < min1) {
                    min1 = distance;
                }
            }
        }

        return sampleEdges(min0, min1, this.falloff, buffer);
    }

    private static float sampleEdges(float min0, float min1, float falloff, CellLocal[] buffer) {
        float borderDistance = (min0 + min1) * 0.5F;
        float blend = borderDistance * falloff;

        float sum = 0.0F;
        float sumWeight = 0.0F;

        for (var local : buffer) {
            float dist = local.distance;
            float value = local.noise;
            float weight = getWeight(dist, min0, blend);

            sum += value * weight;
            sumWeight += weight;
        }
        return sum / sumWeight;
    }

    private long getNearestCell(float x, float y, int seed) {
        x = this.cellShape.adjustX(x);
        y = this.cellShape.adjustY(y);

        int minX = NoiseUtil.floor(x) - 1;
        int minY = NoiseUtil.floor(y) - 1;
        int maxX = minX + 2;
        int maxY = minY + 2;

        int nearestX = 0;
        int nearestY = 0;
        float distance = Float.MAX_VALUE;

        for (int cy = minY; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++) {
            	int hash = MathUtil.hash(seed, cx, cy);
                float px = this.cellShape.getCellX(hash, cx, cy, this.jitter);
                float py = this.cellShape.getCellY(hash, cx, cy, this.jitter);
                float distance2 = NoiseUtil.dist2(x, y, px, py);

                if (distance2 < distance) {
                    distance = distance2;
                    nearestX = cx;
                    nearestY = cy;
                }
            }
        }

        return PosUtil.pack(nearestX, nearestY);
    }
    
    private float getCached(int cx, int cy, float px, float py, int seed) {
    	return this.cellCache.computeIfAbsent(PosUtil.pack(cx, cy), (k) -> {
        	return this.cellSource.getValue(px, py, seed);
        });
    }

    private static float getWeight(float dist, float origin, float blendRange) {
        float delta = dist - origin;
        if (delta <= 0.0F) {
        	return 1.0F;
        }
        if (delta >= blendRange) {
        	return 0.0F;
        }
        return 1 - (delta / blendRange);
    }

    private static class CellLocal {
        public float noise;
        public float distance;

        public static CellLocal[] makeBuffer() {
            int diameter = RADIUS * 2 + 1;
            var buffer = new CellLocal[diameter * diameter];
            for (int i = 0; i < buffer.length; i++) {
                buffer[i] = new CellLocal();
            }
            return buffer;
        }
    }
}
