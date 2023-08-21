package raccoonman.reterraforged.common.level.levelgen.noise;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import raccoonman.reterraforged.common.noise.Noise;

//TODO don't hardcode min/max
public record HolderNoise(Holder<Noise> holder) implements Noise {
	public static final Codec<HolderNoise> CODEC = Noise.CODEC.xmap(HolderNoise::new, HolderNoise::holder);
	
    @Override
    public float minValue() {
    	return 0.0F;
//    	return this.holder.value().minValue();
    }
    
    @Override
    public float maxValue() {
    	return 1.0F;
//    	return this.holder.value().maxValue();
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
