package raccoonman.reterraforged.data.preset;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public class PresetTerrainNoise {
	public static final ResourceKey<Noise> MOUNTAIN_CHAIN_ALPHA = createKey("mountain_chain_alpha");
	public static final ResourceKey<Noise> RIVER_ALPHA = createKey("river_alpha");
	public static final ResourceKey<Noise> LAKE_ALPHA = createKey("lake_alpha");
	public static final ResourceKey<Noise> SWAMP_ALPHA = createKey("swamp_alpha");
	
	public static final ResourceKey<Noise> EROSION = createKey("erosion");
	public static final ResourceKey<Noise> RIDGES = createKey("ridges");
	public static final ResourceKey<Noise> RIDGES_FOLDED = createKey("ridges_folded");
	public static final ResourceKey<Noise> OFFSET = createKey("offset");
	
	public static void bootstrap(Preset preset, BootstapContext<Noise> ctx) {
	}

	protected static ResourceKey<Noise> createKey(String name) {
		return PresetNoiseData.createKey("terrain/" + name);
	}
}
