//package raccoonman.reterraforged.common.level.levelgen.noise.continent.fancy;
//
//import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
//import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
//import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;
//
//public class Island {
//    private int id;
//    private Segment[] segments;
//    private ControlPoints controlPoints;
//    private float coast2;
//    private float deepOcean;
//    private float deepOcean2;
//    private float shallowOcean;
//    private float shallowOcean2;
//    private float coast;
//    private float inland;
//    private float radius;
//    private float deepMod;
//    private float shallowMod;
//    private Vec2f center;
//    private Vec2f min;
//    private Vec2f max;
//    
//    public Island(int id, Segment[] segments, ControlPoints controlPoints, float deepOcean, float shallowOcean, float coast, float inland) {
//        float x = 0.0f;
//        float y = 0.0f;
//        int points = segments.length + 1;
//        float minX = Float.MAX_VALUE;
//        float minZ = Float.MAX_VALUE;
//        float maxX = Float.MIN_VALUE;
//        float maxZ = Float.MIN_VALUE;
//        float maxRadius = coast;
//        for (int i = 0; i < segments.length; ++i) {
//            Segment segment = segments[i];
//            minX = Math.min(minX, segment.minX());
//            minZ = Math.min(minZ, segment.minY());
//            maxX = Math.max(maxX, segment.maxX());
//            maxZ = Math.max(maxZ, segment.maxY());
//            maxRadius = Math.max(maxRadius, segment.maxScale() * coast);
//            if (i == 0) {
//                x += segment.a().x();
//                y += segment.a().y();
//            }
//            x += segment.b().x();
//            y += segment.b().y();
//        }
//        this.id = id;
//        this.segments = segments;
//        this.controlPoints = controlPoints;
//        this.coast = coast;
//        this.inland = inland;
//        this.deepOcean = deepOcean;
//        this.shallowOcean = shallowOcean;
//        this.coast2 = coast * coast;
//        this.deepOcean2 = deepOcean * deepOcean;
//        this.shallowOcean2 = shallowOcean * shallowOcean;
//        this.deepMod = 0.25f;
//        this.shallowMod = 1.0f - this.deepMod;
//        minX -= coast;
//        minZ -= coast;
//        maxX += coast;
//        maxZ += coast;
//        float maxDim = Math.max(maxX - minX, maxZ - minZ);
//        this.center = new Vec2f(x / points, y / points);
//        this.min = new Vec2f(minX - maxRadius, minZ - maxRadius);
//        this.max = new Vec2f(maxX + maxRadius, maxZ + maxRadius);
//        this.radius = maxDim;
//    }
//    
//    public int getId() {
//        return this.id;
//    }
//    
//    public Segment[] getSegments() {
//        return this.segments;
//    }
//    
//    public float radius() {
//        return this.radius;
//    }
//    
//    public float coast() {
//        return this.shallowOcean;
//    }
//    
//    public void translate(float x, float y) {
//        this.center = new Vec2f(this.center.x() + x, this.center.y() + y);
//        this.min = new Vec2f(this.min.x() + x, this.min.y() + y);
//        this.max = new Vec2f(this.max.x() + x, this.max.y() + y);
//        for (int i = 0; i < this.segments.length; ++i) {
//            this.segments[i] = this.segments[i].translate(x, y);
//        }
//    }
//    
//    public Vec2f getMin() {
//        return this.min;
//    }
//    
//    public Vec2f getMax() {
//        return this.max;
//    }
//    
//    public Vec2f getCenter() {
//        return this.center;
//    }
//    
//    public boolean overlaps(Island other) {
//        return this.overlaps(other.min, other.max);
//    }
//    
//    public boolean overlaps(Vec2f min, Vec2f max) {
//        return this.min.x() < max.x() && this.max.x() > min.x() && this.min.y() < max.y() && this.max.y() > min.y();
//    }
//    
//    public boolean contains(Vec2f vec) {
//        return this.contains(vec.x(), vec.y());
//    }
//    
//    public boolean contains(float x, float z) {
//        return x > this.min.x() && x < this.max.x() && z > this.min.y() && z < this.max.y();
//    }
//    
//    public float getEdgeValue(float x, float y) {
//        float value = this.getEdgeDist2(x, y, this.deepOcean2);
//        float deepValue = Math.min(this.deepOcean2, value);
//        float shallowValue = Math.min(this.shallowOcean2, value);
//        return this.process(deepValue, shallowValue);
//    }
//    
//    public float getLandValue(float x, float y) {
//        float value = this.getEdgeDist2(x, y, this.shallowOcean2);
//        if (value < this.shallowOcean2) {
//            value = (this.shallowOcean2 - value) / this.shallowOcean2;
//            return NoiseUtil.curve(value, 0.75f, 4.0f);
//        }
//        return 0.0f;
//    }
//    
//    private float getEdgeDist2(float x, float y, float minDist2) {
//        float value = minDist2;
//        for (Segment segment : this.segments) {
//            float dx = segment.dx();
//            float dy = segment.dy();
//            float scale2A = segment.scaleA() * segment.scaleA();
//            float scale2B = segment.scaleB() * segment.scaleB();
//            float length2 = dx * dx + dy * dy;
//            
//            float t = (x - segment.a().x()) * dx + (y - segment.a().y()) * dy;
//            t /= length2;
//            float px;
//            float py;
//            float scale;
//            if (t < 0.0f) {
//                px = segment.a().x();
//                py = segment.a().y();
//                scale = scale2A;
//            } else if (t > 1.0f) {
//                px = segment.b().x();
//                py = segment.b().y();
//                scale = scale2B;
//            } else {
//                px = segment.a().x() + t * dx;
//                py = segment.a().y() + t * dy;
//                scale = NoiseUtil.lerp(scale2A, scale2B, t);
//            }
//            float v = Line.dist2(x, y, px, py) / scale;
//            value = Math.min(v, value);
//        }
//        return value;
//    }
//    
//    private float process(float deepValue, float shallowValue) {
//        if (deepValue == this.deepOcean2) {
//            return 0.0f;
//        }
//        if (deepValue > this.shallowOcean2) {
//            deepValue = (deepValue - this.shallowOcean2) / (this.deepOcean2 - this.shallowOcean2);
//            deepValue = 1.0f - deepValue;
//            deepValue *= deepValue;
//            return deepValue * this.deepMod;
//        }
//        if (shallowValue == this.shallowOcean2) {
//            return this.deepMod;
//        }
//        if (shallowValue > this.coast2) {
//            shallowValue = (shallowValue - this.coast2) / (this.shallowOcean2 - this.coast2);
//            shallowValue = 1.0f - shallowValue;
//            return this.deepMod + shallowValue * this.shallowMod;
//        }
//        return 1.0f;
//    }
//}
