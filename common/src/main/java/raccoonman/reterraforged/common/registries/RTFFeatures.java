package raccoonman.reterraforged.common.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.feature.Feature;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.feature.TemplateFeature;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFFeatures {
	public static final ResourceKey<Feature<?>> TEMPLATE = resolve("template");
	
	public static void register() {
		RegistryUtil.register(TEMPLATE, TemplateFeature::new);
	}
	
	private static ResourceKey<Feature<?>> resolve(String name) {
		return ReTerraForged.resolve(Registries.FEATURE, name);
	}
}
