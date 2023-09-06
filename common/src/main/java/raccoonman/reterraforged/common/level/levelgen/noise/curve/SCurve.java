package raccoonman.reterraforged.common.level.levelgen.noise.curve;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

public class SCurve implements CurveFunction {
	public static final Codec<SCurve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("lower", 0.0F).forGetter((c) -> c.lower),
		Codec.FLOAT.optionalFieldOf("upper", 1.0F).forGetter((c) -> c.upper)		
	).apply(instance, SCurve::new));

    private final float lower;
    private final float upper;

    public SCurve(float lower, float upper) {
        this.lower = lower;
        this.upper = upper < 0 ? Math.max(-lower, upper) : upper;
    }

    @Override
    public float apply(float value) {
        return NoiseUtil.pow(value, lower + (upper * value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SCurve sCurve = (SCurve) o;

        if (Float.compare(sCurve.lower, lower) != 0) return false;
        return Float.compare(sCurve.upper, upper) == 0;
    }

    @Override
    public int hashCode() {
        int result = (lower != +0.0f ? Float.floatToIntBits(lower) : 0);
        result = 31 * result + (upper != +0.0f ? Float.floatToIntBits(upper) : 0);
        return result;
    }
    
    @Override
	public Codec<SCurve> codec() {
		return CODEC;
	}
}
