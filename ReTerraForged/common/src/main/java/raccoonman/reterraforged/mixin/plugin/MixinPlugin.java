package raccoonman.reterraforged.mixin.plugin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import com.google.common.collect.ImmutableList;

import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.world.worldgen.compat.CompatUtil;

public class MixinPlugin implements IMixinConfigPlugin {
	private static final List<String> TB_MIXINS = ImmutableList.of(
		"terrablender.MixinClimateSampler",
		"terrablender.MixinNoiseChunk",
		"terrablender.MixinParameterList",
		"terrablender.MixinTargetPoint",
		"terrablender.MixinChunkMap",
		"terrablender.MixinMultiNoiseBiomeSource"
	);
	private static final List<String> WP_MIXINS = ImmutableList.of(
		"worldpreview.MixinSampleUtils"
	);
	
	@Override
	public void onLoad(String mixinPackage) {
		log(CompatUtil.HAS_TB, "TerraBlender");
		log(CompatUtil.HAS_WP, "WorldPreview");
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		ImmutableList.Builder<String> builder = ImmutableList.builder();
		if(CompatUtil.HAS_TB) {
			builder.addAll(TB_MIXINS);
		}
		if(CompatUtil.HAS_WP) {
			builder.addAll(WP_MIXINS);
		}
		return builder.build();
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
		
	private static void log(boolean isModLoaded, String modName) {
		if(isModLoaded) {
			RTFCommon.LOGGER.info("Enabling {} compat", modName);
		} else {
			RTFCommon.LOGGER.info("Disabling {} compat", modName);
		}
	}
}
