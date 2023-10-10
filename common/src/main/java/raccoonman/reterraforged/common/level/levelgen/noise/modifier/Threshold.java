package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record Threshold(Noise alpha, Noise threshold, Noise below, Noise above) implements Noise {
	public static final Codec<Threshold> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(Threshold::alpha),
		Noise.HOLDER_HELPER_CODEC.fieldOf("threshold").forGetter(Threshold::threshold),
		Noise.HOLDER_HELPER_CODEC.fieldOf("below").forGetter(Threshold::below),
		Noise.HOLDER_HELPER_CODEC.fieldOf("above").forGetter(Threshold::above)
	).apply(instance, Threshold::new));
	
	@Override
	public Codec<Threshold> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return this.alpha.compute(x, y, seed) > this.threshold.compute(x, y, seed) ? this.above.compute(x, y, seed) : this.below.compute(x, y, seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Threshold(this.alpha.mapAll(visitor), this.threshold.mapAll(visitor), this.below.mapAll(visitor), this.above.mapAll(visitor)));
	}
}
