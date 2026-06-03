package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Abs(Noise input) implements Noise {
	public static final MapCodec<Abs> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
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
	public MapCodec<Abs> codec() {
		return CODEC;
	}
}
