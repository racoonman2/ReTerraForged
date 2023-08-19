/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
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

package raccoonman.reterraforged.common.noise.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class Line implements Noise {
	public static final Codec<Line> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("x1", 0.0F).forGetter((m) -> m.x1),
		Codec.FLOAT.optionalFieldOf("y1", 0.0F).forGetter((m) -> m.y1),
		Codec.FLOAT.optionalFieldOf("x2", 0.0F).forGetter((m) -> m.x2),
		Codec.FLOAT.optionalFieldOf("y2", 0.0F).forGetter((m) -> m.y2),
		Noise.DIRECT_CODEC.fieldOf("radius").forGetter((m) -> m.radius),
		Noise.DIRECT_CODEC.fieldOf("fade_in").forGetter((m) -> m.fadeIn),
		Noise.DIRECT_CODEC.fieldOf("fade_out").forGetter((m) -> m.fadeOut),
		Codec.FLOAT.optionalFieldOf("feather", 0.0F).forGetter((m) -> m.featherScale)
	).apply(instance, Line::new));
	
    private final float x1;
    private final float y1;
    private final float x2;
    private final float y2;
    private final float dx;
    private final float dy;
    private final float orthX1;
    private final float orthY1;
    private final float orthX2;
    private final float orthY2;
    private final float length2;
    private final float featherBias;
    private final float featherScale;
    private final Noise fadeIn;
    private final Noise fadeOut;
    private final Noise radius;

    public Line(float x1, float y1, float x2, float y2, Noise radius, Noise fadeIn, Noise fadeOut, float feather) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.orthX1 = x1 + (y2 - y1);
        this.orthY1 = y1 + (x1 - x2);
        this.orthX2 = x2 + (y2 - y1);
        this.orthY2 = y2 + (x1 - x2);
        this.dx = x2 - x1;
        this.dy = y2 - y1;
        this.fadeIn = fadeIn;
        this.fadeOut = fadeOut;
        this.radius = radius;
        this.featherScale = feather;
        this.featherBias = 1 - feather;
        this.length2 = dx * dx + dy * dy;
    }
    
    @Override
    public float getValue(float x, float y, int seed) {
        float widthMod = getWidthModifier(x, y, seed);
        return getValue(x, y, widthMod, seed);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (Float.compare(line.x1, x1) != 0) return false;
        if (Float.compare(line.y1, y1) != 0) return false;
        if (Float.compare(line.x2, x2) != 0) return false;
        if (Float.compare(line.y2, y2) != 0) return false;
        if (Float.compare(line.dx, dx) != 0) return false;
        if (Float.compare(line.dy, dy) != 0) return false;
        if (Float.compare(line.orthX1, orthX1) != 0) return false;
        if (Float.compare(line.orthY1, orthY1) != 0) return false;
        if (Float.compare(line.orthX2, orthX2) != 0) return false;
        if (Float.compare(line.orthY2, orthY2) != 0) return false;
        if (Float.compare(line.length2, length2) != 0) return false;
        if (Float.compare(line.featherBias, featherBias) != 0) return false;
        if (Float.compare(line.featherScale, featherScale) != 0) return false;
        if (!fadeIn.equals(line.fadeIn)) return false;
        if (!fadeOut.equals(line.fadeOut)) return false;
        return radius.equals(line.radius);
    }

    @Override
    public int hashCode() {
        int result = (x1 != +0.0f ? Float.floatToIntBits(x1) : 0);
        result = 31 * result + (y1 != +0.0f ? Float.floatToIntBits(y1) : 0);
        result = 31 * result + (x2 != +0.0f ? Float.floatToIntBits(x2) : 0);
        result = 31 * result + (y2 != +0.0f ? Float.floatToIntBits(y2) : 0);
        result = 31 * result + (dx != +0.0f ? Float.floatToIntBits(dx) : 0);
        result = 31 * result + (dy != +0.0f ? Float.floatToIntBits(dy) : 0);
        result = 31 * result + (orthX1 != +0.0f ? Float.floatToIntBits(orthX1) : 0);
        result = 31 * result + (orthY1 != +0.0f ? Float.floatToIntBits(orthY1) : 0);
        result = 31 * result + (orthX2 != +0.0f ? Float.floatToIntBits(orthX2) : 0);
        result = 31 * result + (orthY2 != +0.0f ? Float.floatToIntBits(orthY2) : 0);
        result = 31 * result + (length2 != +0.0f ? Float.floatToIntBits(length2) : 0);
        result = 31 * result + (featherBias != +0.0f ? Float.floatToIntBits(featherBias) : 0);
        result = 31 * result + (featherScale != +0.0f ? Float.floatToIntBits(featherScale) : 0);
        result = 31 * result + fadeIn.hashCode();
        result = 31 * result + fadeOut.hashCode();
        result = 31 * result + radius.hashCode();
        return result;
    }
    
    @Override
	public Codec<Line> codec() {
		return CODEC;
	}

    public float getValue(float x, float y, float widthModifier, int seed) {
        return getValue(x, y, 0, widthModifier, seed);
    }

    public float getValue(float x, float y, float minWidth2, float widthModifier, int seed) {
        float dist2 = getDistance2(x, y);
        float radius2 = minWidth2 + radius.getValue(x, y, seed) * widthModifier;
        if (dist2 > radius2) {
            return 0;
        }
        float value = dist2 / radius2;
        if (featherScale == 0) {
            return 1 - value;
        }
        float feather = featherBias + (widthModifier * featherScale);
        return (1 - value) * feather;
    }

    /**
     * Check if the position x,y is 'before' the start of this line
     */
    public boolean clipStart(float x, float y) {
        return sign(x, y, x1, y1, orthX1, orthY1) > 0;
    }

    /**
     * Check if the position x,y is past the end of this line
     */
    public boolean clipEnd(float x, float y) {
        return sign(x, y, x2, y2, orthX2, orthY2) < 0;
    }

    public float getWidthModifier(float x, float y, int seed) {
        float d1 = dist2(x, y, x1, y1);
        if (d1 == 0) {
            return 0;
        }

        float d2 = dist2(x, y, x2, y2);
        if (d2 == 0) {
            return 0;
        }

        float fade = 1F;
        float in = fadeIn.getValue(x, y, seed);
        float out = fadeOut.getValue(x, y, seed);
        if (in > 0) {
            float dist = in * length2;
            if (d1 < dist) {
                fade *= (d1 / dist);
            }
        }

        if (out > 0) {
            float dist = out * length2;
            if (d2 < dist) {
                fade *= (d2 / dist);
            }
        }
        return fade;
    }

    private float getDistance2(float x, float y) {
        float t = ((x - x1) * dx) + ((y - y1) * dy);
        float s = NoiseUtil.clamp(t / length2, 0, 1);
        float ix = x1 + s * dx;
        float iy = y1 + s * dy;
        return dist2(x, y, ix, iy);
    }

    public static float dist2(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    public static int sign(float x, float y, float x1, float y1, float x2, float y2) {
        float value = (x - x1) * (y2 - y1) - (y - y1) * (x2 - x1);
        if (value == 0) {
            return 0;
        }
        if (value < 0) {
            return -1;
        }
        return 1;
    }

    public static boolean intersect(float ax1, float ay1, float ax2, float ay2, float bx1, float by1, float bx2, float by2) {
        return ((relativeCCW(ax1, ay1, ax2, ay2, bx1, by1) * relativeCCW(ax1, ay1, ax2, ay2, bx2, by2) <= 0)
                && (relativeCCW(bx1, by1, bx2, by2, ax1, ay1) * relativeCCW(bx1, by1, bx2, by2, ax2, ay2) <= 0));
    }

    public static float distanceOnLine(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        return v / (dx * dx + dy * dy);
    }

    public static float distance2Line(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;

        v /= dx * dx + dy * dy;

        if (v < 0 || v > 1) {
            return -1;
        }

        float ox = ax + dx * v;
        float oy = ay + dy * v;

        return NoiseUtil.dist2(x, y, ox, oy);
    }

    public static float distance2LineIncEnds(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;

        v /= dx * dx + dy * dy;

        if (v < 0) {
            return Line.dist2(x, y, ax, ay);
        } else if (v > 1) {
            return Line.dist2(x, y, bx, by);
        } else {
            return Line.dist2(x, y, ax + dx * v, ay + dy * v);
        }
    }

    private static int relativeCCW(float x1, float y1, float x2, float y2, float px, float py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0F) {
            // The point is colinear, classify based on which side of
            // the segment the point falls on.  We can calculate a
            // relative value using the projection of px,py onto the
            // segment - a negative value indicates the point projects
            // outside of the segment in the direction of the particular
            // endpoint used as the origin for the projection.
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                // Reverse the projection to be relative to the original x2,y2
                // x2 and y2 are simply negated.
                // px and py need to have (x2 - x1) or (y2 - y1) subtracted
                //    from them (based on the original values)
                // Since we really want to get a positive answer when the
                //    point is "beyond (x2,y2)", then we want to calculate
                //    the inverse anyway - thus we leave x2 & y2 negated.
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0F) ? -1 : ((ccw > 0F) ? 1 : 0);
    }
}
