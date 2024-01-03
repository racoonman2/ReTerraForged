package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Max(Noise input1, Noise input2) implements Noise {
	public static final Codec<Max> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input1").forGetter(Max::input1),
		Noise.HOLDER_HELPER_CODEC.fieldOf("input2").forGetter(Max::input2)
	).apply(instance, Max::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return Math.max(this.input1.compute(x, z, seed), this.input2.compute(x, z, seed));
	}

	@Override
	public float minValue() {
		return Math.max(this.input1.minValue(), this.input2.minValue());
	}

	@Override
	public float maxValue() {
		return Math.max(this.input1.maxValue(), this.input2.maxValue());
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Max(this.input1.mapAll(visitor), this.input2.mapAll(visitor)));
	}

	@Override
	public Codec<Max> codec() {
		return CODEC;
	}
}
