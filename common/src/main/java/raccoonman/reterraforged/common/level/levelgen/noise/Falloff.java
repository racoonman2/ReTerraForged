package raccoonman.reterraforged.common.level.levelgen.noise;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.util.MathUtil;

public record Falloff(Noise source, float min, List<Falloff.Point> points) implements Noise {
	public static final Codec<Falloff> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter(Falloff::source),
		Codec.FLOAT.fieldOf("min").forGetter(Falloff::min),
		Falloff.Point.CODEC.listOf().fieldOf("points").forGetter(Falloff::points)
	).apply(instance, Falloff::new));
	
	public Falloff {
		points = ImmutableList.copyOf(points);
	}
	
	@Override
	public Codec<? extends Noise> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float source = this.source.getValue(x, y, seed);
		float previous = 1.0F;
		for (Falloff.Point falloff : this.points) {
			if (source >= falloff.point()) {
				return MathUtil.map(source, falloff.point(), previous, falloff.min(), falloff.max());
			}
			previous = falloff.point();
		}
		return MathUtil.map(source, 0.0F, previous, 0.0F, this.min);
	}

	public record Point(float point, float min, float max) {
		public static final Codec<Falloff.Point> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("point").forGetter(Falloff.Point::point),
			Codec.FLOAT.fieldOf("min").forGetter(Falloff.Point::min),
			Codec.FLOAT.fieldOf("max").forGetter(Falloff.Point::max)
		).apply(instance, Falloff.Point::new));
	}
}
