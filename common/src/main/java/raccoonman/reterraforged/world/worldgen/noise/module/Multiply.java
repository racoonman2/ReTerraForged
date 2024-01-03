package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Multiply(Noise input1, Noise input2) implements Noise {
	public static final Codec<Multiply> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input1").forGetter(Multiply::input1),
		Noise.HOLDER_HELPER_CODEC.fieldOf("input2").forGetter(Multiply::input2)
	).apply(instance, Multiply::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float input1 = this.input1.compute(x, z, seed);
		return input1 != 0.0F ? input1 * this.input2.compute(x, z, seed) : 0.0F;
	}

	@Override
	public float minValue() {
		return this.input1.minValue() * this.input2.minValue();
	}

	@Override
	public float maxValue() {
		return this.input1.maxValue() * this.input2.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Multiply(this.input1.mapAll(visitor), this.input2.mapAll(visitor)));
	}

	@Override
	public Codec<Multiply> codec() {
		return CODEC;
	}
}
