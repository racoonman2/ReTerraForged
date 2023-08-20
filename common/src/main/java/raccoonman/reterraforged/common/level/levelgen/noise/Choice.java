package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;

//TODO support different comparisons 
public record Choice(Noise source, Noise threshold, Noise low, Noise high) implements Noise {
	public static final Codec<Choice> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter(Choice::source),
		Noise.DIRECT_CODEC.fieldOf("threshold").forGetter(Choice::threshold),
		Noise.DIRECT_CODEC.fieldOf("low").forGetter(Choice::low),
		Noise.DIRECT_CODEC.fieldOf("high").forGetter(Choice::high)
	).apply(instance, Choice::new));
	
	@Override
	public Codec<Choice> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		return this.source.getValue(x, y, seed) < this.threshold.getValue(x, y, seed) ? this.low.getValue(x, y, seed) : this.high.getValue(x, y, seed);
	}
}
