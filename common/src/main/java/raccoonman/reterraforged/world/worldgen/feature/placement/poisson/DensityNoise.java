package raccoonman.reterraforged.world.worldgen.feature.placement.poisson;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public class DensityNoise implements Noise {
    private BiomeVariance biome;
    private Noise variance;

    public DensityNoise(BiomeVariance biome, Noise variance) {
        this.biome = biome;
        this.variance = variance;
    }

    @Override
    public float compute(float x, float y, int seed) {
        float value1 = this.biome.compute(x, y, 0);
        if (value1 > 2F) {
            return value1;
        }

        float value2 = this.variance.compute(x, y, 0);
        if (value1 > 1F) {
            return NoiseUtil.lerp(value2, value1, (value1 - 0.25F) / 0.25F);
        }

        return value2;
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
	public Codec<DensityNoise> codec() {
		throw new UnsupportedOperationException();
	}
}
