package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.General;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.Terrain;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

final class TerrainTypes {
	public static final ResourceKey<Noise> GROUND = RTFTerrainNoise.createKey("ground");

	private static final ResourceKey<Noise> STEPPE_VARIANCE = RTFTerrainNoise.createKey("steppe/variance");
	public static final ResourceKey<Noise> STEPPE_HEIGHT = RTFTerrainNoise.createKey("steppe/height");
	public static final ResourceKey<Noise> STEPPE_EROSION = RTFTerrainNoise.createKey("steppe/erosion");
	public static final ResourceKey<Noise> STEPPE_RIDGE = RTFTerrainNoise.createKey("steppe/ridge");
	
	private static final ResourceKey<Noise> PLAINS_VARIANCE = RTFTerrainNoise.createKey("plains/variance");
	public static final ResourceKey<Noise> PLAINS_HEIGHT = RTFTerrainNoise.createKey("plains/height");
	public static final ResourceKey<Noise> PLAINS_EROSION = RTFTerrainNoise.createKey("plains/erosion");
	public static final ResourceKey<Noise> PLAINS_RIDGE = RTFTerrainNoise.createKey("plains/ridge");

	private static final ResourceKey<Noise> PLATEAU_VALLEY = RTFTerrainNoise.createKey("plateau/valley");
	private static final ResourceKey<Noise> PLATEAU_TOP = RTFTerrainNoise.createKey("plateau/top");
	private static final ResourceKey<Noise> PLATEAU_SURFACE = RTFTerrainNoise.createKey("plateau/surface");
	private static final ResourceKey<Noise> PLATEAU_VARIANCE = RTFTerrainNoise.createKey("plateau/variance");
	public static final ResourceKey<Noise> PLATEAU_HEIGHT = RTFTerrainNoise.createKey("plateau/height");
	public static final ResourceKey<Noise> PLATEAU_EROSION = RTFTerrainNoise.createKey("plateau/erosion");
	public static final ResourceKey<Noise> PLATEAU_RIDGE = RTFTerrainNoise.createKey("plateau/ridge");
	
	private static final ResourceKey<Noise> HILLS_1_VARIANCE = RTFTerrainNoise.createKey("hills_1/variance");
	public static final ResourceKey<Noise> HILLS_1_HEIGHT = RTFTerrainNoise.createKey("hills_1/height");
	public static final ResourceKey<Noise> HILLS_1_EROSION = RTFTerrainNoise.createKey("hills_1/erosion");
	public static final ResourceKey<Noise> HILLS_1_RIDGE = RTFTerrainNoise.createKey("hills_1/ridge");

	private static final ResourceKey<Noise> HILLS_2_VARIANCE = RTFTerrainNoise.createKey("hills_2/variance");
	public static final ResourceKey<Noise> HILLS_2_HEIGHT = RTFTerrainNoise.createKey("hills_2/height");
	public static final ResourceKey<Noise> HILLS_2_EROSION = RTFTerrainNoise.createKey("hills_2/erosion");
	public static final ResourceKey<Noise> HILLS_2_RIDGE = RTFTerrainNoise.createKey("hills_2/ridge");

	private static final ResourceKey<Noise> DALES_HILLS_1 = RTFTerrainNoise.createKey("dales/hills_1");
	private static final ResourceKey<Noise> DALES_HILLS_2 = RTFTerrainNoise.createKey("dales/hills_2");
	private static final ResourceKey<Noise> DALES_SELECTOR = RTFTerrainNoise.createKey("dales/selector");
	private static final ResourceKey<Noise> DALES_WARP_X = RTFTerrainNoise.createKey("dales/warp_x");
	private static final ResourceKey<Noise> DALES_WARP_Y = RTFTerrainNoise.createKey("dales/warp_y");
	private static final ResourceKey<Noise> DALES_WARP_POWER = RTFTerrainNoise.createKey("dales/warp_power");
	private static final ResourceKey<Noise> DALES_VARIANCE = RTFTerrainNoise.createKey("dales/variance");
	public static final ResourceKey<Noise> DALES_HEIGHT = RTFTerrainNoise.createKey("dales/height");
	public static final ResourceKey<Noise> DALES_EROSION = RTFTerrainNoise.createKey("dales/erosion");
	public static final ResourceKey<Noise> DALES_RIDGE = RTFTerrainNoise.createKey("dales/ridge");
	
	private static final ResourceKey<Noise> DEEP_OCEAN_HILLS = RTFTerrainNoise.createKey("deep_ocean/hills");
	private static final ResourceKey<Noise> DEEP_OCEAN_CANYONS = RTFTerrainNoise.createKey("deep_ocean/canyons");
	private static final ResourceKey<Noise> DEEP_OCEAN_SELECTOR = RTFTerrainNoise.createKey("deep_ocean/selector");
	public static final ResourceKey<Noise> DEEP_OCEAN_HEIGHT = RTFTerrainNoise.createKey("deep_ocean/height");

	public static final ResourceKey<Noise> SHALLOW_OCEAN_HEIGHT = RTFTerrainNoise.createKey("shallow_ocean/height");
	public static final ResourceKey<Noise> COAST_HEIGHT = RTFTerrainNoise.createKey("coast/height");
	
	public static void bootstrap(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, WorldSettings worldSettings, TerrainSettings terrainSettings, Seed seed) {
		WorldSettings.Properties properties = worldSettings.properties;
		TerrainSettings.General generalSettings = terrainSettings.general;
		
		Seed terrainSeed = seed.offset(generalSettings.terrainSeedOffset);

		Noise unit = RTFNoise.lookup(noise, RTFTerrainNoise.UNIT);
		Noise ground = RTFNoise.registerAndWrap(ctx, GROUND, Source.constant(properties.seaLevel).mul(unit));
		
		registerSteppe(ctx, noise, terrainSeed, terrainSettings.steppe);
		registerPlains(ctx, noise, terrainSeed, terrainSettings.plains, generalSettings);
		registerPlateau(ctx, noise, terrainSeed, terrainSettings.plateau, generalSettings);
		registerHills1(ctx, noise, terrainSeed, generalSettings, terrainSettings.hills);
		registerHills2(ctx, noise, terrainSeed, generalSettings, terrainSettings.hills);
		registerDales(ctx, noise, terrainSeed, terrainSettings.dales);
		
		Noise coast = RTFNoise.registerAndWrap(ctx, COAST_HEIGHT, ground.sub(unit)); // one block below ground level
		ctx.register(SHALLOW_OCEAN_HEIGHT, coast.sub(unit.mul(Source.constant(7.0F)))); // 7 blocks below coast
		registerDeepOcean(ctx, coast, seed.next());
	}
	
	private static void registerSteppe(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, Seed seed, Terrain settings) {
        int scaleH = Math.round(250.0F * settings.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 200.0);
        
        registerLand(ctx, noise, STEPPE_VARIANCE, STEPPE_HEIGHT, settings, module.scale(0.08).bias(-0.02));
        ctx.register(STEPPE_EROSION, RTFNoise.lookup(noise, ErosionLevels.LEVEL_4));
        ctx.register(STEPPE_RIDGE, RTFNoise.lookup(noise, RidgeLevels.MID_SLICE));
	}
	
	private static void registerPlains(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, Seed seed, Terrain terrainSettings, General generalSettings) {
        int scaleH = Math.round(250.0F * terrainSettings.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 256.0);
        Noise scaled = module.scale(0.15F * generalSettings.globalVerticalScale).bias(-0.02);
        registerLand(ctx, noise, PLAINS_VARIANCE, PLAINS_HEIGHT, terrainSettings, scaled);
        ctx.register(PLAINS_EROSION, RTFNoise.lookup(noise, ErosionLevels.LEVEL_4));
        ctx.register(PLAINS_RIDGE, RTFNoise.lookup(noise, RidgeLevels.LOW_SLICE));
	}

	private static void registerPlateau(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, Seed seed, Terrain terrainSettings, General generalSettings) {
		Noise valley = RTFNoise.registerAndWrap(ctx, PLATEAU_VALLEY, Source.ridge(seed.next(), 500, 1).invert().warp(seed.next(), 100, 1, 150.0).warp(seed.next(), 20, 1, 15.0));
		Noise top = RTFNoise.registerAndWrap(ctx, PLATEAU_TOP, Source.build(seed.next(), 150, 3).lacunarity(2.45).ridge().warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 40, 2, 20.0).scale(0.15).mul(valley.clamp(0.02, 0.1).map(0.0, 1.0)));
		Noise surface = RTFNoise.registerAndWrap(ctx, PLATEAU_SURFACE, Source.perlin(seed.next(), 20, 3).scale(0.05).warp(seed.next(), 40, 2, 20.0));
		Noise module = valley.mul(Source.cubic(seed.next(), 500, 1).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
        registerLand(ctx, noise, PLATEAU_VARIANCE, PLATEAU_HEIGHT, terrainSettings, module.scale(0.475 * generalSettings.globalVerticalScale));
        ctx.register(PLATEAU_EROSION, top.threshold(0.1D, RTFNoise.lookup(noise, ErosionLevels.LEVEL_4), RTFNoise.lookup(noise, ErosionLevels.LEVEL_2)));
        ctx.register(PLATEAU_RIDGE, RTFNoise.lookup(noise, RidgeLevels.MID_SLICE));
	}
	
	private static void registerHills1(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, Seed seed, General generalSettings, Terrain terrainSettings) {
    	registerLand(ctx, noise, HILLS_1_VARIANCE, HILLS_1_HEIGHT, terrainSettings, Source.perlin(seed.next(), 200, 3).mul(Source.billow(seed.next(), 400, 3).alpha(0.5)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).scale(0.6f * generalSettings.globalVerticalScale));
    	ctx.register(HILLS_1_EROSION, RTFNoise.lookup(noise, ErosionLevels.LEVEL_4));
    	ctx.register(HILLS_1_RIDGE, RTFNoise.lookup(noise, RidgeLevels.MID_SLICE));
    }
    
	private static void registerHills2(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, Seed seed, General generalSettings, Terrain terrainSettings) {
    	registerLand(ctx, noise, HILLS_2_VARIANCE, HILLS_2_HEIGHT, terrainSettings, Source.cubic(seed.next(), 128, 2).mul(Source.perlin(seed.next(), 32, 4).alpha(0.075)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).mul(Source.ridge(seed.next(), 512, 2).alpha(0.8)).scale(0.55f * generalSettings.globalVerticalScale));
    	ctx.register(HILLS_2_EROSION, RTFNoise.lookup(noise, ErosionLevels.LEVEL_6));
    	ctx.register(HILLS_2_RIDGE, RTFNoise.lookup(noise, RidgeLevels.HIGH_SLICE));
    }

	private static void registerDales(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, Seed seed, Terrain settings) {
		Noise hills1 = RTFNoise.registerAndWrap(ctx, DALES_HILLS_1, Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75));
        Noise hills2 = RTFNoise.registerAndWrap(ctx, DALES_HILLS_2, Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25));
        Noise selector = RTFNoise.registerAndWrap(ctx, DALES_SELECTOR, Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0));
        Noise combined = selector.blend(hills1, hills2, 0.4, 0.75);
        int warpSeed = seed.next();
        Noise warpX = RTFNoise.registerAndWrap(ctx, DALES_WARP_X, Source.build(warpSeed, 300, 1).perlin());
        Noise warpY = RTFNoise.registerAndWrap(ctx, DALES_WARP_Y, Source.build(warpSeed + 1, 300, 1).perlin());
        Noise warpPower = RTFNoise.registerAndWrap(ctx, DALES_WARP_POWER, Source.constant(100.0));
        Noise hills3 = combined.pow(1.125).warp(warpX, warpY, warpPower);

        Noise erosion2 = RTFNoise.lookup(noise, ErosionLevels.LEVEL_2);
        Noise erosion4 = RTFNoise.lookup(noise, ErosionLevels.LEVEL_4);
        
    	registerLand(ctx, noise, DALES_VARIANCE, DALES_HEIGHT, settings, hills3.scale(0.4));
    	ctx.register(DALES_EROSION, selector.warp(warpX, warpY, warpPower).blend(hills1.warp(warpX, warpY, warpPower), Source.ZERO, 0.4, 0.15).threshold(0.2D, erosion4, erosion2));
    	ctx.register(DALES_RIDGE, RTFNoise.lookup(noise, RidgeLevels.MID_SLICE));
    }
    
	private static void registerDeepOcean(BootstapContext<Noise> ctx, Noise seaLevel, int seed) {
        Noise hills = RTFNoise.registerAndWrap(ctx, DEEP_OCEAN_HILLS, Source.perlin(++seed, 150, 3).scale(seaLevel.mul(Source.constant(0.7)).bias(Source.perlin(++seed, 200, 1).scale(seaLevel.mul(Source.constant(0.2f))))));
        Noise canyons = RTFNoise.registerAndWrap(ctx, DEEP_OCEAN_CANYONS, Source.perlin(++seed, 150, 4).powCurve(0.2).invert().scale(seaLevel.mul(Source.constant(0.7)).bias(Source.perlin(++seed, 170, 1).scale(seaLevel.mul(Source.constant(0.15f))))));
        Noise selector = RTFNoise.registerAndWrap(ctx, DEEP_OCEAN_SELECTOR, Source.perlin(++seed, 500, 1));
        ctx.register(DEEP_OCEAN_HEIGHT, selector.blend(hills, canyons, 0.6, 0.65).warp(++seed, 50, 2, 50.0));
	}
    
	private static void registerLand(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, ResourceKey<Noise> varianceKey, ResourceKey<Noise> heightKey, Terrain settings, Noise variance) {
		variance = RTFNoise.registerAndWrap(ctx, varianceKey, variance);
		ctx.register(heightKey, RTFNoise.lookup(noise, GROUND).mul(Source.constant(settings.baseScale)).add(variance.mul(Source.constant(settings.verticalScale))));
	}
}
