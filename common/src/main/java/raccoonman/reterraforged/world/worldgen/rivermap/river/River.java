package raccoonman.reterraforged.world.worldgen.rivermap.river;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2i;
import raccoonman.reterraforged.world.worldgen.noise.module.Line;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;
import raccoonman.reterraforged.world.worldgen.util.Variance;

public class River {
    public static final int VALLEY_WIDTH = 275;
    public static final Variance MAIN_VALLEY = Variance.of(0.8F, 0.7F);
    public static final Variance FORK_VALLEY = Variance.of(0.4F, 0.75F);
    public static final Variance FORK_ANGLE = Variance.of(0.075F, 0.115F);
    public static final Variance MAIN_SPACING = Variance.of(0.1F, 0.25F);
    public static final Variance FORK_SPACING = Variance.of(0.25F, 0.25F);

    public float x1;
    public float z1;
    public float x2;
    public float z2;
    public float dx;
    public float dz;
    public float ndx;
    public float ndz;
    public float normX;
    public float normZ;
    public float length;
    public float length2;
    public float minX;
    public float minZ;
    public float maxX;
    public float maxZ;
    public boolean weird;
    
    public River(float x1, float z1, float x2, float z2) {
        this(x1, z1, x2, z2, 275.0F);
    }
    
    public River(float x1, float z1, float x2, float z2, float radius) {
        radius *= 2.0F;
        this.x1 = x1;
        this.z1 = z1;
        this.x2 = x2;
        this.z2 = z2;
        this.dx = x2 - x1;
        this.dz = z2 - z1;
        this.length = (float) Math.sqrt(this.dx * this.dx + this.dz * this.dz);
        this.length2 = this.length * this.length;
        this.ndx = this.dx / this.length;
        this.ndz = this.dz / this.length;
        this.normX = this.ndz;
        this.normZ = -this.ndx;
        this.minX = Math.min(x1, x2) - radius;
        this.minZ = Math.min(z1, z2) - radius;
        this.maxX = Math.max(x1, x2) + radius;
        this.maxZ = Math.max(z1, z2) + radius;
        this.weird = PosUtil.pack(x1, z1) % 2 == 0;
    }
    
    public float length() {
        return this.length;
    }
    
    public float getAngle() {
        return (float) Math.atan2(this.dx, this.dz);
    }
    
    public long pos(float distance) {
        return PosUtil.packf(this.x1 + this.dx * distance, this.z1 + this.dz * distance);
    }
    
    public long pos(float distance, RiverWarp warp) {
        float x = this.x1 + this.dx * distance;
        float z = this.z1 + this.dz * distance;
        if (warp.test(distance)) {
            long offset = warp.getOffset(x, z, distance, this);
            x -= PosUtil.unpackLeftf(offset);
            z -= PosUtil.unpackRightf(offset);
        }
        return PosUtil.packf(x, z);
    }
    
    public boolean intersects(River other) {
        return Line.intersect(other.x1, other.z1, other.x2, other.z2, this.x1, this.z1, this.x2, this.z2);
    }
    
    public boolean intersects(River other, float extend) {
        float extendA = NoiseUtil.clamp(extend / this.length, 0.0F, 1.0F);
        float extendB = NoiseUtil.clamp(extend / other.length, 0.0F, 1.0F);
        float deltaAX = this.x2 - this.x1;
        float deltaAY = this.z2 - this.z1;
        float deltaBX = other.x2 - other.x1;
        float deltaBY = other.z2 - other.z1;
        float ax1 = this.x1 - deltaAX * extendA;
        float ax2 = this.x2 + deltaAX * extendA;
        float ay1 = this.z1 - deltaAY * extendA;
        float ay2 = this.z2 + deltaAY * extendA;
        float bx1 = other.x1 - deltaBX * extendB;
        float bx2 = other.x2 + deltaBX * extendB;
        float by1 = other.z1 - deltaBY * extendB;
        float by2 = other.z2 + deltaBY * extendB;
        return Line.intersect(ax1, ay1, ax2, ay2, bx1, by1, bx2, by2);
    }
    
    public boolean contains(float x, float z) {
        return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
    }
    
    public boolean overlaps(River other) {
        return this.overlaps(other.minX, other.minZ, other.maxX, other.maxZ);
    }
    
    public boolean overlaps(float minX, float minY, float maxX, float maxY) {
        return this.minX < maxX && this.maxX > minX && this.minZ < maxY && this.maxZ > minY;
    }
    
    public boolean overlaps(Vec2f center, float radius) {
        float minX = center.x() - radius;
        float maxX = center.x() + radius;
        float minY = center.y() - radius;
        float maxY = center.y() + radius;
        return this.overlaps(minX, minY, maxX, maxY);
    }
    
    public River shorten(int distance) {
        float factor = distance / this.length();
        float dx = this.x2 - this.x1;
        float dy = this.z2 - this.z1;
        float x = NoiseUtil.round(this.x1 + dx * factor);
        float y = NoiseUtil.round(this.z1 + dy * factor);
        return new River(x, y, this.x2, this.z2);
    }
    
    @Override
    public String toString() {
        return "RiverBounds{x1=" + this.x1 + ", y1=" + this.z1 + ", x2=" + this.x2 + ", y2=" + this.z2 + ", length=" + this.length + ", length2=" + this.length2 + '}';
    }
    
    public static River fromNodes(Vec2i p1, Vec2i p2) {
        return new River(p1.x(), p1.y(), p2.x(), p2.y(), 300.0F);
    }
}
