package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import raccoonman.reterraforged.common.level.levelgen.surface.filter.ErosionFilterSource;
import raccoonman.reterraforged.common.level.levelgen.surface.filter.FilterSurfaceRuleSource;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFSurfaceFilterTypes {

	public static void bootstrap() {
		register("erosion", ErosionFilterSource.CODEC);
	}
	
	private static void register(String name, Codec<? extends FilterSurfaceRuleSource.FilterSource> value) {
		RegistryUtil.register(RTFBuiltInRegistries.SURFACE_FILTER_TYPE, name, value);
	}
}
