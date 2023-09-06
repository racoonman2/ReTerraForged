package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public record Floor(Noise source) implements Noise {
	public static final Codec<Floor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Floor::source)
	).apply(instance, Floor::new));
	
	@Override
	public Codec<Floor> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return NoiseUtil.floor(this.source.compute(x, y, seed));
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Floor(this.source.mapAll(visitor)));
	}
}
