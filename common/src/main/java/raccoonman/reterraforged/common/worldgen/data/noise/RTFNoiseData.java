package raccoonman.reterraforged.common.worldgen.data.noise;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class RTFNoiseData {

	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		Seed seed = new Seed(0);
		RTFContinentNoise.bootstrap(ctx, preset.world(), seed);
		RTFTerrainNoise2.bootstrap(ctx, preset.world(), preset.terrain(), seed);
		RTFClimateNoise.bootstrap(ctx, preset, seed);
		
	}
	
	private static Noise getNoise(HolderGetter<Noise> getter, ResourceKey<Noise> key) {
		return new HolderNoise(getter.getOrThrow(key));
	}
	
	private static Noise register(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise value) {
		return new HolderNoise(ctx.register(key, value));
	}
	
	protected static ResourceKey<Noise> createKey(String string) {
        return ResourceKey.create(RTFRegistries.NOISE, ReTerraForged.resolve(string));
    }
	    
}
