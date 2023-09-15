package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record Volcano(Noise cone, Noise height, Noise lowlands, float inversionPoint, float blendLower, float blendUpper, float blendRange) implements Noise {
	public static final Codec<Volcano> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("cone").forGetter(Volcano::cone),
		Noise.HOLDER_HELPER_CODEC.fieldOf("height").forGetter(Volcano::height),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lowlands").forGetter(Volcano::lowlands),
		Codec.FLOAT.fieldOf("inversionPoint").forGetter(Volcano::inversionPoint),
		Codec.FLOAT.fieldOf("blendLower").forGetter(Volcano::blendLower),
		Codec.FLOAT.fieldOf("blendUpper").forGetter(Volcano::blendUpper),
		Codec.FLOAT.fieldOf("blendRange").forGetter(Volcano::blendRange)
	).apply(instance, Volcano::new));
	
    @Override
    public float compute(float x, float z, int seed) {
        float value = this.cone.compute(x, z, seed);
        float limit = this.height.compute(x, z, seed);
        float maxHeight = limit * this.inversionPoint;
        if (value > maxHeight) {
            float steepnessModifier = 1.0F;
            float delta = (value - maxHeight) * steepnessModifier;
            float range = limit - maxHeight;
            float alpha = delta / range;
            return maxHeight - maxHeight / 5.0F * alpha;
        } else if (value < this.blendLower) {
            return value + this.lowlands.compute(x, z, seed);
        } else if (value < this.blendUpper) {
            float alpha2 = 1.0F - (value - this.blendLower) / this.blendRange;
            return value + this.lowlands.compute(x, z, seed) * alpha2;
        }
		return value;
    }

	@Override
	public Codec<Volcano> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return new Volcano(this.cone.mapAll(visitor), this.height.mapAll(visitor), this.lowlands.mapAll(visitor), this.inversionPoint, this.blendLower, this.blendUpper, this.blendRange);
	}
}
