package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record MapToRange(Noise source, float min, float max) implements Noise {
	public static final Codec<MapToRange> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(MapToRange::source),
		Codec.FLOAT.fieldOf("min").forGetter(MapToRange::min),
		Codec.FLOAT.fieldOf("max").forGetter(MapToRange::max)
	).apply(instance, MapToRange::new));
	
	@Override
	public Codec<MapToRange> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		return NoiseUtil.map(this.source.getValue(x, y, seed), this.min, this.max);
	}
}
