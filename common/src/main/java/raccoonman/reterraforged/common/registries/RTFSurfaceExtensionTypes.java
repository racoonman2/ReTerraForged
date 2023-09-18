package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.surface.extension.ErosionExtensionSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.ExtensionRuleSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.geology.StrataExtensionSource;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFSurfaceExtensionTypes {

	public static void bootstrap() {
		register("erosion", ErosionExtensionSource.CODEC);
		register("strata", StrataExtensionSource.CODEC);
	}
	
	private static void register(String name, Codec<? extends ExtensionRuleSource.ExtensionSource> value) {
		RegistryUtil.register(RTFBuiltInRegistries.SURFACE_EXTENSION_TYPE, name, value);
	}
}
