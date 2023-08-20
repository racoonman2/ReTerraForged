package raccoonman.reterraforged.common.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ShiftWarp(Domain source, int shift) implements Domain {
	public static final Codec<ShiftWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("delegate").forGetter(ShiftWarp::source),
		Codec.INT.fieldOf("source").forGetter(ShiftWarp::shift)
	).apply(instance, ShiftWarp::new));
	
	@Override
	public float getOffsetX(float x, float y, int seed) {
		return this.source.getOffsetX(x, y, this.shift + seed);
	}

	@Override
	public float getOffsetY(float x, float y, int seed) {
		return this.source.getOffsetY(x, y, this.shift + seed);
	}

	@Override
	public Codec<ShiftWarp> codec() {
		return CODEC;
	}
}
