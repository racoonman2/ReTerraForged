package raccoonman.reterraforged.world.worldgen.feature;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.feature.chance.ChanceFeature;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateFeature;

public class RTFFeatures {
	public static final Feature<TemplateFeature.Config<?>> TEMPLATE = register("template", new TemplateFeature(TemplateFeature.Config.CODEC));
	public static final Feature<BushFeature.Config> BUSH = register("bush", new BushFeature(BushFeature.Config.CODEC));
	public static final Feature<DiskConfiguration> DISK = register("disk", new DiskFeature(DiskConfiguration.CODEC));
	public static final Feature<ChanceFeature.Config> CHANCE = register("chance", new ChanceFeature(ChanceFeature.Config.CODEC));
	public static final Feature<ErodeFeature.Config> ERODE = register("erode", new ErodeFeature(ErodeFeature.Config.CODEC));
	public static final Feature<DecorateSnowFeature.Config> DECORATE_SNOW = register("decorate_snow", new DecorateSnowFeature(DecorateSnowFeature.Config.CODEC));
	public static final Feature<SwampSurfaceFeature.Config> SWAMP_SURFACE = register("swamp_surface", new SwampSurfaceFeature(SwampSurfaceFeature.Config.CODEC));
	
	public static void bootstrap() {
	}
	
	private static <T extends FeatureConfiguration> Feature<T> register(String name, Feature<T> feature) {
		RegistryUtil.register(BuiltInRegistries.FEATURE, name, feature);
		return feature;
	}
}
