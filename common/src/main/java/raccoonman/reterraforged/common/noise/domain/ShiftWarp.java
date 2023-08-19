package raccoonman.reterraforged.common.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ShiftWarp(Domain delegate, int shift) implements Domain {
	public static final Codec<ShiftWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("delegate").forGetter(ShiftWarp::delegate),
		Codec.INT.fieldOf("shift").forGetter(ShiftWarp::shift)
	).apply(instance, ShiftWarp::new));
	
	@Override
	public float getOffsetX(float x, float y, int seed) {
		return this.delegate.getOffsetX(x, y, this.shift + seed);
	}

	@Override
	public float getOffsetY(float x, float y, int seed) {
		return this.delegate.getOffsetY(x, y, this.shift + seed);
	}

	@Override
	public Codec<ShiftWarp> codec() {
		return CODEC;
	}
}
