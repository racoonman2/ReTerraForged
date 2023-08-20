package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;

//TODO support different comparisons 
public record Choice(Noise noise, float threshold, float low, float high) implements Noise {
	public static final Codec<Choice> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("noise").forGetter(Choice::noise),
		Codec.FLOAT.fieldOf("threshold").forGetter(Choice::threshold),
		Codec.FLOAT.fieldOf("low").forGetter(Choice::low),
		Codec.FLOAT.fieldOf("high").forGetter(Choice::high)
	).apply(instance, Choice::new));
	
	@Override
	public Codec<Choice> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		return this.noise.getValue(x, y, seed) < this.threshold ? this.low : this.high;
	}
}
