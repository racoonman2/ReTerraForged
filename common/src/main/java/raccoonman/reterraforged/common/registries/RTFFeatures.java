package raccoonman.reterraforged.common.registries;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.common.level.levelgen.feature.TemplateFeature;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFFeatures {
	public static final TemplateFeature TEMPLATE = register("template", new TemplateFeature(TemplateFeature.Config.CODEC));
	
	public static void bootstrap() {
	}
	
	private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String string, F feature) {
		RegistryUtil.register(BuiltInRegistries.FEATURE, string, feature);
		return feature;
    }
}
