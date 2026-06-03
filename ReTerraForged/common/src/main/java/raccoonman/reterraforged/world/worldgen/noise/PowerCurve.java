package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

record PowerCurve(Noise input, float power, float mid, float min, float max) implements Noise {
	public static final MapCodec<PowerCurve> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("input").forGetter(PowerCurve::input),
		Codec.FLOAT.fieldOf("power").forGetter(PowerCurve::power)
	).apply(instance, PowerCurve::new));

	public PowerCurve(Noise input, float power) {
		this(input, power, input.minValue() + (input.maxValue() - input.minValue()) / 2.0F);
	}
	
	private PowerCurve(Noise input, float power, float mid) {
		this(input, power, mid, mid - NoiseUtil.pow(mid - input.minValue(), power), mid + NoiseUtil.pow(input.maxValue() - mid, power));
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
	public float compute(float x, float z, int seed) {
		float input = this.input.compute(x, z, seed);
        if (input >= this.mid) {
            float part = input - this.mid;
            input = this.mid + NoiseUtil.pow(part, this.power);
        } else {
            float part = this.mid - input;
            input = this.mid - NoiseUtil.pow(part, this.power);
        }
        return NoiseUtil.map(input, this.min, this.max, this.max - this.min);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return new PowerCurve(this.input.mapAll(visitor), this.power);
	}

	@Override
	public MapCodec<PowerCurve> codec() {
		return CODEC;
	}
}
