package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record Lerp(Noise alpha, Noise from, Noise to) implements Noise {
	public static final MapCodec<Lerp> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(Lerp::alpha),
		Noise.HOLDER_HELPER_CODEC.fieldOf("from").forGetter(Lerp::from),
		Noise.HOLDER_HELPER_CODEC.fieldOf("to").forGetter(Lerp::to)
	).apply(instance, Lerp::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float alpha = this.alpha.compute(x, z, seed);
		if(alpha == 0.0F) {
			return this.from.compute(x, z, seed);
		}
		if(alpha == 1.0F) {
			return this.to.compute(x, z, seed);
		}
		return NoiseUtil.lerp(this.from.compute(x, z, seed), this.to.compute(x, z, seed), alpha);
	}

	@Override
	public float minValue() {
		return this.from.minValue();
	}

	@Override
	public float maxValue() {
		return this.to.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Lerp(this.alpha.mapAll(visitor), this.from.mapAll(visitor), this.to.mapAll(visitor)));
	}

	@Override
	public MapCodec<Lerp> codec() {
		return CODEC;
	}
}
