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

import raccoonman.reterraforged.common.level.levelgen.cell.CellPoint;
import raccoonman.reterraforged.common.level.levelgen.cell.CellShape;
import raccoonman.reterraforged.common.level.levelgen.cell.CellSource;
import raccoonman.reterraforged.common.level.levelgen.continent.ContinentPoints;
import raccoonman.reterraforged.common.level.levelgen.continent.shape.FalloffPoint;
import raccoonman.reterraforged.common.level.levelgen.settings.ControlPoints;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.noise.util.Vec2f;
import raccoonman.reterraforged.common.util.SpiralIterator;
import raccoonman.reterraforged.common.util.pos.PosUtil;

//TODO clean this up
public class Continent implements Noise {
	public static final Codec<Continent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("falloff").forGetter((c) -> c.falloff),
		Codec.FLOAT.fieldOf("threshold").forGetter((c) -> c.threshold),
		Codec.FLOAT.fieldOf("jitter").forGetter((c) -> c.jitter),
		CellShape.CODEC.fieldOf("cell_shape").forGetter((c) -> c.cellShape),
		CellSource.CODEC.fieldOf("cell_source").forGetter((c) -> c.cellSource),
		WorldSettings.ControlPoints.CODEC.fieldOf("control_points").forGetter((c) -> c.controlPoints)
	).apply(instance, Continent::new));
	
	private static final int VALID_SPAWN_RADIUS = 3;
    private static final int SPAWN_SEARCH_RADIUS = 100_000;
    private static final int RADIUS = 2;

    private final float falloff;
    //TODO shouldn't be public
    public final float threshold;
    private final float jitter;
    private final CellShape cellShape;
    private final CellSource cellSource;

    private final ContinentCell cellNoise;
    private final WorldSettings.ControlPoints controlPoints;
    private final FalloffPoint[] falloffPoints;
    private final ThreadLocal<CellLocal[]> cellBuffer = ThreadLocal.withInitial(CellLocal::init);
    @Deprecated //ew
    private volatile Vec2f offset = null;

    public Continent(float falloff, float threshold, float jitter, CellShape cellShape, CellSource cellSource, WorldSettings.ControlPoints controlPoints) {
        this.falloff = falloff;
        this.threshold = threshold;
        this.jitter = jitter;
        this.cellShape = cellShape;
        this.cellSource = cellSource;
        this.controlPoints = controlPoints;
        this.cellNoise = new ContinentCell(jitter, cellShape, cellSource);
        this.falloffPoints = ContinentPoints.getFalloff(new ControlPoints(controlPoints));
    }
    
    private Vec2f getWorldOffset(int seed) {
        if (this.offset == null) {
        	this.offset = computeWorldOffset(seed);
        }
        return this.offset;
    }

    public float getThresholdValue(CellPoint cell) {
        return cell.noise < this.threshold ? 0F : 1F;
    }

    public float getFalloff(float continentNoise) {
        return ContinentPoints.getFalloff(continentNoise, this.falloffPoints);
    }

    @Override
    public float getValue(float x, float y, int seed) {
    	var offset = this.getWorldOffset(seed);
    	x += offset.x;
    	y += offset.y;
    	
        var centre = this.getNearestCell(x, y, seed);
        int centreX = PosUtil.unpackLeft(centre);
        int centreY = PosUtil.unpackRight(centre);

        // Note: Must adjust inputs AFTER getting nearest cell
        x = cellNoise.cellShape.adjustX(x);
        y = cellNoise.cellShape.adjustY(y);

        int minX = centreX - RADIUS;
        int minY = centreY - RADIUS;
        int maxX = centreX + RADIUS;
        int maxY = centreY + RADIUS;

        float min0 = Float.MAX_VALUE;
        float min1 = Float.MAX_VALUE;
        var buffer = cellBuffer.get();

        for (int cy = minY, i = 0; cy <= maxY; cy++) {
            for (int cx = minX; cx <= maxX; cx++, i++) {
                var cell = cellNoise.getCell(cx, cy, seed);
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

        return sampleEdges(min0, min1, buffer);
    }

    public long getNearestCell(float x, float y, int seed) {
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
                var cell = this.cellNoise.getCell(cx, cy, seed);
                float dist2 = NoiseUtil.dist2(x, y, cell.px, cell.py);

                if (dist2 < distance) {
                    distance = dist2;
                    nearestX = cx;
                    nearestY = cy;
                }
            }
        }

        return PosUtil.pack(nearestX, nearestY);
    }
    
	@Override
	public Codec<Continent> codec() {
		return CODEC;
	}

    @Deprecated
    private float sampleEdges(float min0, float min1, CellLocal[] buffer) {
        float borderDistance = (min0 + min1) * 0.5F;
        float blend = borderDistance * this.falloff;

        float sum = 0f;
        float sumWeight = 0f;

        for (var local : buffer) {
            float dist = local.context;
            float value = getThresholdValue(local.cell);
            float weight = getWeight(dist, min0, blend);

            sum += value * weight;
            sumWeight += weight;
        }
        return getFalloff(sum / sumWeight);
    }
    
    private Vec2f computeWorldOffset(int seed) {
        var iterator = new SpiralIterator(0, 0, 0, SPAWN_SEARCH_RADIUS);
        var cell = new CellPoint();

        while (iterator.hasNext()) {
            long pos = iterator.next();
            this.cellNoise.computeCell(seed, pos, 0, 0, cell);

            if (this.getThresholdValue(cell) == 0) {
                continue;
            }

            float px = cell.px;
            float py = cell.py;
            if (isValidSpawn(seed, pos, VALID_SPAWN_RADIUS, cell)) {
                return new Vec2f(px, py);
            }
        }

        return Vec2f.ZERO;
    }

    private boolean isValidSpawn(int seed, long pos, int radius, CellPoint cell) {
        int radius2 = radius * radius;

        for (int dy = -radius; dy <= radius; dy++) {
            for (int dx = -radius; dx <= radius; dx++) {
                int d2 = dx * dx + dy * dy;

                if (dy < 1 || d2 >= radius2) continue;

                this.cellNoise.computeCell(seed, pos, dx, dy, cell);

                if (this.getThresholdValue(cell) == 0) {
                    return false;
                }
            }
        }

        return true;
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
