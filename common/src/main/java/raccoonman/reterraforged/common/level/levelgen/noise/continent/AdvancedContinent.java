package raccoonman.reterraforged.common.level.levelgen.noise.continent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.util.Vec2f;

public record AdvancedContinent(float variance, float jitter, float inland, float shallowOcean, Noise cliffNoise, Noise bayNoise) implements Noise {
	public static final Codec<AdvancedContinent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("variance").forGetter(AdvancedContinent::variance),
		Codec.FLOAT.fieldOf("jitter").forGetter(AdvancedContinent::jitter),
		Codec.FLOAT.fieldOf("inland").forGetter(AdvancedContinent::inland),
		Codec.FLOAT.fieldOf("shallow_ocean").forGetter(AdvancedContinent::shallowOcean),
		Noise.HOLDER_HELPER_CODEC.fieldOf("cliff_noise").forGetter(AdvancedContinent::cliffNoise),
		Noise.HOLDER_HELPER_CODEC.fieldOf("bay_noise").forGetter(AdvancedContinent::bayNoise)
	).apply(instance, AdvancedContinent::new));
	
	@Override
	public Codec<AdvancedContinent> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        final int xi = NoiseUtil.floor(x);
        final int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        float cellPointX = x;
        float cellPointY = y;
        float nearest = Float.MAX_VALUE;
        for (int cy = yi - 1; cy <= yi + 1; ++cy) {
            for (int cx = xi - 1; cx <= xi + 1; ++cx) {
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                final float px = cx + vec.x() * this.jitter;
                final float py = cy + vec.y() * this.jitter;
                final float dist2 = Line.dist2(x, y, px, py);
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
                    final float px2 = cx2 + vec2.x() * this.jitter;
                    final float py2 = cy2 + vec2.y() * this.jitter;
                    final float dist3 = getDistance(x, y, cellPointX, cellPointY, px2, py2);
                    if (dist3 < nearest) {
                        nearest = dist3;
                    }
                }
            }
        }
        return this.getDistanceValue(x, y, cellX, cellY, nearest, seed);
	}
	
	protected float getDistanceValue(final float x, final float y, final int cellX, final int cellY, float distance, final int seed) {
		distance = this.getVariedDistanceValue(cellX, cellY, distance);
		distance = NoiseUtil.sqrt(distance);
		distance = NoiseUtil.map(distance, 0.05f, 0.25f, 0.2f);
		distance = this.getCoastalDistanceValue(x, y, seed, distance);
		if (distance < this.inland && distance >= this.shallowOcean) {
			distance = this.getCoastalDistanceValue(x, y, seed, distance);
		}
		return distance;
	}
	    
	protected float getVariedDistanceValue(final int cellX, final int cellY, float distance) {
		if (this.variance > 0.0F && !this.isDefaultContinent(cellX, cellY)) {
			final float sizeValue = getCellValue(123456, cellX, cellY);
			final float sizeModifier = NoiseUtil.map(sizeValue, 0.0f, this.variance, this.variance);
			distance *= sizeModifier;
		}
		return distance;
	}
	
	protected boolean isDefaultContinent(final int cellX, final int cellY) {
		return cellX == 0 && cellY == 0;
	}
	    
	protected float getCoastalDistanceValue(final float x, final float y, int seed, float distance) {
		if (distance > this.shallowOcean && distance < this.inland) {
			final float alpha = distance / this.inland;
			final float cliff = this.cliffNoise.compute(x, y, seed);
			distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
			if (distance < this.shallowOcean) {
				distance = this.shallowOcean * this.bayNoise.compute(x, y, seed);
			}
		}
		return distance;
	}
	
	private static float getCellValue(final int seed, final int cellX, final int cellY) {
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
