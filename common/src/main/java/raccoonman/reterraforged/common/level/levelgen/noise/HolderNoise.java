package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.common.noise.Noise;

public record HolderNoise(Holder<Noise> holder) implements Noise {
	public static final Codec<HolderNoise> CODEC = Noise.CODEC.xmap(HolderNoise::new, HolderNoise::holder);
	
    @Override
    public float minValue() {
    	return this.holder.value().minValue();
    }
    
    @Override
    public float maxValue() {
    	return this.holder.value().maxValue();
    }
	
	@Override
	public Codec<HolderNoise> codec() {
		return CODEC;
	}

	@Override
	public float getValue(float x, float y, int seed) {
		return this.holder.value().getValue(x, y, seed);
	}
}
