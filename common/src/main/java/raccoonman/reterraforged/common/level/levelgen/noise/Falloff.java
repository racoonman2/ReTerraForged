package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.util.CodecUtil;
import raccoonman.reterraforged.common.util.MathUtil;

public record Falloff(Noise source, Falloff.Point... points) implements Noise {
	public static final Codec<Falloff> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter(Falloff::source),
		CodecUtil.forArray(Falloff.Point.CODEC, Falloff.Point[]::new).fieldOf("points").forGetter(Falloff::points)
	).apply(instance, Falloff::new));
	//TODO remove this?
	@Deprecated
	public static final float MIN = 0.1F;
	
	@Override
	public Codec<? extends Noise> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		float source = this.source.getValue(x, y, seed);
		float previous = 1.0F;
		for (Falloff.Point falloff : this.points) {
			if (source >= falloff.controlPoint()) {
				return MathUtil.map(source, falloff.controlPoint(), previous, falloff.min(), falloff.max());
			}
			previous = falloff.controlPoint();
		}
		return MathUtil.map(source, 0.0F, previous, 0.0F, MIN);
	}

	public record Point(float controlPoint, float min, float max) {
		public static final Codec<Falloff.Point> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.FLOAT.fieldOf("control_point").forGetter(Falloff.Point::controlPoint),
			Codec.FLOAT.fieldOf("min").forGetter(Falloff.Point::min),
			Codec.FLOAT.fieldOf("max").forGetter(Falloff.Point::max)
		).apply(instance, Falloff.Point::new));
	}
}
