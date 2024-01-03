package raccoonman.reterraforged.data.worldgen;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.data.worldgen.preset.WorldSettings;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class TerrainTypeNoise {
	public static final ResourceKey<Noise> GROUND = TerrainNoise.createKey("ground");
	
	public static void bootstrap(Preset preset, BootstapContext<Noise> ctx) {
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		float seaLevel = properties.seaLevel;
		int terrainScaler = properties.terrainScaler();

		ctx.register(GROUND, Noises.constant(seaLevel / (float)terrainScaler));
	}
}
