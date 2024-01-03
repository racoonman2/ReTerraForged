package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Abs(Noise input) implements Noise {
	public static final Codec<Abs> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Abs::input)
	).apply(instance, Abs::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return Math.abs(this.input.compute(x, z, seed));
	}

	@Override
	public float minValue() {
		return Math.abs(this.input.minValue());
	}

	@Override
	public float maxValue() {
		return Math.abs(this.input.maxValue());
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this.input.mapAll(visitor));
	}

	@Override
	public Codec<Abs> codec() {
		return CODEC;
	}
}
