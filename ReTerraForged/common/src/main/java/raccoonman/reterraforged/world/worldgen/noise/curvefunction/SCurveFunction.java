package raccoonman.reterraforged.world.worldgen.noise.curvefunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

record SCurveFunction(float lower, float upper) implements CurveFunction {
	public static final MapCodec<SCurveFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Codec.FLOAT.fieldOf("lower").forGetter(SCurveFunction::lower),
		Codec.FLOAT.fieldOf("upper").forGetter(SCurveFunction::upper)
	).apply(instance, SCurveFunction::new));
	
	public SCurveFunction {
		upper = upper < 0.0F ? Math.max(-lower, upper) : upper;
	}

	@Override
	public float apply(float f) {
        return NoiseUtil.pow(f, this.lower + this.upper * f);
	}

	@Override
	public MapCodec<SCurveFunction> codec() {
		return CODEC;
	}
}
