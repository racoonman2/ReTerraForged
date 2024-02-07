package raccoonman.reterraforged.tags;

import net.minecraft.tags.TagKey;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.surface.rule.LayeredSurfaceRule;

public class RTFSurfaceLayerTags {
	public static final TagKey<LayeredSurfaceRule.Layer> ON_FLOOR = resolve("on_floor");
	public static final TagKey<LayeredSurfaceRule.Layer> UNDER_FLOOR = resolve("under_floor");
	public static final TagKey<LayeredSurfaceRule.Layer> EROSION = resolve("erosion");
	public static final TagKey<LayeredSurfaceRule.Layer> EROSION_VARIANT = resolve("erosion_variant");

    private static TagKey<LayeredSurfaceRule.Layer> resolve(String path) {
    	return TagKey.create(RTFRegistries.SURFACE_LAYERS, RTFCommon.location(path));
    }
}
