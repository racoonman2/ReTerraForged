package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record Negate(Noise noise) implements Noise {
	public static final Codec<Negate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("noise").forGetter(Negate::noise)
	).apply(instance, Negate::new));
	
	@Override
	public Codec<Negate> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return -this.noise.compute(x, y, seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Negate(this.noise.mapAll(visitor)));
	}
}
