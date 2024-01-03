package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record Sin(float frequency, Noise alpha) implements Noise {
	public static final Codec<Sin> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("frequency").forGetter(Sin::frequency),
		Noise.HOLDER_HELPER_CODEC.fieldOf("alpha").forGetter(Sin::alpha)
	).apply(instance, Sin::new));

	@Override
	public float compute(float x, float z, int seed) {
        float a = this.alpha.compute(x, z, seed);
        x *= this.frequency;
        z *= this.frequency;
        float noise;
        if (a == 0.0F) {
            noise = NoiseUtil.sin(x);
        } else if (a == 1.0F) {
            noise = NoiseUtil.sin(z);
        } else {
            float sx = NoiseUtil.sin(x);
            float sy = NoiseUtil.sin(z);
            noise = NoiseUtil.lerp(sx, sy, a);
        }
        return NoiseUtil.map(noise, -1.0F, 1.0F, 2.0F);
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
		return visitor.apply(new Sin(this.frequency, this.alpha.mapAll(visitor)));
	}

	@Override
	public Codec<Sin> codec() {
		return CODEC;
	}
}
