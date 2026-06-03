package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Threshold(Noise input, Noise lower, Noise upper, Noise threshold) implements Noise {
	public static final MapCodec<Threshold> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Threshold::input),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lower").forGetter(Threshold::lower),
		Noise.HOLDER_HELPER_CODEC.fieldOf("upper").forGetter(Threshold::upper),
		Noise.HOLDER_HELPER_CODEC.fieldOf("threshold").forGetter(Threshold::threshold)
	).apply(instance, Threshold::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		if(this.input.compute(x, z, seed) > this.threshold.compute(x, z, seed)) {
			return this.upper.compute(x, z, seed);
		} else {
			return this.lower.compute(x, z, seed);
		}
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
		return visitor.apply(new Threshold(this.input.mapAll(visitor), this.lower.mapAll(visitor), this.upper.mapAll(visitor), this.threshold.mapAll(visitor)));
	}

	@Override
	public MapCodec<Threshold> codec() {
		return CODEC;
	}
}
