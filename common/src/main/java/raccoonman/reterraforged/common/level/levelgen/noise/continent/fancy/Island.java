// 
// Decompiled by Procyon v0.5.36
// 

package raccoonman.reterraforged.common.level.levelgen.noise.continent.fancy;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;

public record Island(List<Segment> segments, float deepOcean, float shallowOcean, float coast, float radius, float deepMod, float shallowMod, Vec2f center, Vec2f min, Vec2f max) {
	public static final Codec<Island> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Segment.CODEC.listOf().fieldOf("segments").forGetter(Island::segments),
		Codec.FLOAT.fieldOf("deep_ocean").forGetter(Island::deepOcean),
		Codec.FLOAT.fieldOf("shallow_ocean").forGetter(Island::shallowOcean),
		Codec.FLOAT.fieldOf("coast").forGetter(Island::coast),
		Codec.FLOAT.fieldOf("radius").forGetter(Island::radius),
		Codec.FLOAT.fieldOf("deep_mod").forGetter(Island::deepMod),
		Codec.FLOAT.fieldOf("shallow_mod").forGetter(Island::shallowMod),
		Vec2f.CODEC.fieldOf("center").forGetter(Island::center),
		Vec2f.CODEC.fieldOf("min").forGetter(Island::min),
		Vec2f.CODEC.fieldOf("max").forGetter(Island::max)
	).apply(instance, Island::new));
    
    public static Island create(Segment[] segments, float deepOcean, float shallowOcean, float coast) {
        float x = 0.0f;
        float y = 0.0f;
        final int points = segments.length + 1;
        float minX = Float.MAX_VALUE;
        float minZ = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxZ = Float.MIN_VALUE;
        float maxRadius = coast;
        for (int i = 0; i < segments.length; ++i) {
            final Segment segment = segments[i];
            minX = Math.min(minX, segment.minX());
            minZ = Math.min(minZ, segment.minY());
            maxX = Math.max(maxX, segment.maxX());
            maxZ = Math.max(maxZ, segment.maxY());
            maxRadius = Math.max(maxRadius, segment.maxScale() * coast);
            if (i == 0) {
                x += segment.a().x();
                y += segment.a().y();
            }
            x += segment.b().x();
            y += segment.b().y();
        }
        minX -= coast;
        minZ -= coast;
        maxX += coast;
        maxZ += coast;
        final float maxDim = Math.max(maxX - minX, maxZ - minZ);
        return new Island(
        	ImmutableList.copyOf(segments), deepOcean, shallowOcean, coast, maxDim, 0.25F, 0.75F,  
        	new Vec2f(x / points, y / points),
            new Vec2f(minX - maxRadius, minZ - maxRadius),
            new Vec2f(maxX + maxRadius, maxZ + maxRadius)
        );
    }

    public Island translate(final Vec2f offset) {
        Segment[] segments = new Segment[this.segments.size()];
        for (int i = 0; i < segments.length; ++i) {
            segments[i] = this.segments.get(i).translate(offset);
        }
        return new Island(ImmutableList.copyOf(segments), this.deepOcean, this.shallowOcean, this.coast, this.radius, this.deepMod, this.shallowMod, 
        	new Vec2f(this.center.x() + offset.x(), this.center.y() + offset.y()), 
        	new Vec2f(this.min.x() + offset.x(), this.min.y() + offset.y()),
        	new Vec2f(this.max.x() + offset.x(), this.max.y() + offset.y())
        );
    }
    
    public Vec2f getMin() {
        return this.min;
    }
    
    public Vec2f getMax() {
        return this.max;
    }
    
    public Vec2f getCenter() {
        return this.center;
    }
    
    public boolean overlaps(final Island other) {
        return this.overlaps(other.min, other.max);
    }
    
    public boolean overlaps(final Vec2f min, final Vec2f max) {
        return this.min.x() < max.x() && this.max.x() > min.x() && this.min.y() < max.y() && this.max.y() > min.y();
    }
    
    public boolean contains(final Vec2f vec) {
        return this.contains(vec.x(), vec.y());
    }
    
    public boolean contains(final float x, final float z) {
        return x > this.min.x() && x < this.max.x() && z > this.min.y() && z < this.max.y();
    }
    
    public float getEdgeValue(final float x, final float y) {
        final float value = this.getEdgeDist2(x, y, this.deepOcean * this.deepOcean);
        final float deepValue = Math.min(this.deepOcean * this.deepOcean, value);
        final float shallowValue = Math.min(this.shallowOcean * this.shallowOcean, value);
        return this.process(deepValue, shallowValue);
    }
    
    public float getLandValue(final float x, final float y) {
        float value = this.getEdgeDist2(x, y, this.shallowOcean * this.shallowOcean);
        if (value < this.shallowOcean * this.shallowOcean) {
            value = (this.shallowOcean * this.shallowOcean - value) / this.shallowOcean * this.shallowOcean;
            return NoiseUtil.curve(value, 0.75f, 4.0f);
        }
        return 0.0f;
    }
    
    private float getEdgeDist2(final float x, final float y, final float minDist2) {
        float value = minDist2;
        for (final Segment segment : this.segments) {
            final float dx = segment.dx();
            final float dy = segment.dy();
            float t = (x - segment.a().x()) * dx + (y - segment.a().y()) * dy;
            t /= segment.length2();
            float px;
            float py;
            float scale;
            if (t < 0.0f) {
                px = segment.a().x();
                py = segment.a().y();
                scale = segment.scaleA() * segment.scaleA();
            }
            else if (t > 1.0f) {
                px = segment.b().x();
                py = segment.b().y();
                scale = segment.scaleB() * segment.scaleB();
            }
            else {
                px = segment.a().x() + t * dx;
                py = segment.a().y() + t * dy;
                scale = NoiseUtil.lerp(segment.scaleA() * segment.scaleA(), segment.scaleB() * segment.scaleB(), t);
            }
            final float v = Line.dist2(x, y, px, py) / scale;
            value = Math.min(v, value);
        }
        return value;
    }
    
    private float process(float deepValue, float shallowValue) {
        if (deepValue == this.deepOcean * this.deepOcean) {
            return 0.0f;
        }
        if (deepValue > this.shallowOcean * this.shallowOcean) {
            deepValue = (deepValue - this.shallowOcean * this.shallowOcean) / (this.deepOcean * this.deepOcean - this.shallowOcean * this.shallowOcean);
            deepValue = 1.0f - deepValue;
            deepValue *= deepValue;
            return deepValue * this.deepMod;
        }
        if (shallowValue == this.shallowOcean * this.shallowOcean) {
            return this.deepMod;
        }
        if (shallowValue > this.coast * this.coast) {
            shallowValue = (shallowValue - this.coast * this.coast) / (this.shallowOcean * this.shallowOcean - this.coast * this.coast);
            shallowValue = 1.0f - shallowValue;
            return this.deepMod + shallowValue * this.shallowMod;
        }
        return 1.0f;
    }
}
