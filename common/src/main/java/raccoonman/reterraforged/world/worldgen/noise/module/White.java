package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public record White(float frequency) implements Noise {
	public static final Codec<White> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(White::frequency)
	).apply(instance, White::new));

	@Override
	public float compute(float x, float z, int seed) {
        x *= this.frequency;
        z *= this.frequency;
        float value = sample(x, z, seed);
        return Math.abs(value);
	}

	@Override
	public float minValue() {
		return 0.0F;
	}

	@Override
	public float maxValue() {
		return 1.0F;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}

	@Override
	public Codec<White> codec() {
		return CODEC;
	}
	
	public static float sample(float x, float z, int seed) {
        int xi = NoiseUtil.round(x);
        int zi = NoiseUtil.round(z);
        return NoiseUtil.valCoord2D(seed, xi, zi);
    }
	
	public static float sample(float x, float z, int seed, int offset) {
		return sample(x, z, NoiseUtil.hash(seed, offset));
	}
}
