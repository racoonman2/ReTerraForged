package raccoonman.reterraforged.world.worldgen.feature;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateFeature;

public class RTFFeatures {
	public static final Feature<TemplateFeature.Config<?>> TEMPLATE = register("template", new TemplateFeature(TemplateFeature.Config.CODEC));
	public static final Feature<DensityFeature.Config> DENSITY = register("density", new DensityFeature(DensityFeature.Config.CODEC));
	public static final Feature<BushFeature.Config> BUSH = register("bush", new BushFeature(BushFeature.Config.CODEC));
	public static final Feature<DiskFeature.Config> DISK = register("disk", new DiskFeature(DiskFeature.Config.CODEC));
	public static final Feature<ProcessSnowFeature.Config> PROCESS_SNOW = register("process_snow", new ProcessSnowFeature(ProcessSnowFeature.Config.CODEC));
	public static final Feature<PerBiomeFeature.Config> PER_BIOME = register("per_biome", new PerBiomeFeature(PerBiomeFeature.Config.CODEC));
	
	public static void bootstrap() {
	} 
	
	private static <T extends FeatureConfiguration> Feature<T> register(String name, Feature<T> feature) {
		RegistryUtil.register(BuiltInRegistries.FEATURE, name, feature);
		return feature;
	}
}
