package raccoonman.reterraforged.world.worldgen.noise.module;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import raccoonman.reterraforged.registries.RTFRegistries;

public interface Noise {
    public static final Codec<Noise> DIRECT_CODEC = Noises.DIRECT_CODEC;
    public static final Codec<Holder<Noise>> CODEC = RegistryFileCodec.create(RTFRegistries.NOISE, DIRECT_CODEC);
    public static final Codec<Noise> HOLDER_HELPER_CODEC = CODEC.xmap(Noises.HolderHolder::new, noise -> {
        if (noise instanceof Noises.HolderHolder holderHolder) {
            return holderHolder.holder();
        }
        return new Holder.Direct<>(noise);
    });
    
	float compute(float x, float z, int seed);
	
	float minValue();
	
	float maxValue();
	
	Noise mapAll(Visitor visitor);
	
	Codec<? extends Noise> codec();
	
	public interface Visitor {
		Noise apply(Noise input);
	}
}
