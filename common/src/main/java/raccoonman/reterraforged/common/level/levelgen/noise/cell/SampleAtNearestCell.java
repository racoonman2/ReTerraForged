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

package raccoonman.reterraforged.common.level.levelgen.noise.cell;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.storage.LongCache;
import raccoonman.reterraforged.common.util.storage.LossyCache;

public record SampleAtNearestCell(Noise source, float falloff, float jitter, int radius, CellShape shape, ThreadLocal<long[]> cellBuffer, LongCache<Float> cellCache) implements Noise {
	public static final Codec<SampleAtNearestCell> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(SampleAtNearestCell::source),
		Codec.FLOAT.fieldOf("falloff").forGetter(SampleAtNearestCell::falloff),
		Codec.FLOAT.fieldOf("jitter").forGetter(SampleAtNearestCell::jitter),
		Codec.INT.fieldOf("radius").forGetter(SampleAtNearestCell::radius),
		CellShape.CODEC.fieldOf("shape").forGetter(SampleAtNearestCell::shape)
	).apply(instance, SampleAtNearestCell::new));

    public SampleAtNearestCell(Noise source, float falloff, float jitter, int radius, CellShape cellShape) {
    	this(source, falloff, jitter, radius, cellShape, ThreadLocal.withInitial(() -> makeCellBuffer(radius)), LossyCache.concurrent(2048, Float[]::new));
    }

    @Override
    public float maxValue() {
    	return this.source.maxValue();
    }
    
    @Override
    public float minValue() {
    	return this.source.minValue();
    }
    
	@Override
	public Codec<SampleAtNearestCell> codec() {
		return CODEC;
	}

    @Override
    public float getValue(float x, float y, int seed) {
        x = this.shape.adjustX(x);
        y = this.shape.adjustY(y);
        
        long centre = this.getNearestCell(x, y, seed);
        int centreX = unpackLeft(centre);
        int centreY = unpackRight(centre);

        int minX = centreX - this.radius;
        int minY = centreY - this.radius;
        int maxX = centreX + this.radius;
        int maxY = centreY + this.radius;

        float min0 = Float.MAX_VALUE;
        float min1 = Float.MAX_VALUE;
        var buffer = this.cellBuffer.get();

        for (int cy = minY, i = 0; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++, i++) {
                float px = this.shape.getCellX(cx, cy, seed, this.jitter);
                float py = this.shape.getCellY(cx, cy, seed, this.jitter);
                var cell = this.cellCache.computeIfAbsent(packf(px, py), (k) -> {
                	return this.source.getValue(px, py, seed);
                });
                float distance = NoiseUtil.sqrt(NoiseUtil.dist2(x, y, px, py));
                buffer[i] = packf(cell, distance);

                if (distance < min0) {
                    min1 = min0;
                    min0 = distance;
                } else if (distance < min1) {
                    min1 = distance;
                }
            }
        }

        return this.sampleEdges(min0, min1, buffer);
    }

    private long getNearestCell(float x, float y, int seed) {
        int minX = NoiseUtil.floor(x) - 1;
        int minY = NoiseUtil.floor(y) - 1;
        int maxX = minX + 2;
        int maxY = minY + 2;

        int nearestX = 0;
        int nearestY = 0;
        float distance = Float.MAX_VALUE;

        for (int cy = minY; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++) {
                float px = this.shape.getCellX(cx, cy, seed, this.jitter);
                float py = this.shape.getCellY(cx, cy, seed, this.jitter);
                float distance2 = NoiseUtil.dist2(x, y, px, py);

                if (distance2 < distance) {
                    distance = distance2;
                    nearestX = cx;
                    nearestY = cy;
                }
            }
        }

        return pack(nearestX, nearestY);
    }

    private float sampleEdges(float min0, float min1, long[] buffer) {
        float borderDistance = (min0 + min1) * 0.5F;
        float blend = borderDistance * this.falloff;

        float sum = 0.0F;
        float sumWeight = 0.0F;

        for (var local : buffer) {
            float value = unpackLeftf(local);
            float dist = unpackRightf(local);
            float weight = blend(dist, min0, blend);

            sum += value * weight;
            sumWeight += weight;
        }
        return sum / sumWeight;
    }
    
    private static float blend(float dist, float origin, float blendRange) {
        float delta = dist - origin;
        if (delta <= 0.0F) {
        	return 1.0F;
        }
        if (delta >= blendRange) {
        	return 0.0F;
        }
        return 1.0F - (delta / blendRange);
    }

    private static long[] makeCellBuffer(int radius) {
        int diameter = radius * 2 + 1;
        return new long[diameter * diameter];
    }
    
    private static final long MASK = 0xFFFFFFFFL;
    private static long pack(int left, int right) {
        return (long)right & MASK | ((long)left & MASK) << 32;
    }

    private static int unpackLeft(long packed) {
        return (int)(packed >>> 32 & MASK);
    }

    private static int unpackRight(long packed) {
        return (int)(packed & MASK);
    }
    
    private static long packf(float left, float right) {
        return (long)Float.floatToRawIntBits(right) & MASK | ((long)Float.floatToRawIntBits(left) & MASK) << 32;
    }

    private static float unpackLeftf(long packed) {
        return Float.intBitsToFloat((int)(packed >>> 32 & MASK));
    }

    private static float unpackRightf(long packed) {
        return Float.intBitsToFloat((int)(packed & MASK));
    }
}
