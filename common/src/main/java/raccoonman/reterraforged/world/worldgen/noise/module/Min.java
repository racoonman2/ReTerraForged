package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Min(Noise input1, Noise input2) implements Noise {
	public static final Codec<Min> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input1").forGetter(Min::input1),
		Noise.HOLDER_HELPER_CODEC.fieldOf("input2").forGetter(Min::input2)
	).apply(instance, Min::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return Math.min(this.input1.compute(x, z, seed), this.input2.compute(x, z, seed));
	}

	@Override
	public float minValue() {
		return Math.min(this.input1.minValue(), this.input2.minValue());
	}

	@Override
	public float maxValue() {
		return Math.min(this.input1.maxValue(), this.input2.maxValue());
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return new Min(this.input1.mapAll(visitor), this.input2.mapAll(visitor));
	}

	@Override
	public Codec<Min> codec() {
		return CODEC;
	}
}
