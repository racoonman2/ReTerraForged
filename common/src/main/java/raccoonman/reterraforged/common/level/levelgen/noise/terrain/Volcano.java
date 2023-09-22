package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record Volcano(Noise cone, Noise height, Noise lowlands, float inversionPoint, float blendLower, float blendUpper) implements Noise {
	public static final Codec<Volcano> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("cone").forGetter(Volcano::cone),
		Noise.HOLDER_HELPER_CODEC.fieldOf("height").forGetter(Volcano::height),
		Noise.HOLDER_HELPER_CODEC.fieldOf("lowlands").forGetter(Volcano::lowlands),
		Codec.FLOAT.fieldOf("inversion_point").forGetter(Volcano::inversionPoint),
		Codec.FLOAT.fieldOf("blend_lower").forGetter(Volcano::blendLower),
		Codec.FLOAT.fieldOf("blend_upper").forGetter(Volcano::blendUpper)
	).apply(instance, Volcano::new));
	
	@Override
	public Codec<Volcano> codec() {
		return CODEC;
	}

	@Override
	public float compute(float x, float y, int seed) {
        float value = this.cone.compute(x, y, seed);
        float limit = this.height.compute(x, y, seed);
        float maxHeight = limit * this.inversionPoint;
        if (value > maxHeight) {
            float steepnessModifier = 1.0F;
            float delta = (value - maxHeight) * steepnessModifier;
            float range = limit - maxHeight;
            float alpha = delta / range;
            value = maxHeight - maxHeight / 5.0F * alpha;
        } else if (value < this.blendLower) {
            value += this.lowlands.compute(x, y, seed);
        } else if (value < this.blendUpper) {
            float alpha2 = 1.0F - (value - this.blendLower) / (this.blendUpper - this.blendLower);
            value += this.lowlands.compute(x, y, seed) * alpha2;
        }
        return value;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return new Volcano(this.cone.mapAll(visitor), this.height.mapAll(visitor), this.lowlands.mapAll(visitor), this.inversionPoint, this.blendLower, this.blendUpper);
	}
}
