package raccoonman.reterraforged.world.worldgen.continent.fancy;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;

public class Segment {
	public Vec2f a;
	public Vec2f b;
	public float dx;
	public float dy;
	public float length;
	public float length2;
	public float scaleA;
	public float scale2A;
	public float scaleB;
	public float scale2B;

	public Segment(Vec2f a, Vec2f b, float scaleA, float scaleB) {
		this.a = a;
		this.b = b;
		this.scaleA = scaleA;
		this.scaleB = scaleB;
		this.scale2A = scaleA * scaleA;
		this.scale2B = scaleB * scaleB;
		this.dx = b.x() - a.x();
		this.dy = b.y() - a.y();
		this.length = (float) Math.sqrt(this.dx * this.dx + this.dy * this.dy);
		this.length2 = this.dx * this.dx + this.dy * this.dy;
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

	public Segment translate(Vec2f offset) {
		return new Segment(new Vec2f(this.a.x() + offset.x(), this.a.y() + offset.y()), new Vec2f(this.b.x() + offset.x(), this.b.y() + offset.y()), this.scaleA, this.scaleB);
	}
}
