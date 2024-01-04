package raccoonman.reterraforged.mixin.plugin;

import java.util.List;
import java.util.Set;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.world.worldgen.terrablender.TBCompat;

public class MixinPlugin implements IMixinConfigPlugin {

	@Override
	public void onLoad(String mixinPackage) {
		if(TBCompat.isEnabled()) {
			RTFCommon.LOGGER.info("Enabling Terrablender compat");
		} else {
			RTFCommon.LOGGER.info("Disabling Terrablender compat");
		}
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		return TBCompat.isTBMixin(mixinClassName) ? TBCompat.isEnabled() : true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
	}

	@Override
	public List<String> getMixins() {
		return TBCompat.TERRABLENDER_COMPAT_MIXINS.stream().map((str) -> {
			return str.replace("raccoonman.reterraforged.mixin.", "");
		}).toList();
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
	}
}
