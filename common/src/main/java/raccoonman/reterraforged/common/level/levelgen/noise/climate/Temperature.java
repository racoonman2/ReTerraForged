package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public record Temperature(Noise frequency, int power) implements Noise {
    public static final Codec<Temperature> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Noise.HOLDER_HELPER_CODEC.fieldOf("frequency").forGetter(Temperature::frequency),
    	Codec.INT.fieldOf("power").forGetter(Temperature::power)
    ).apply(instance, Temperature::new));
	
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Temperature(this.frequency.mapAll(visitor), this.power));
	}
    
    @Override
    public float compute(float x, float y, int seed) {
        y *= this.frequency.compute(x, y, seed);
        float sin = NoiseUtil.sin(y);
        sin = NoiseUtil.clamp(sin, -1.0F, 1.0F);
        float value = NoiseUtil.pow(sin, this.power);
        value = NoiseUtil.copySign(value, sin);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }
    
	@Override
	public Codec<Temperature> codec() {
		return CODEC;
	}
}
