package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

//TODO make this work with decimals
public record Hash() implements Noise {
	public static final Codec<Hash> CODEC = Codec.unit(Hash::new);
	
	@Override
	public Codec<Hash> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return NoiseUtil.rand(NoiseUtil.hash2D(seed, (int) Math.floor(x), (int) Math.floor(y)));
	}
}
