package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

final class TerrainTypes {
	private static final ResourceKey<Noise> DEEP_OCEAN_SELECTOR = RTFTerrainNoise.createKey("deep_ocean/selector");
	private static final ResourceKey<Noise> DEEP_OCEAN_HILLS = RTFTerrainNoise.createKey("deep_ocean/hills");
	private static final ResourceKey<Noise> DEEP_OCEAN_CANYONS = RTFTerrainNoise.createKey("deep_ocean/canyons");
	public static final ResourceKey<Noise> DEEP_OCEAN = RTFTerrainNoise.createKey("deep_ocean/variance");

	public static final ResourceKey<Noise> SHALLOW_OCEAN = RTFTerrainNoise.createKey("shallow_ocean/variance");
	public static final ResourceKey<Noise> COAST = RTFTerrainNoise.createKey("coast/variance");
	
	public static void bootstrap(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, WorldSettings worldSettings, TerrainSettings terrainSettings, Seed seed) {
		WorldSettings.Properties properties = worldSettings.properties;
		
		int yScale = 256;
		int seaLevelY = properties.seaLevel;
		
		float seaLevel = ((float) seaLevelY - 1.0F) / yScale;
	
		Noise coast = RTFNoise.registerAndWrap(ctx, COAST, Source.constant(seaLevel));
		ctx.register(SHALLOW_OCEAN, coast.sub(Source.constant(7.0F / yScale)));
		registerDeepOcean(ctx, seaLevel, seed.next());
	}
	
	private static void registerDeepOcean(BootstapContext<Noise> ctx, float seaLevel, int seed) {
		Noise hills = RTFNoise.registerAndWrap(ctx, DEEP_OCEAN_HILLS, Source.perlin(++seed, 150, 3).scale(seaLevel * 0.7)).bias(Source.perlin(++seed, 200, 1).scale(seaLevel * 0.2f));
		Noise canyons = RTFNoise.registerAndWrap(ctx, DEEP_OCEAN_CANYONS, Source.perlin(++seed, 150, 4).powCurve(0.2).invert().scale(seaLevel * 0.7).bias(Source.perlin(++seed, 170, 1).scale(seaLevel * 0.15f)));
		Noise selector = RTFNoise.registerAndWrap(ctx, DEEP_OCEAN_SELECTOR, Source.perlin(++seed, 500, 1));
		ctx.register(DEEP_OCEAN, selector.blend(hills, canyons, 0.6, 0.65).warp(++seed, 50, 2, 50.0));
	}
}
