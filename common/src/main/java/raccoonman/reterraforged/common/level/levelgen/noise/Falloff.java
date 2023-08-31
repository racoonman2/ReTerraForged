package raccoonman.reterraforged.common.level.levelgen.noise;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record Falloff(Noise source, float min, List<Falloff.Point> points) implements Noise {
	public static final Codec<Falloff> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Falloff::source),
		Codec.FLOAT.fieldOf("min").forGetter(Falloff::min),
		Falloff.Point.CODEC.listOf().fieldOf("points").forGetter(Falloff::points)
	).apply(instance, Falloff::new));
	
	public Falloff {
		points = ImmutableList.copyOf(points);
	}

	@Override
	public float maxValue() {
		return this.source.maxValue();
	}
	
	@Override
	public float minValue() {
		return this.source.minValue();
	}
	
	@Override
	public Codec<Falloff> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float source = this.source.getValue(x, y, seed);
		float previous = this.source.maxValue();
		final float min = this.source.minValue();
		for (Falloff.Point falloff : this.points) {
			if (source >= falloff.point()) {
				return map(source, falloff.point(), previous, falloff.min(), falloff.max());
			}
			previous = falloff.point();
		}
		return map(source, min, previous, min, this.min);
	}

	private static float map(float value, float min0, float max0, float min1, float max1) {
		float alpha = NoiseUtil.map(value, min0, max0, max0 - min0);
		return NoiseUtil.lerp(min1, max1, alpha);
	}
	
	public record Point(float point, float min, float max) {
		public static final Codec<Falloff.Point> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("min").forGetter(Falloff.Point::point),
			Codec.FLOAT.fieldOf("max").forGetter(Falloff.Point::min),
			Codec.FLOAT.fieldOf("point").forGetter(Falloff.Point::max)
		).apply(instance, Falloff.Point::new));
	}
}
