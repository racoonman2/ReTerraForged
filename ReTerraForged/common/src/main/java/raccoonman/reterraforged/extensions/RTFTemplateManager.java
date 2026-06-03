package raccoonman.reterraforged.extensions;

import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.world.worldgen.feature.template.FeatureTemplate;

public interface RTFTemplateManager {
	FeatureTemplate loadFeatureTemplate(ResourceLocation location);
}
