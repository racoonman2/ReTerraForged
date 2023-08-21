package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public record Floor(Noise source) implements Noise {
	public static final Codec<Floor> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.DIRECT_CODEC.fieldOf("source").forGetter(Floor::source)
	).apply(instance, Floor::new));
	
	@Override
	public Codec<Floor> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		return NoiseUtil.floor(this.source.getValue(x, y, seed));
	}
}
