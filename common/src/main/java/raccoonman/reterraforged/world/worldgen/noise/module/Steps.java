package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.function.CurveFunction;

public record Steps(Noise input, Noise steps, Noise slopeMin, Noise slopeMax, CurveFunction slopeCurve) implements Noise {
	public static final Codec<Steps> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Steps::input),
		Noise.HOLDER_HELPER_CODEC.fieldOf("steps").forGetter(Steps::steps),
		Noise.HOLDER_HELPER_CODEC.fieldOf("slope_min").forGetter(Steps::slopeMin),
		Noise.HOLDER_HELPER_CODEC.fieldOf("slope_max").forGetter(Steps::slopeMax),
		CurveFunction.CODEC.fieldOf("slope_curve").forGetter(Steps::slopeCurve)
	).apply(instance, Steps::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		float noiseValue = this.input.compute(x, z, seed);
        float min = this.slopeMin.compute(x, z, seed);
        float max = this.slopeMax.compute(x, z, seed);
        float stepCount = this.steps.compute(x, z, seed);
        float range = max - min;
        if (range <= 0.0F) {
            return (int) (noiseValue * stepCount) / stepCount;
        }
        noiseValue = 1.0F - noiseValue;
        float value = (int) (noiseValue * stepCount) / stepCount;
        float delta = noiseValue - value;
        float alpha = NoiseUtil.map(delta * stepCount, min, max, range);
        return 1.0F - NoiseUtil.lerp(value, noiseValue, this.slopeCurve.apply(alpha));
	}

	@Override
	public float minValue() {
		return this.input.minValue();
	}

	@Override
	public float maxValue() {
		return this.input.maxValue();
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Steps(this.input.mapAll(visitor), this.steps.mapAll(visitor), this.slopeMin.mapAll(visitor), this.slopeMax.mapAll(visitor), this.slopeCurve));
	}

	@Override
	public Codec<Steps> codec() {
		return CODEC;
	}
}
