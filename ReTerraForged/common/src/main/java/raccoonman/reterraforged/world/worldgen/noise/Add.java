package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Add(Noise input1, Noise input2) implements Noise {
	public static final MapCodec<Add> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input1").forGetter(Add::input1),
		Noise.HOLDER_HELPER_CODEC.fieldOf("input2").forGetter(Add::input2)
	).apply(instance, Add::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return this.input1.compute(x, z, seed) + this.input2.compute(x, z, seed);
	}

	@Override
	public float minValue() {
		return this.input1.minValue() + this.input2.minValue();
	}

	@Override
	public float maxValue() {
		return this.input1.maxValue() + this.input2.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Add(this.input1.mapAll(visitor), this.input2.mapAll(visitor)));
	}

	@Override
	public MapCodec<Add> codec() {
		return CODEC;
	}
}
