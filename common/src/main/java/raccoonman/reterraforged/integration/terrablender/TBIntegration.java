package raccoonman.reterraforged.integration.terrablender;

import java.util.List;

import com.google.common.collect.ImmutableList;

import raccoonman.reterraforged.platform.ModLoaderUtil;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.SurfaceRuleManager.RuleCategory;
import terrablender.core.TerraBlender;

public class TBIntegration {
	public static final List<String> MIXINS = ImmutableList.of(mixinClass("terrablender.MixinClimateSampler"), mixinClass("terrablender.MixinNoiseChunk"), mixinClass("terrablender.MixinParameterList"), mixinClass("terrablender.MixinTargetPoint"));
	
	public static void bootstrap() {
		TBSurfaceRules.bootstrap();
	}
	
	public static boolean isEnabled() {
		return ModLoaderUtil.isLoaded(TerraBlender.MOD_ID);
	}
	
	public static void initSurface() {
		SurfaceRuleManager.setDefaultSurfaceRules(RuleCategory.OVERWORLD, null);
	}
	
	public static boolean isTBMixin(String mixinClassName) {
		return MIXINS.contains(mixinClassName);
	}
	
	private static String mixinClass(String className) {
		return "raccoonman.reterraforged.mixin." + className;
	}
}
