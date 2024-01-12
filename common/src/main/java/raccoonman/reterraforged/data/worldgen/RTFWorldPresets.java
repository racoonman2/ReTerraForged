package raccoonman.reterraforged.data.worldgen;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import raccoonman.reterraforged.RTFCommon;

//TODO
public class RTFWorldPresets {
	public static final ResourceKey<WorldPreset> RETERRAFORGED = createKey("reterraforged");
	
	public static ResourceKey<WorldPreset> createKey(String name) {
		return ResourceKey.create(Registries.WORLD_PRESET, RTFCommon.location(name));
	}
}
