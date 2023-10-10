//package raccoonman.reterraforged.common.level.levelgen.noise.continent.fancy;
//
//import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
//
//public record Segment(Vec2f a, Vec2f b, float dx, float dy, float length, float scaleA, float scaleB) {
//
//    public float minX() {
//        return Math.min(this.a.x(), this.b.x());
//    }
//    
//    public float minY() {
//        return Math.min(this.a.y(), this.b.y());
//    }
//    
//    public float maxX() {
//        return Math.max(this.a.x(), this.b.x());
//    }
//    
//    public float maxY() {
//        return Math.max(this.a.y(), this.b.y());
//    }
//    
//    public float maxScale() {
//        return Math.max(this.scaleA, this.scaleB);
//    }
//    
//    public Segment translate(float x, float y) {
//        return of(new Vec2f(this.a.x() + x, this.a.y() + y), new Vec2f(this.b.x() + x, this.b.y() + y), this.scaleA, this.scaleB);
//    }
//    
//    public static Segment of(Vec2f a, Vec2f b, float scaleA, float scaleB) {
//        float dx = b.x() - a.x();
//        float dy = b.y() - a.y();
//        float length = (float) Math.sqrt(dx * dx + dy * dy);
//        return new Segment(a, b, dx, dy, length, scaleA, scaleB);
//    }
//}
