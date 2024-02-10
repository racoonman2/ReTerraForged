package raccoonman.reterraforged.tags;

import net.minecraft.tags.TagKey;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.surface.rule.LayeredSurfaceRule;

public class RTFSurfaceLayerTags {
	public static final TagKey<LayeredSurfaceRule.Layer> TERRABLENDER = resolve("terrablender");
	
    private static TagKey<LayeredSurfaceRule.Layer> resolve(String path) {
    	return TagKey.create(RTFRegistries.SURFACE_LAYERS, RTFCommon.location(path));
    }
}
