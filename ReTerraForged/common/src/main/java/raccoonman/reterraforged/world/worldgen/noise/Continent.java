package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;

public record Continent(@Deprecated(forRemoval = true) float frequency, float jitter, float sizeVariance, float skipping, Noise cliffNoise, Noise bayNoise, float oceanThreshold, float coastThreshold) implements Noise {
	public static final MapCodec<Continent> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Continent::frequency),
		Codec.FLOAT.fieldOf("jitter").forGetter(Continent::jitter),
		Codec.FLOAT.fieldOf("size_variance").forGetter(Continent::sizeVariance),
		Codec.FLOAT.fieldOf("skipping").forGetter(Continent::skipping),
		Noise.HOLDER_HELPER_CODEC.fieldOf("cliff_noise").forGetter(Continent::cliffNoise),
		Noise.HOLDER_HELPER_CODEC.fieldOf("bay_noise").forGetter(Continent::bayNoise),
		Codec.FLOAT.fieldOf("ocean_threshold").forGetter(Continent::oceanThreshold),
		Codec.FLOAT.fieldOf("coast_threshold").forGetter(Continent::coastThreshold)
    ).apply(instance, Continent::new));
	
	@Override
	public float compute(float x, float y, int seed) {
		x = x * this.frequency;
		y = y * this.frequency;
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        float cellPointX = x;
        float cellPointY = y;
        float nearest = Float.MAX_VALUE;
        for (int cy = yi - 1; cy <= yi + 1; cy++) {
            for (int cx = xi - 1; cx <= xi + 1; cx++) {
                Vec2f vec = NoiseUtil.cell(cx, cy, seed);
                float px = cx + vec.x() * this.jitter;
                float py = cy + vec.y() * this.jitter;
                float dist2 = Line.distSq(x, y, px, py);
                if (dist2 < nearest) {
                    cellPointX = px;
                    cellPointY = py;
                    cellX = cx;
                    cellY = cy;
                    nearest = dist2;
                }
            }
        }
        
        if (this.shouldSkip(cellX, cellY, seed)) {
            return 0.0F;
        }
        
        nearest = Float.MAX_VALUE;
        for (int cy2 = cellY - 1; cy2 <= cellY + 1; cy2++) {
            for (int cx2 = cellX - 1; cx2 <= cellX + 1; cx2++) {
                if (cx2 != cellX || cy2 != cellY) {
                    Vec2f vec2 = NoiseUtil.cell(cx2, cy2, seed);
                    float px2 = cx2 + vec2.x() * this.jitter;
                    float py2 = cy2 + vec2.y() * this.jitter;
                    float dist3 = getDistance(x, y, cellPointX, cellPointY, px2, py2);
                    if (dist3 < nearest) {
                        nearest = dist3;
                    }
                }
            }
        }
        return this.getCenterDistance(x, y, cellX, cellY, nearest, seed);
	}

	@Override
	public float minValue() {
		return 0.0F;
	}

	@Override
	public float maxValue() {
		return 1.0F;
	}

	@Override
	public Noise mapAll(Noise.Visitor visitor) {
		return visitor.apply(new Continent(this.frequency, this.jitter, this.sizeVariance, this.skipping, this.cliffNoise, this.bayNoise, this.oceanThreshold, this.coastThreshold));
	}

	@Override
	public MapCodec<Continent> codec() {
		return CODEC;
	}

    private float getCenterDistance(float x, float y, int cellX, int cellY, float distance, int seed) {
        distance = this.getVariedDistance(cellX, cellY, distance, seed);
        distance = NoiseUtil.sqrt(distance);
        distance = NoiseUtil.map(distance, 0.05F, 0.25F, 0.2F);
        distance = this.getCoastalDistanceValue(x, y, distance, seed);
        if (distance < this.coastThreshold && distance >= this.oceanThreshold) {
            distance = this.getCoastalDistanceValue(x, y, distance, seed);
        }
        return distance;
    }
    
    private float getVariedDistance(int cellX, int cellY, float distance, int seed) {
        if (this.sizeVariance > 0.0F) {
            float sizeValue = getCellValue(seed, cellX, cellY);
            float sizeModifier = NoiseUtil.map(sizeValue, 0.0F, this.sizeVariance, this.sizeVariance);
            distance *= sizeModifier;
        }
        return distance;
    }
    
    private float getCoastalDistanceValue(float x, float y, float distance, int seed) {
        if (distance > this.oceanThreshold && distance < this.coastThreshold) {
            final float alpha = distance / this.coastThreshold;
            final float cliff = this.cliffNoise.compute(x, y, 0);
            distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
            if (distance < this.oceanThreshold) {
                distance = this.oceanThreshold * this.bayNoise.compute(x, y, seed);
            }
        }
        return distance;
    }
    
    private boolean shouldSkip(int cellX, int cellY, int seed) {
    	float skipValue = getCellValue(seed, cellX, cellY);
    	return skipValue < this.skipping;
    }
    
    private static float getCellValue(int cellX, int cellY, int seed) {
        return 0.5F + NoiseUtil.valCoord2D(seed, cellX, cellY) * 0.5F;
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
        return getDistanceSqLine(x, y, mx, my, mx + nx, my + ny);
    }
    
    private static float getDistanceSqLine(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        float ox = ax + dx * v;
        float oy = ay + dy * v;
        return Line.distSq(x, y, ox, oy);
    }
}
