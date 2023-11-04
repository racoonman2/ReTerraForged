package raccoonman.reterraforged.common.level.levelgen.noise.climate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;

public record Moisture(Noise source, int power) implements Noise {
    public static final Codec<Moisture> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter(Moisture::source),
    	Codec.INT.fieldOf("power").forGetter(Moisture::power)
    ).apply(instance, Moisture::new));
	
    public Moisture(int seed, int scale, int power) {
        this(Source.simplex(seed, scale, 1).clamp(0.125, 0.875).map(0.0, 1.0), power);
    }
    
    public Moisture {
        source = source.freq(0.5, 1.0);
    }
    
    @Override
    public float compute(float x, float y, int seed) {
        float noise = this.source.compute(x, y, seed);
        if (this.power < 2) {
            return noise;
        }
        noise = (noise - 0.5F) * 2.0F;
        float value = NoiseUtil.pow(noise, this.power);
        value = NoiseUtil.copySign(value, noise);
        return NoiseUtil.map(value, -1.0F, 1.0F);
    }

	@Override
	public Codec<Moisture> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Moisture(this.source.mapAll(visitor), this.power));
	}
}
