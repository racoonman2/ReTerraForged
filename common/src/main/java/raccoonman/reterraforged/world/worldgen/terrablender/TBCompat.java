package raccoonman.reterraforged.world.worldgen.terrablender;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.platform.ModLoaderUtil;

public class TBCompat {
	public static final List<String> TERRABLENDER_COMPAT_MIXINS = ImmutableList.of(mixinClass("terrablender.MixinClimateSampler"), mixinClass("terrablender.MixinNoiseChunk"), mixinClass("terrablender.MixinParameterList"), mixinClass("terrablender.MixinTargetPoint"));
	
	public static boolean isEnabled() {
		return ModLoaderUtil.isLoaded("terrablender");
	}
	
	public static boolean isTBMixin(String mixinClassName) {
		return TERRABLENDER_COMPAT_MIXINS.contains(mixinClassName);
	}
	
	// has to be deferred to a function or else we get bootstrapping issues from MixinPlugin
	public static ResourceKey<DensityFunction> uniquenessKey() {
		return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation("terrablender:uniqueness"));
	}
	
	private static String mixinClass(String className) {
		return "raccoonman.reterraforged.mixin." + className;
	}
}
