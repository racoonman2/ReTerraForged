package raccoonman.reterraforged.data.worldgen;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.worldgen.preset.ClimateSettings;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.data.worldgen.preset.WorldSettings;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public class ClimateNoise {
	public static final ResourceKey<Noise> BIOME_EDGE_SHAPE = createKey("biome_edge_shape");
	
	public static void bootstrap(Preset preset, BootstapContext<Noise> ctx) {
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		
		ClimateSettings climateSettings = preset.climate();
		ClimateSettings.BiomeNoise biomeEdgeShape = climateSettings.biomeEdgeShape;
		
		ctx.register(BIOME_EDGE_SHAPE, biomeEdgeShape.build(0));
	}

	private static ResourceKey<Noise> createKey(String name) {
		return RTFNoiseData.createKey("climate/" + name);
	}
}
