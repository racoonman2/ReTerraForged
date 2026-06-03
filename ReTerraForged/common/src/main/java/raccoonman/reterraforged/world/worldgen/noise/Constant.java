package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Constant(float value) implements Noise {
	public static final MapCodec<Constant> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
		Codec.FLOAT.fieldOf("value").forGetter(Constant::value)
	).apply(instance, Constant::new));

	@Override
	public float compute(float x, float z, int seed) {
		return this.value;
	}

	@Override
	public float minValue() {
		return this.value;
	}

	@Override
	public float maxValue() {
		return this.value;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}

	@Override
	public MapCodec<Constant> codec() {
		return CODEC;
	}
}
