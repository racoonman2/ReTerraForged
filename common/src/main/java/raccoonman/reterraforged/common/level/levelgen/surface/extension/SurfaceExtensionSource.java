package raccoonman.reterraforged.common.level.levelgen.surface.extension;

import java.util.function.Function;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.registries.RTFBuiltInRegistries;

@Deprecated
public interface SurfaceExtensionSource {
	public static final Codec<SurfaceExtensionSource> CODEC = RTFBuiltInRegistries.SURFACE_EXTENSION_SOURCE.byNameCodec().dispatch(SurfaceExtensionSource::codec, Function.identity());
	
	SurfaceExtension apply(Context ctx);
	
	Codec<? extends SurfaceExtensionSource> codec();
}
