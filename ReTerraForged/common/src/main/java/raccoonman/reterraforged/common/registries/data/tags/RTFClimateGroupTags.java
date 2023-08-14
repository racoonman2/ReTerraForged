package raccoonman.reterraforged.common.registries.data.tags;

import net.minecraft.tags.TagKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateGroup;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public final class RTFClimateGroupTags {
    public static final TagKey<ClimateGroup> DEFAULT = resolve("default");	

	private static TagKey<ClimateGroup> resolve(String path) {
		return TagKey.create(RTFRegistries.CLIMATE_GROUP, ReTerraForged.resolve(path));
	}
}
