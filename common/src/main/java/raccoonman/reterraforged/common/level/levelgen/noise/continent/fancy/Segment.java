// 
// Decompiled by Procyon v0.5.36
// 

package raccoonman.reterraforged.common.level.levelgen.noise.continent.fancy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;

public record Segment(Vec2f a, Vec2f b, float dx, float dy, float length2, float scaleA, float scaleB) {
	public static final Codec<Segment> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Vec2f.CODEC.fieldOf("a").forGetter(Segment::a),
		Vec2f.CODEC.fieldOf("b").forGetter(Segment::b),
		Codec.FLOAT.fieldOf("dx").forGetter(Segment::dx),
		Codec.FLOAT.fieldOf("dy").forGetter(Segment::dy),
		Codec.FLOAT.fieldOf("length2").forGetter(Segment::length2),
		Codec.FLOAT.fieldOf("scaleA").forGetter(Segment::scaleA),
		Codec.FLOAT.fieldOf("scaleB").forGetter(Segment::scaleB)
	).apply(instance, Segment::new));
	
    public static Segment create(final Vec2f a, final Vec2f b, final float scaleA, final float scaleB) {
    	float dx = b.x() - a.x();
    	float dy = b.y() - a.y();
    	return new Segment(a, b, dx, dy, dx * dx + dy * dy, scaleA, scaleB);
    }
    
    public float minX() {
        return Math.min(this.a.x(), this.b.x());
    }
    
    public float minY() {
        return Math.min(this.a.y(), this.b.y());
    }
    
    public float maxX() {
        return Math.max(this.a.x(), this.b.x());
    }
    
    public float maxY() {
        return Math.max(this.a.y(), this.b.y());
    }
    
    public float maxScale() {
        return Math.max(this.scaleA, this.scaleB);
    }
    
    public Segment translate(final Vec2f offset) {
        return create(new Vec2f(this.a.x() + offset.x(), this.a.y() + offset.y()), new Vec2f(this.b.x() + offset.x(), this.b.y() + offset.y()), this.scaleA, this.scaleB);
    }
}
