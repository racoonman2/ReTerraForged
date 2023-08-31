package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public class Freq extends Modifier {
	public static final Codec<Freq> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("x").forGetter((m) -> m.x),
		Noise.HOLDER_HELPER_CODEC.fieldOf("y").forGetter((m) -> m.y)
	).apply(instance, Freq::new));
	
    private final Noise x;
    private final Noise y;

    public Freq(Noise source, Noise x, Noise y) {
        super(source);
        this.x = x;
        this.y = y;
    }

    @Override
    public float getValue(float x, float y, int seed) {
    	float fx = this.x.getValue(x, y, seed);
        float fy = this.y.getValue(x, y, seed);
        return this.source.getValue(x * fx, y * fy, seed);
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Freq freq = (Freq) o;

        if (!x.equals(freq.x)) return false;
        return y.equals(freq.y);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + x.hashCode();
        result = 31 * result + y.hashCode();
        return result;
    }
    
    @Override
	public Codec<Freq> codec() {
		return CODEC;
	}
}
