package raccoonman.reterraforged.common.level.levelgen.noise.curve;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class MidPointCurve implements CurveFunction {
	public static final Codec<MidPointCurve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("mid", 0.5F).forGetter((c) -> c.mid),
		Codec.FLOAT.optionalFieldOf("steepness", 4.0F).forGetter((c) -> c.steepness)		
	).apply(instance, MidPointCurve::new));
	
    private final float mid;
    private final float steepness;

    public MidPointCurve(float mid, float steepness) {
        this.mid = mid;
        this.steepness = steepness;
    }

    @Override
    public float apply(float value) {
        return NoiseUtil.curve(value, mid, steepness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MidPointCurve that = (MidPointCurve) o;

        if (Float.compare(that.mid, mid) != 0) return false;
        return Float.compare(that.steepness, steepness) == 0;
    }

    @Override
    public int hashCode() {
        int result = (mid != +0.0f ? Float.floatToIntBits(mid) : 0);
        result = 31 * result + (steepness != +0.0f ? Float.floatToIntBits(steepness) : 0);
        return result;
    }
    
    @Override
	public Codec<MidPointCurve> codec() {
		return CODEC;
	}
}
