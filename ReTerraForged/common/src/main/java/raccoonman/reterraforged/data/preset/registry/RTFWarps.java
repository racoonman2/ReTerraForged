package raccoonman.reterraforged.data.preset.registry;

import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.Seed;
import raccoonman.reterraforged.world.worldgen.noise.Noises;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warp;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warps;

public class RTFWarps {
	public static final ResourceKey<Warp> TERRAIN_REGION = createKey("terrain_region");
	
	public static void bootstrap(Preset preset, BootstrapContext<Warp> ctx) {
		ctx.register(TERRAIN_REGION, regionWarp(new Seed(0)));
	}
	
	private static Warp regionWarp(Seed seed) {
		int regionWarpScale = 400;
		int regionWarpStrength = 200;
		return Warps.noise(
			Noises.simplex(seed.next(), regionWarpScale, 1),
			Noises.simplex(seed.next(), regionWarpScale, 1),
			Noises.constant(regionWarpStrength)
		);
	}
	
	private static ResourceKey<Warp> createKey(String name) {
		return RTFRegistries.createKey(RTFRegistries.WARP, name);
	}
}
