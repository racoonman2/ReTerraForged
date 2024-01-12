package raccoonman.reterraforged.world.worldgen.noise.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record TerraceFunction(float inputRange, float ramp, float cliff, float rampHeight, float blendRange, Step[] steps) implements CurveFunction {
	public static final Codec<TerraceFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.fieldOf("input_range").forGetter(TerraceFunction::inputRange),
		Codec.FLOAT.fieldOf("ramp").forGetter(TerraceFunction::ramp),
		Codec.FLOAT.fieldOf("cliff").forGetter(TerraceFunction::cliff),
		Codec.FLOAT.fieldOf("ramp_height").forGetter(TerraceFunction::rampHeight),
		Codec.FLOAT.fieldOf("blend_range").forGetter(TerraceFunction::blendRange),
		Codec.INT.fieldOf("steps").forGetter((terrace) -> terrace.steps().length)
	).apply(instance, TerraceFunction::new));
	
	public TerraceFunction(float inputRange, float ramp, float cliff, float rampHeight, float blendRange, int steps) {
		this(inputRange, ramp, cliff, rampHeight, blendRange, createSteps(inputRange, blendRange, steps));
	}
	
	@Override
	public float apply(float f) {
        float input = NoiseUtil.clamp(f, 0.0F, 0.999999F);
        int index = NoiseUtil.floor(input * this.steps.length);
        Step step = this.steps[index];
        if (index == this.steps.length - 1) {
            return step.value;
        }
        if (input < step.lowerBound) {
            return step.value;
        }
        if (input > step.upperBound) {
            Step next = this.steps[index + 1];
            return next.value;
        }
        float ramp = 1.0F - this.ramp * 0.5F;
        float cliff = 1.0F - this.cliff * 0.5F;
        float alpha = (input - step.lowerBound) / (step.upperBound - step.lowerBound);
        float value = step.value;
        if (alpha > ramp) {
            Step next2 = this.steps[index + 1];
            float rampSize = 1.0F - ramp;
            float rampAlpha = (alpha - ramp) / rampSize;
            float rampHeight = this.rampHeight;
            value += (next2.value - value) * rampAlpha * rampHeight;
        }
        if (alpha > cliff) {
            Step next2 = this.steps[index + 1];
            float cliffAlpha = (alpha - cliff) / (1.0F - cliff);
            value = NoiseUtil.lerp(value, next2.value, cliffAlpha);
        }
        return value;
	}
	
	@Override
	public Codec<TerraceFunction> codec() {
		return CODEC;
	}
	
	private static Step[] createSteps(float inputRange, float blendRange, int steps) {
        float spacing = inputRange / (steps - 1);
        Step[] array = new Step[steps];
        for (int i = 0; i < steps; ++i) {
            float value = i * spacing;
            array[i] = Step.create(value, spacing, blendRange);
        }
        return array;
	}
	
	private record Step(float value, float lowerBound, float upperBound) {
	
		public static Step create(float value, float distance, float blendRange) {
			float blend = distance * blendRange;
			float bound = (distance - blend) / 2.0F;
            return new Step(value, value - bound, value + bound);
		}
	}
}
