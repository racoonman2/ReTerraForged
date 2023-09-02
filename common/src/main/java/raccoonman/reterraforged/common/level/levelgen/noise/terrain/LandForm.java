package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record LandForm(Noise base, Noise terrain) implements Noise {
	public static final Codec<LandForm> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("base").forGetter(LandForm::base),
		Noise.HOLDER_HELPER_CODEC.fieldOf("terrain").forGetter(LandForm::terrain)
	).apply(instance, LandForm::new));
	
	@Override
	public Codec<LandForm> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
		return NoiseUtil.clamp(this.base.compute(x, y, seed) + this.terrain.compute(x, y, seed), 0.0F, 1.0F);
	}
}
