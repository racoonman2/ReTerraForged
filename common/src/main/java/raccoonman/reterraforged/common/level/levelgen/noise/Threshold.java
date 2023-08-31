package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

//TODO support more comparisons
public record Threshold(Noise source, Noise threshold, Noise low, Noise high) implements Noise {
	public static final Codec<Threshold> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Threshold::source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("threshold").forGetter(Threshold::threshold),
		Noise.HOLDER_HELPER_CODEC.fieldOf("low").forGetter(Threshold::low),
		Noise.HOLDER_HELPER_CODEC.fieldOf("high").forGetter(Threshold::high)
	).apply(instance, Threshold::new));

	@Override
	public Codec<Threshold> codec() {
		return CODEC;
	}

	@Override
	public float maxValue() {
		return this.high.maxValue();
	}
	
	@Override
	public float minValue() {
		return this.low.minValue();
	}
	
	@Override
	public float compute(float x, float y, int seed) {
		return this.source.compute(x, y, seed) < this.threshold.compute(x, y, seed) ? this.low.compute(x, y, seed) : this.high.compute(x, y, seed);
	}
}
