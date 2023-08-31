package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class Terrace extends Modifier {
	public static final Codec<Terrace> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("ramp").forGetter((m) -> m.ramp),
		Noise.HOLDER_HELPER_CODEC.fieldOf("cliff").forGetter((m) -> m.cliff),
		Noise.HOLDER_HELPER_CODEC.fieldOf("ramp_height").forGetter((m) -> m.rampHeight),
		Codec.INT.optionalFieldOf("steps", 1).forGetter((m) -> m.steps.length),
		Codec.FLOAT.optionalFieldOf("blend", 1.0F).forGetter((m) -> m.blend)
	).apply(instance, Terrace::new));
	
    private static final float MIN_NOISE_VALUE = 0.0F;
    private static final float MAX_NOISE_VALUE = 0.999999F;
    private final float blend;
    private final float length;
    private final int maxIndex;
    private final Step[] steps;
    private final Noise ramp;
    private final Noise cliff;
    private final Noise rampHeight;

    public Terrace(Noise source, Noise ramp, Noise cliff, Noise rampHeight, int steps, float blendRange) {
        super(source);
        this.blend = blendRange;
        this.maxIndex = steps - 1;
        this.steps = new Step[steps];
        this.length = steps * MAX_NOISE_VALUE;
        this.ramp = ramp;
        this.cliff = cliff;
        this.rampHeight = rampHeight;
        float min = source.minValue();
        float max = source.maxValue();
        float range = max - min;
        float spacing = range / (steps - 1);

        for (int i = 0; i < steps; i++) {
            float value = i * spacing;
            this.steps[i] = new Step(value, spacing, blendRange);
        }
    }

    @Override
    public float compute(float x, float y, int seed) {
        float value = source.compute(x, y, seed);
        value = NoiseUtil.clamp(value, MIN_NOISE_VALUE, MAX_NOISE_VALUE);
        return modify(x, y, value, seed);
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        int index = NoiseUtil.floor(noiseValue * steps.length);
        Step step = steps[index];

        if (index == maxIndex) {
            return step.value;
        }

        if (noiseValue < step.lowerBound) {
            return step.value;
        }

        if (noiseValue > step.upperBound) {
            Step next = steps[index + 1];
            return next.value;
        }

        float ramp = 1F - (this.ramp.compute(x, y, seed) * 0.5F);
        float cliff = 1F - (this.cliff.compute(x, y, seed) * 0.5F);
        float alpha = (noiseValue - step.lowerBound) / (step.upperBound - step.lowerBound);

        float value = step.value;
        if (alpha > ramp) {
            Step next = steps[index + 1];
            float rampSize = 1 - ramp;
            float rampAlpha = (alpha - ramp) / rampSize;
            float rampHeight = this.rampHeight.compute(x, y, seed);
            value += (next.value - value) * rampAlpha * rampHeight;
        }

        if (alpha > cliff) {
            Step next = steps[index + 1];
            float cliffAlpha = (alpha - cliff) / (1 - cliff);
            value = NoiseUtil.lerp(value, next.value, cliffAlpha);
        }

        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Terrace terrace = (Terrace) o;

        if (Float.compare(terrace.blend, blend) != 0) return false;
        if (Float.compare(terrace.length, length) != 0) return false;
        if (maxIndex != terrace.maxIndex) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(steps, terrace.steps)) return false;
        if (!ramp.equals(terrace.ramp)) return false;
        if (!cliff.equals(terrace.cliff)) return false;
        return rampHeight.equals(terrace.rampHeight);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (blend != +0.0f ? Float.floatToIntBits(blend) : 0);
        result = 31 * result + (length != +0.0f ? Float.floatToIntBits(length) : 0);
        result = 31 * result + maxIndex;
        result = 31 * result + Arrays.hashCode(steps);
        result = 31 * result + ramp.hashCode();
        result = 31 * result + cliff.hashCode();
        result = 31 * result + rampHeight.hashCode();
        return result;
    }
    
    @Override
	public Codec<Terrace> codec() {
		return CODEC;
	}

    private static class Step {

        private final float value;
        private final float lowerBound;
        private final float upperBound;

        private Step(float value, float distance, float blendRange) {
            this.value = value;
            float blend = distance * blendRange;
            float bound = (distance - blend) / 2F;
            lowerBound = value - bound;
            upperBound = value + bound;
        }
    }
}
