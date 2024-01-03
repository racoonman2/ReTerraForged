package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Frequency(Noise input, Noise xFreq, Noise zFreq) implements Noise {
	public static final Codec<Frequency> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Frequency::input),
		Noise.HOLDER_HELPER_CODEC.fieldOf("x_freq").forGetter(Frequency::xFreq),
		Noise.HOLDER_HELPER_CODEC.fieldOf("z_freq").forGetter(Frequency::zFreq)
	).apply(instance, Frequency::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float xFreq = this.xFreq.compute(x, z, seed);
		float zFreq = this.zFreq.compute(x, z, seed);
		return this.input.compute(x * xFreq, z * zFreq, seed);
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
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Frequency(this.input.mapAll(visitor), this.xFreq.mapAll(visitor), this.zFreq.mapAll(visitor)));
	}

	@Override
	public Codec<Frequency> codec() {
		return CODEC;
	}
}
