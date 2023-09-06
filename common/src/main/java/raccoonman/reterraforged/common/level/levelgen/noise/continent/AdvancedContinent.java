package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;

public record AdvancedContinent(Noise inland, Noise shallowOcean, Noise cliff, Noise bay, Noise variance, Noise jitter, Noise skipThreshold) implements Noise {
	public static final Codec<AdvancedContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("inland").forGetter(AdvancedContinent::inland),
		Noise.HOLDER_HELPER_CODEC.fieldOf("shallow_ocean").forGetter(AdvancedContinent::shallowOcean),
		Noise.HOLDER_HELPER_CODEC.fieldOf("cliff").forGetter(AdvancedContinent::cliff),
		Noise.HOLDER_HELPER_CODEC.fieldOf("bay").forGetter(AdvancedContinent::bay),
		Noise.HOLDER_HELPER_CODEC.fieldOf("variance").forGetter(AdvancedContinent::variance),
		Noise.HOLDER_HELPER_CODEC.fieldOf("jitter").forGetter(AdvancedContinent::jitter),
		Noise.HOLDER_HELPER_CODEC.fieldOf("skip_threshold").forGetter(AdvancedContinent::skipThreshold)
	).apply(instance, AdvancedContinent::new));
			
	@Override
	public Codec<AdvancedContinent> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        float cellPointX = x;
        float cellPointY = y;
        float nearest = Float.MAX_VALUE;
        float jitter = this.jitter.compute(x, y, seed);
        for (int cy = yi - 1; cy <= yi + 1; ++cy) {
            for (int cx = xi - 1; cx <= xi + 1; ++cx) {
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float px = cx + vec.x() * jitter;
                float py = cy + vec.y() * jitter;
                float dist2 = Line.dist2(x, y, px, py);
                if (dist2 < nearest) {
                    cellPointX = px;
                    cellPointY = py;
                    cellX = cx;
                    cellY = cy;
                    nearest = dist2;
                }
            }
        }
        nearest = Float.MAX_VALUE;
        for (int cy2 = cellY - 1; cy2 <= cellY + 1; ++cy2) {
            for (int cx2 = cellX - 1; cx2 <= cellX + 1; ++cx2) {
                if (cx2 != cellX || cy2 != cellY) {
                    final Vec2f vec2 = NoiseUtil.cell(seed, cx2, cy2);
                    float px2 = cx2 + vec2.x() * jitter;
                    float py2 = cy2 + vec2.y() * jitter;
                    float dist3 = getDistance(x, y, cellPointX, cellPointY, px2, py2);
                    if (dist3 < nearest) {
                        nearest = dist3;
                    }
                }
            }
        }
        if (shouldSkip(cellX, cellY, this.skipThreshold.compute(x, y, seed), seed)) {
            return 0.0F;
        }
        return this.getDistanceValue(x, y, cellX, cellY, nearest, this.variance.compute(x, y, seed), this.inland.compute(x, y, seed), this.shallowOcean.compute(x, y, seed), seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new AdvancedContinent(this.inland.mapAll(visitor), this.shallowOcean.mapAll(visitor), this.cliff.mapAll(visitor), this.bay.mapAll(visitor), this.variance.mapAll(visitor), this.jitter.mapAll(visitor), this.skipThreshold.mapAll(visitor)));
	}
  
	private float getDistanceValue(float x, float y, int cellX, int cellY, float distance, float variance, float inland, float shallowOcean, int seed) {
		distance = getVariedDistanceValue(cellX, cellY, distance, variance, seed);
		distance = NoiseUtil.sqrt(distance);
		distance = NoiseUtil.map(distance, 0.05F, 0.25F, 0.2F);
		distance = this.getCoastalDistanceValue(x, y, distance, inland, shallowOcean, seed);
		if (distance < inland && distance >= shallowOcean) {
			distance = this.getCoastalDistanceValue(x, y, distance, inland, shallowOcean, seed);
		}
		return distance;
	}
	    
	private float getCoastalDistanceValue(float x, float y, float distance, float inland, float shallowOcean, int seed) {
		if (distance > shallowOcean && distance < inland) {
			float alpha = distance / inland;
			float cliff = this.cliff.compute(x, y, seed);
			distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
			if (distance < shallowOcean) {
				distance = shallowOcean * this.bay.compute(x, y, seed);
			}
		}
		return distance;
	}
	
	private static boolean isDefaultContinent(int cellX, int cellY) {
        return cellX == 0 && cellY == 0;
    }
	
    private static boolean shouldSkip(int cellX, int cellY, float skipThreshold, int seed) {
        if (skipThreshold > 0.0F && !isDefaultContinent(cellX, cellY)) {
            float skipValue = getCellValue(seed + 109283409, cellX, cellY);
            return skipValue < skipThreshold;
        }
        return false;
    }
    
	private static float getVariedDistanceValue(int cellX, int cellY, float distance, float variance, int seed) {
		if (variance > 0.0F && !isDefaultContinent(cellX, cellY)) {
			float sizeValue = getCellValue(seed + 216348597, cellX, cellY);
			float sizeModifier = NoiseUtil.map(sizeValue, 0.0F, variance, variance);
			distance *= sizeModifier;
		}
		return distance;
	}
    
    private static float getCellValue(int seed, int cellX, int cellY) {
        return 0.5F + NoiseUtil.valCoord2D(seed, cellX, cellY) * 0.5F;
    }
    
    private static float midPoint(final float a, final float b) {
        return (a + b) * 0.5F;
    }
    
    private static float getDistance(final float x, final float y, final float ax, final float ay, final float bx, final float by) {
        final float mx = midPoint(ax, bx);
        final float my = midPoint(ay, by);
        final float dx = bx - ax;
        final float dy = by - ay;
        final float nx = -dy;
        final float ny = dx;
        return getDistance2Line(x, y, mx, my, mx + nx, my + ny);
    }
    
    private static float getDistance2Line(final float x, final float y, final float ax, final float ay, final float bx, final float by) {
        final float dx = bx - ax;
        final float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        final float ox = ax + dx * v;
        final float oy = ay + dy * v;
        return Line.dist2(x, y, ox, oy);
    }
}
