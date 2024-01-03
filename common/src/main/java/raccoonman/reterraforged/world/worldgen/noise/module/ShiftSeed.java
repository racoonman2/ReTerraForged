package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record ShiftSeed(Noise input, int shift) implements Noise {
	public static final Codec<ShiftSeed> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(ShiftSeed::input),
		Codec.INT.fieldOf("shift").forGetter(ShiftSeed::shift)
	).apply(instance, ShiftSeed::new));

	@Override
	public float compute(float x, float z, int seed) {
		return this.input.compute(x, z, seed + this.shift);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new ShiftSeed(this.input.mapAll(visitor), this.shift));
	}
	

	@Override
	public float minValue() {
		return this.input.minValue();
	}

	@Override
	public float maxValue() {
		return this.input.maxValue();
	}
	
	@Override
	public Codec<ShiftSeed> codec() {
		return CODEC;
	}
}
