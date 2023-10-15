package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class RTFNoise {

	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		Seed seed = new Seed(0);
		
		RTFClimateNoise.bootstrap(ctx, preset.climate());
		RTFTerrainNoise.bootstrap(ctx, noise, preset.world(), preset.terrain(), seed);
		RTFStrataNoise.bootstrap(ctx, preset.miscellaneous(), seed);
	}

	protected static Noise midpoint(float min, float max) {
		return Source.constant((min + max) / 2.0F);
	}
	
	protected static Noise lookup(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
		return new HolderNoise(noise.getOrThrow(key));
	}
	
	protected static Noise registerAndWrap(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise noise) {
		return new HolderNoise(ctx.register(key, noise));
	}

	protected static ResourceKey<Noise> createKey(String name) {
		return ResourceKey.create(RTFRegistries.NOISE, ReTerraForged.resolve(name));
	}
}
