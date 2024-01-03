package raccoonman.reterraforged.world.worldgen.noise.domain;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.registries.RTFBuiltInRegistries;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public interface Domain {
    public static final Codec<Domain> CODEC = RTFBuiltInRegistries.DOMAIN_TYPE.byNameCodec().dispatch(Domain::codec, Function.identity());
	
    float getOffsetX(float x, float z, int seed);
    
    float getOffsetZ(float x, float z, int seed);
    
    Domain mapAll(Noise.Visitor visitor);
    
    Codec<? extends Domain> codec();

    default float getX(float x, float z, int seed) {
        return x + this.getOffsetX(x, z, seed);
    }
    
    default float getZ(float x, float z, int seed) {
        return z + this.getOffsetZ(x, z, seed);
    }
}
