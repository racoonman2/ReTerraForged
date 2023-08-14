package raccoonman.reterraforged.common.registries.data.tags;

import net.minecraft.tags.TagKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.Climate;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public final class RTFClimateTags {
	public static final TagKey<Climate> TEMPERATE = resolve("temperate");
	public static final TagKey<Climate> DEEP = resolve("deep");

	private static TagKey<Climate> resolve(String path) {
		return TagKey.create(RTFRegistries.CLIMATE, ReTerraForged.resolve(path));
	}
}
