package raccoonman.reterraforged.tags;

import net.minecraft.tags.TagKey;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.ParameterSlice;

public class RTFParameterSliceTags {
//	TODO
//	public static final TagKey<ParameterSlice> TERRAIN_TYPES = tag("terrain_types");
	public static final TagKey<ParameterSlice> BIOME_TYPES = tag("biome_types");
	
    private static TagKey<ParameterSlice> tag(String path) {
    	return TagKey.create(RTFRegistries.PARAMETER_SLICE, RTFCommon.location(path));
    }
}
