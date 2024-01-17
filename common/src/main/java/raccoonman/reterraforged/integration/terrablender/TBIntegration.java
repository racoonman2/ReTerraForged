package raccoonman.reterraforged.integration.terrablender;

import java.util.List;

import com.google.common.collect.ImmutableList;

import raccoonman.reterraforged.platform.ModLoaderUtil;

//terrablender compat
public class TBIntegration {
	public static final List<String> COMPAT_MIXINS = ImmutableList.of(mixinClass("terrablender.MixinClimateSampler"), mixinClass("terrablender.MixinNoiseChunk"), mixinClass("terrablender.MixinParameterList"), mixinClass("terrablender.MixinTargetPoint"));
	
	public static boolean isEnabled() {
		return ModLoaderUtil.isLoaded("terrablender");
	}
	
	public static boolean isTBMixin(String mixinClassName) {
		return COMPAT_MIXINS.contains(mixinClassName);
	}
	
	private static String mixinClass(String className) {
		return "raccoonman.reterraforged.mixin." + className;
	}
}
