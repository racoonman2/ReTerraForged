package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.surface.extension.GeoSurfaceExtensionSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.SurfaceExtensionSource;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFSurfaceExtensionSourceTypes {

	public static void bootstrap() {
		register("geo", GeoSurfaceExtensionSource.CODEC);
	}
	
	private static void register(String name, Codec<? extends SurfaceExtensionSource> value) {
		RegistryUtil.register(RTFBuiltInRegistries.SURFACE_EXTENSION_SOURCE, name, value);
	}
}
