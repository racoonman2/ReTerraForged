package raccoonman.reterraforged.mixin.plugin;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.common.collect.ImmutableList;

import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.compat.terrablender.TBCompat;
import raccoonman.reterraforged.compat.worldpreview.WPCompat;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final String MIXIN_PACKAGE_PREFIX = "raccoonman.reterraforged.mixin.";
	public static final List<String> TB_MIXINS = ImmutableList.of(mixinClassName("terrablender.MixinClimateSampler"), mixinClassName("terrablender.MixinNoiseChunk"), mixinClassName("terrablender.MixinParameterList"), mixinClassName("terrablender.MixinTargetPoint"));
	public static final List<String> WP_MIXINS = ImmutableList.of(mixinClassName("worldpreview.SampleUtilsMixin"));
	
	@Override
	public void onLoad(String mixinPackage) {
		log(TBCompat.isEnabled(), "TerraBlender");
		log(WPCompat.isEnabled(), "World Preview");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return TB_MIXINS.contains(mixinClassName) ? TBCompat.isEnabled() : 
			   WP_MIXINS.contains(mixinClassName) ? WPCompat.isEnabled() :
			   true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return Stream.concat(TB_MIXINS.stream(), WP_MIXINS.stream()).map((str) -> {
			return str.replace(MIXIN_PACKAGE_PREFIX, "");
		}).toList();
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	private static String mixinClassName(String className) {
		return MIXIN_PACKAGE_PREFIX + className;
	}
	
	private static void log(boolean isModLoaded, String modName) {
		if(isModLoaded) {
			RTFCommon.LOGGER.info("Enabling {} compat", modName);
		} else {
			RTFCommon.LOGGER.info("Disabling {} compat", modName);
		}
	}
}
