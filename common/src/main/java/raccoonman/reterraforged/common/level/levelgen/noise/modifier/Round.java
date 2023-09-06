package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record Round(Noise source) implements Noise {
	public static final Codec<Round> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Round::source)
	).apply(instance, Round::new));
	
	@Override
	public Codec<Round> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return NoiseUtil.round(this.source.compute(x, y, seed));
	}
	
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Round(this.source.mapAll(visitor)));
	}
}
