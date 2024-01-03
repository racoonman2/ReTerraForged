package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public record Line(float x1, float z1, float x2, float z2, Noise radiusSq, Noise fadeIn, Noise fadeOut, float feather, float featherBias, float orthX1, float orthZ1, float orthX2, float orthZ2, float dx, float dz, float lengthSq) implements Noise {
	public static final Codec<Line> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("x1").forGetter(Line::x1),
		Codec.FLOAT.fieldOf("z1").forGetter(Line::z1),
		Codec.FLOAT.fieldOf("x2").forGetter(Line::x2),
		Codec.FLOAT.fieldOf("z2").forGetter(Line::z2),
		Noise.HOLDER_HELPER_CODEC.fieldOf("radiusSq").forGetter(Line::radiusSq),
		Noise.HOLDER_HELPER_CODEC.fieldOf("radiusSq").forGetter(Line::radiusSq),
		Noise.HOLDER_HELPER_CODEC.fieldOf("radiusSq").forGetter(Line::radiusSq),
		Codec.FLOAT.fieldOf("feather").forGetter(Line::feather)
	).apply(instance, Line::new));
	
	public Line(float x1, float z1, float x2, float z2, Noise radiusSq, Noise fadeIn, Noise fadeOut, float feather)	{
		this(x1, z1, x2, z2, radiusSq, fadeIn, fadeOut, feather, 1.0F - feather, x1 + (z2 - z1), z1 + (x1 - x2), x2 + (z2 - z1), z2 + (x1 - x2), x2 - x1, z2 - z1);
	}
	
	private Line(float x1, float z1, float x2, float z2, Noise radiusSq, Noise fadeIn, Noise fadeOut, float feather, float featherBias, float orthX1, float orthZ1, float orthX2, float orthZ2, float dx, float dz) {
		this(x1, z1, x2, z2, radiusSq, fadeIn, fadeOut, feather, featherBias, orthX1, orthZ1, orthX2, orthZ2, dx, dz, dx * dx + dz * dz);
	}
	
	@Override
	public float compute(float x, float z, int seed) {
		float widthModifier = this.getWidthModifier(x, z, seed);
		float dist2 = this.getDistanceSq(x, z);
        float radius2 = this.radiusSq.compute(x, z, seed) * widthModifier;
        if (dist2 > radius2) {
            return 0.0F;
        }
        float value = dist2 / radius2;
        if (this.feather == 0.0F) {
            return 1.0F - value;
        }
        float feather = this.featherBias + widthModifier * this.feather;
        return (1.0F - value) * feather;
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
	public Noise mapAll(Visitor visitor) {
		return new Line(this.x1, this.z1, this.x2, this.z2, this.radiusSq, this.fadeIn, this.fadeOut, this.feather);
	}

	@Override
	public Codec<Line> codec() {
		return CODEC;
	}
	
	private float getWidthModifier(float x, float z, int seed) {
        float d1 = distSq(x, z, this.x1, this.z1);
        if (d1 == 0.0F) {
            return 0.0F;
        }
        float d2 = distSq(x, z, this.x2, this.z2);
        if (d2 == 0.0F) {
            return 0.0F;
        }
        float fade = 1.0F;
        float in = this.fadeIn.compute(x, z, seed);
        float out = this.fadeOut.compute(x, z, seed);
        if (in > 0.0F) {
            float dist = in * this.lengthSq;
            if (d1 < dist) {
                fade *= d1 / dist;
            }
        }
        if (out > 0.0F) {
            float dist = out * this.lengthSq;
            if (d2 < dist) {
                fade *= d2 / dist;
            }
        }
        return fade;
	}
	
	private float getDistanceSq(float x, float y) {
        float t = (x - this.x1) * this.dx + (y - this.z1) * this.dz;
        float s = NoiseUtil.clamp(t / this.lengthSq, 0.0f, 1.0f);
        float ix = this.x1 + s * this.dx;
        float iy = this.z1 + s * this.dz;
        return distSq(x, y, ix, iy);
    }
	
	public static float distanceOnLine(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        return v / (dx * dx + dy * dy);
    }
	
	public static float distSq(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }
	
    public static boolean intersect(float ax1, float ay1, float ax2, float ay2, float bx1, float by1, float bx2, float by2) {
        return relativeCCW(ax1, ay1, ax2, ay2, bx1, by1) * relativeCCW(ax1, ay1, ax2, ay2, bx2, by2) <= 0 && relativeCCW(bx1, by1, bx2, by2, ax1, ay1) * relativeCCW(bx1, by1, bx2, by2, ax2, ay2) <= 0;
    }
    
    private static int relativeCCW(float x1, float y1, float x2, float y2, float px, float py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0.0) {
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }
}
