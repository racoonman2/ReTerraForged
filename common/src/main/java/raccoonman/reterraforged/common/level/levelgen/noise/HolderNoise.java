package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;

//TODO don't hardcode min/max
public record HolderNoise(Holder<Noise> holder) implements Noise {
	public static final Codec<HolderNoise> CODEC = Noise.CODEC.xmap(HolderNoise::new, HolderNoise::holder);

	@Override
	public Codec<HolderNoise> codec() {
		return CODEC;
	}
	
	@Override
    public float minValue() {
        return this.holder.isBound() ? this.holder.value().minValue() : Float.NEGATIVE_INFINITY;
    }

    @Override
    public float maxValue() {
        return this.holder.isBound() ? this.holder.value().maxValue() : Float.POSITIVE_INFINITY;
    }

	@Override
	public float compute(float x, float y, int seed) {
		return this.holder.value().compute(x, y, seed);
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new HolderNoise(Holder.direct(this.holder.value().mapAll(visitor))));
	}
}
