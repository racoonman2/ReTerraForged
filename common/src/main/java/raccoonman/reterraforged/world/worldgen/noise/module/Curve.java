package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.function.CurveFunction;

record Curve(Noise input, CurveFunction curveFunction) implements Noise {
	public static final Codec<Curve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(Curve::input),
		CurveFunction.CODEC.fieldOf("curve_function").forGetter(Curve::curveFunction)
	).apply(instance, Curve::new));
	
	@Override
	public float compute(float x, float z, int seed) {
		return this.curveFunction.apply(this.input.compute(x, z, seed));
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
		return visitor.apply(new Curve(this.input.mapAll(visitor), this.curveFunction));
	}

	@Override
	public Codec<Curve> codec() {
		return CODEC;
	}
}
