package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;

public record MultiImprovedContinent(Domain warp, float frequency, float variance, float jitter, float skipThreshold, float shallowOceanPoint, float inlandPoint, Noise cliff, Noise bay, int baseSeed, int varianceSeed, int skippingSeed) implements Noise {
	public static final Codec<MultiImprovedContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("warp").forGetter(MultiImprovedContinent::warp),
		Codec.FLOAT.fieldOf("frequency").forGetter(MultiImprovedContinent::frequency),
		Codec.FLOAT.fieldOf("variance").forGetter(MultiImprovedContinent::variance),
		Codec.FLOAT.fieldOf("jitter").forGetter(MultiImprovedContinent::jitter),
		Codec.FLOAT.fieldOf("skip_threshold").forGetter(MultiImprovedContinent::skipThreshold),
		Codec.FLOAT.fieldOf("shallow_ocean_point").forGetter(MultiImprovedContinent::shallowOceanPoint),
		Codec.FLOAT.fieldOf("inland_point").forGetter(MultiImprovedContinent::inlandPoint),
		Noise.HOLDER_HELPER_CODEC.fieldOf("cliff").forGetter(MultiImprovedContinent::cliff),
		Noise.HOLDER_HELPER_CODEC.fieldOf("bay").forGetter(MultiImprovedContinent::bay),
		Codec.INT.fieldOf("base_seed").forGetter(MultiImprovedContinent::baseSeed),
		Codec.INT.fieldOf("variance_seed").forGetter(MultiImprovedContinent::varianceSeed),
		Codec.INT.fieldOf("skipping_seed").forGetter(MultiImprovedContinent::skippingSeed)
	).apply(instance, MultiImprovedContinent::new));
	
	@Override
	public Codec<MultiImprovedContinent> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        float wx = this.warp.getX(x, y, seed);
        float wy = this.warp.getY(x, y, seed);
        x = wx * this.frequency;
        y = wy * this.frequency;
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        float cellPointX = x;
        float cellPointY = y;
        float nearest = Float.MAX_VALUE;
        for (int cy = yi - 1; cy <= yi + 1; ++cy) {
            for (int cx = xi - 1; cx <= xi + 1; ++cx) {
                Vec2f vec = NoiseUtil.cell(this.baseSeed + seed, cx, cy);
                float px = cx + vec.x() * this.jitter;
                float py = cy + vec.y() * this.jitter;
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
                    Vec2f vec2 = NoiseUtil.cell(this.baseSeed + seed, cx2, cy2);
                    float px2 = cx2 + vec2.x() * this.jitter;
                    float py2 = cy2 + vec2.y() * this.jitter;
                    float dist3 = getDistance(x, y, cellPointX, cellPointY, px2, py2);
                    if (dist3 < nearest) {
                        nearest = dist3;
                    }
                }
            }
        }
        if (this.shouldSkip(cellX, cellY, seed)) {
            return 0.0F;
        }
       	return this.getDistanceValue(x, y, cellX, cellY, nearest, seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new MultiImprovedContinent(this.warp, this.frequency, this.variance, this.jitter, this.skipThreshold, this.shallowOceanPoint, this.inlandPoint, this.cliff.mapAll(visitor), this.bay.mapAll(visitor), this.baseSeed, this.varianceSeed, this.skippingSeed));
	}
    
    private float getDistanceValue(float x, float y, int cellX, int cellY, float distance, int seed) {
        distance = this.getVariedDistanceValue(cellX, cellY, distance, seed);
        distance = NoiseUtil.sqrt(distance);
        distance = NoiseUtil.map(distance, 0.05F, 0.25F, 0.2F);
        distance = this.getCoastalDistanceValue(x, y, distance, seed);
        if (distance < this.inlandPoint && distance >= this.shallowOceanPoint) {
            distance = this.getCoastalDistanceValue(x, y, distance, seed);
        }
        return distance;
    }
    
    private float getVariedDistanceValue(int cellX, int cellY, float distance, int seed) {
        if (this.variance > 0.0F && !isDefaultContinent(cellX, cellY)) {
            float sizeValue = sampleCell(cellX, cellY, seed + this.varianceSeed);
            float sizeModifier = NoiseUtil.map(sizeValue, 0.0F, this.variance, this.variance);
            distance *= sizeModifier;
        }
        return distance;
    }
    
    private float getCoastalDistanceValue(float x, float y, float distance, int seed) {
        if (distance > this.shallowOceanPoint && distance < this.inlandPoint) {
            float alpha = distance / this.inlandPoint;
            float cliff = this.cliff.compute(x, y, seed);
            distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
            if (distance < this.shallowOceanPoint) {
                distance = this.shallowOceanPoint * this.bay.compute(x, y, seed);
            }
        }
        return distance;
    }
    
    private boolean shouldSkip(int cellX, int cellY, int seed) {
        if (this.skipThreshold > 0.0F && !isDefaultContinent(cellX, cellY)) {
            float skipValue = sampleCell(cellX, cellY, seed + this.skippingSeed);
            return skipValue < this.skipThreshold;
        }
        return false;
    }

    private static float midPoint(float a, float b) {
        return (a + b) * 0.5F;
    }
    
    private static float getDistance(float x, float y, float ax, float ay, float bx, float by) {
        float mx = midPoint(ax, bx);
        float my = midPoint(ay, by);
        float dx = bx - ax;
        float dy = by - ay;
        float nx = -dy;
        float ny = dx;
        return getDistance2Line(x, y, mx, my, mx + nx, my + ny);
    }
    
    private static float getDistance2Line(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        float ox = ax + dx * v;
        float oy = ay + dy * v;
        return Line.dist2(x, y, ox, oy);
    }
	
	private static boolean isDefaultContinent(int cellX, int cellY) {
        return cellX == 0 && cellY == 0;
    }
    
	private static float sampleCell(int cellX, int cellY, int seed) {
        return 0.5F + NoiseUtil.valCoord2D(seed, cellX, cellY) * 0.5F;
    }
}