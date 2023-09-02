package raccoonman.reterraforged.common.level.levelgen.noise.settings;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record WorldHeightMarker() implements Noise {
	public static final Codec<WorldHeightMarker> CODEC = Codec.unit(WorldHeightMarker::new);
	
	@Override
	public Codec<WorldHeightMarker> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return -1;
	}
	
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
}
