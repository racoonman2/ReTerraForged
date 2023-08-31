package raccoonman.reterraforged.common.level.levelgen.noise.domain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ShiftWarp(Domain source, int shift) implements Domain {
	public static final Codec<ShiftWarp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Domain.CODEC.fieldOf("source").forGetter(ShiftWarp::source),
		Codec.INT.fieldOf("shift").forGetter(ShiftWarp::shift)
	).apply(instance, ShiftWarp::new));
	
	@Override
	public float getOffsetX(float x, float y, int seed) {
		return this.source.getOffsetX(x, y, seed + this.shift);
	}

	@Override
	public float getOffsetY(float x, float y, int seed) {
		return this.source.getOffsetY(x, y, seed + this.shift);
	}

	@Override
	public Codec<ShiftWarp> codec() {
		return CODEC;
	}
}
