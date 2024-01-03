package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record Map(Noise alpha, Noise from, Noise to) implements Noise {
	public static final Codec<Map> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(Map::alpha),
		Noise.HOLDER_HELPER_CODEC.fieldOf("from").forGetter(Map::from),
		Noise.HOLDER_HELPER_CODEC.fieldOf("to").forGetter(Map::to)
	).apply(instance, Map::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float alphaMin = this.alpha.minValue();
		float alphaMax = this.alpha.maxValue();
		
        float value = this.alpha.compute(x, z, seed);
		float alpha = (value - alphaMin) / (alphaMax - alphaMin);
        float min = this.from.compute(x, z, seed);
        float max = this.to.compute(x, z, seed);
        return min + alpha * (max - min);
	}

	@Override
	public float minValue() {
		return this.from.minValue();
	}

	@Override
	public float maxValue() {
		return this.to.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Map(this.alpha.mapAll(visitor), this.from.mapAll(visitor), this.to.mapAll(visitor)));
	}

	@Override
	public Codec<Map> codec() {
		return CODEC;
	}
}
