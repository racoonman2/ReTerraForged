package raccoonman.reterraforged.world.worldgen.noise.warp;

import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryFileCodec;
import raccoonman.reterraforged.registry.RTFBuiltInRegistries;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.noise.Noise;

//TODO these should work with DensityFunction rather than Noise
public interface Warp {
    public static final Codec<Warp> DIRECT_CODEC = RTFBuiltInRegistries.WARP_TYPE.byNameCodec().dispatch(Warp::codec, Function.identity());
    public static final Codec<Holder<Warp>> CODEC = RegistryFileCodec.create(RTFRegistries.WARP, DIRECT_CODEC);
    public static final Codec<Warp> HOLDER_HELPER_CODEC = CODEC.xmap(Warps.HolderHolder::new, noise -> {
        if (noise instanceof Warps.HolderHolder holderHolder) {
            return holderHolder.holder();
        }
        return new Holder.Direct<>(noise);
    });

    default float getX(float x, float z, int seed) {
        return x + this.getOffsetX(x, z, seed);
    }
    
    default float getZ(float x, float z, int seed) {
        return z + this.getOffsetZ(x, z, seed);
    }
    
    float getOffsetX(float x, float z, int seed);

    float getOffsetZ(float x, float z, int seed);
    
    Warp mapAll(Noise.Visitor visitor);
    
    MapCodec<? extends Warp> codec();
}
