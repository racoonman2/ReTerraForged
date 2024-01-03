package raccoonman.reterraforged.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;

public class RTFNoiseData {

	public static void bootstrap(Preset preset, BootstapContext<Noise> ctx) {
		TerrainNoise.bootstrap(preset, ctx);
		ClimateNoise.bootstrap(preset, ctx);
		SurfaceNoise.bootstrap(preset, ctx);
	}
	
	public static Noise getNoise(HolderGetter<Noise> noiseLookup, ResourceKey<Noise> key) {
		return new Noises.HolderHolder(noiseLookup.getOrThrow(key));
	}
	
	public static Noise registerAndWrap(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise noise) {
		return new Noises.HolderHolder(ctx.register(key, noise));
	}
	
	public static ResourceKey<Noise> createKey(String name) {
        return ResourceKey.create(RTFRegistries.NOISE, RTFCommon.location(name));
	}
}
