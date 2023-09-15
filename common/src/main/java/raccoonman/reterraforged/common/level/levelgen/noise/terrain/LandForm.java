package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

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
//		System.out.println(this.base.compute(x, y, seed) + this.terrain.compute(x, y, seed));
		return this.base.compute(x, y, seed) + this.terrain.compute(x, y, seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new LandForm(this.base.mapAll(visitor), this.terrain.mapAll(visitor)));
	}
}
