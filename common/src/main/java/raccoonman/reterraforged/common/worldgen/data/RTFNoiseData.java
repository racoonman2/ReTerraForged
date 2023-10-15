package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.ClimateRegion;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.ClimateRegionEdge;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.ClimateRegionOffset;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Moisture;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Temperature;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper2;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper3;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.ClimateSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.ControlPoints;

public final class RTFNoiseData {
	public static final ResourceKey<Noise> STRATA_SELECTOR = createStrataKey("selector");
	public static final ResourceKey<Noise> STRATA_DEPTH = createStrataKey("depth");
	
	public static final ResourceKey<Noise> TEMPERATURE = createClimateKey("temperature");
	public static final ResourceKey<Noise> MOISTURE = createClimateKey("moisture");
	public static final ResourceKey<Noise> CONTINENT = createContinentKey("continent");

	public static final ResourceKey<Noise> EROSION_0 = createErosionKey("level_0");
	public static final ResourceKey<Noise> EROSION_1 = createErosionKey("level_1");
	public static final ResourceKey<Noise> EROSION_2 = createErosionKey("level_2");
	public static final ResourceKey<Noise> EROSION_3 = createErosionKey("level_3");
	public static final ResourceKey<Noise> EROSION_4 = createErosionKey("level_4");
	public static final ResourceKey<Noise> EROSION_5 = createErosionKey("level_5");
	public static final ResourceKey<Noise> EROSION_6 = createErosionKey("level_6");

	public static final ResourceKey<Noise> PEAKS = createRidgeKey("peaks");
	public static final ResourceKey<Noise> HIGH_SLICE = createRidgeKey("high_slice");
	public static final ResourceKey<Noise> LOW_SLICE = createRidgeKey("low_slice");
	public static final ResourceKey<Noise> MID_SLICE = createRidgeKey("mid_slice");
	public static final ResourceKey<Noise> VALLEYS = createRidgeKey("valleys");
	public static final ResourceKey<Noise> LOW_SLICE_WEIRD = createRidgeKey("low_slice_weird");
	public static final ResourceKey<Noise> MID_SLICE_WEIRD = createRidgeKey("mid_slice_weird");
	public static final ResourceKey<Noise> HIGH_SLICE_WEIRD = createRidgeKey("high_slice_weird");
	public static final ResourceKey<Noise> PEAKS_WEIRD = createRidgeKey("peaks_weird");

	public static final ResourceKey<Noise> GROUND = createFeatureKey("ground");
	
	private static final ResourceKey<Noise> DALES_HILL_SELECTOR = createFeatureKey("dales/hill_selector");
	private static final ResourceKey<Noise> DALES_HILL_1 = createFeatureKey("dales/hill_1");
	private static final ResourceKey<Noise> DALES_HILL_2 = createFeatureKey("dales/hill_2");
	private static final ResourceKey<Noise> DALES_VARIANCE = createFeatureKey("dales/variance");
	private static final ResourceKey<Noise> DALES_EROSION = createFeatureKey("dales/erosion");
	private static final ResourceKey<Noise> DALES_RIDGE = createFeatureKey("dales/ridge");
	
	public static final ResourceKey<Noise> COAST = createFeatureKey("coast");
	public static final ResourceKey<Noise> SHALLOW_OCEAN = createFeatureKey("shallow_ocean");
	public static final ResourceKey<Noise> DEEP_OCEAN = createFeatureKey("deep_ocean");

	public static final ResourceKey<Noise> LAND = createTerrainKey("land");
	public static final ResourceKey<Noise> OCEANS = createTerrainKey("oceans");

	public static final ResourceKey<Noise> VARIANCE = createTerrainKey("variance");
	public static final ResourceKey<Noise> EROSION = createTerrainKey("erosion");	
	public static final ResourceKey<Noise> RIDGE = createTerrainKey("ridge");
	
	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		Seed seed = new Seed(0);
		
		WorldSettings world = preset.world();
		ClimateSettings climate = preset.climate();
		TerrainSettings terrain = preset.terrain();
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		
		if(miscellaneous.strataDecorator) {
			registerStrata(ctx, seed.split(), miscellaneous);
		}
        	
		int biomeSize = climate.biomeShape.biomeSize;
        float tempScaler = climate.temperature.scale;
        float moistScaler = climate.moisture.scale * 2.5F;
        float biomeFreq = 1.0F / biomeSize;
        float moistureSize = moistScaler * biomeSize;
        float temperatureSize = tempScaler * biomeSize;
        int moistScale = NoiseUtil.round(moistureSize * biomeFreq);
        int tempScale = NoiseUtil.round(temperatureSize * biomeFreq);
        int warpScale = climate.biomeShape.biomeWarpScale;
        float warpStrength = climate.biomeShape.biomeWarpStrength;
        Noise warpX = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
        Noise warpZ = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);

        Noise regionOffsetX = climate.biomeEdgeShape.build(seed.next());
        Noise regionOffsetZ = climate.biomeEdgeShape.build(seed.next());
        
        Seed moistureSeed = seed.offset(climate.moisture.seedOffset);
        Noise moisture = new Moisture(Source.simplex(moistureSeed.next(), moistScale, 1).clamp(0.125, 0.875).map(0.0, 1.0).freq(0.5, 1.0), Source.constant(climate.moisture.falloff));
        ctx.register(MOISTURE, applyRegionOffset(biomeFreq, warpX, warpZ, warpStrength, climate.moisture.apply(moisture).warp(moistureSeed.next(), Math.max(1, moistScale / 2), 1, moistScale / 4.0).warp(moistureSeed.next(), Math.max(1, moistScale / 6), 2, moistScale / 12.0), regionOffsetX, regionOffsetZ, climate.biomeEdgeShape.strength));
        Seed tempSeed = seed.offset(climate.temperature.seedOffset);
        Noise temperature = new Temperature(Source.constant(1.0F / tempScale), Source.constant(climate.temperature.falloff));
        ctx.register(TEMPERATURE, applyRegionOffset(biomeFreq, warpX, warpZ, warpStrength, climate.temperature.apply(temperature).warp(tempSeed.next(), tempScale * 4, 2, tempScale * 4).warp(tempSeed.next(), tempScale, 1, tempScale), regionOffsetX, regionOffsetZ, climate.biomeEdgeShape.strength));
        Noise continent = registerAndWrap(ctx, CONTINENT, world.continent.continentType.create(world, seed));
    
        registerErosionLevels(ctx);
        registerRidges(ctx);
        
        ControlPoints controlPoints = world.controlPoints;
        float seaLevel = world.properties.seaLevel;
        float yScale = 256.0F;
        float waterY = seaLevel - 1.0F;
        float water = waterY / yScale;
        
        Seed terrainSeed = seed.offset(terrain.general.terrainSeedOffset);
        
        registerDales(ctx, noise, terrainSeed);
        
        Noise coast = registerAndWrap(ctx, COAST, Source.constant(water));
        Noise shallowOcean = registerAndWrap(ctx, SHALLOW_OCEAN, Source.constant((waterY - 7) / yScale));
        Noise deepOcean = registerAndWrap(ctx, DEEP_OCEAN, makeDeepOcean(seed.next(), water));
        
        Noise oceans = registerAndWrap(ctx, OCEANS, new ContinentLerper3(continent, deepOcean, shallowOcean, coast, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast, Interpolation.CURVE3));
        Noise ground = registerAndWrap(ctx, GROUND, Source.constant(seaLevel / yScale));
        Noise land = registerAndWrap(ctx, LAND, ground.add(wrapNoise(noise, DALES_VARIANCE)));
        ctx.register(VARIANCE, new ContinentLerper2(continent, oceans, land, controlPoints.shallowOcean, controlPoints.inland, Interpolation.LINEAR));
        ctx.register(EROSION, wrapNoise(noise, DALES_EROSION));// new ContinentLerper2(continent, oceans, land, controlPoints.shallowOcean, controlPoints.inland, Interpolation.LINEAR));
        ctx.register(RIDGE, wrapNoise(noise, DALES_RIDGE));// new ContinentLerper2(continent, oceans, land, controlPoints.shallowOcean, controlPoints.inland, Interpolation.LINEAR));
	}
	
	private static void registerStrata(BootstapContext<Noise> ctx, Seed seed, MiscellaneousSettings miscellaneous) {
        ctx.register(STRATA_SELECTOR, Source.cell(seed.next(), miscellaneous.strataRegionSize)
        	.warp(seed.next(), miscellaneous.strataRegionSize / 4, 2, miscellaneous.strataRegionSize / 2D)
        	.warp(seed.next(), 15, 2, 30));
        ctx.register(STRATA_DEPTH, Source.perlin(seed.next(), 128, 3));
	}
	
	private static void registerErosionLevels(BootstapContext<Noise> ctx) {
		ctx.register(EROSION_0, Source.constant(-0.89));
		ctx.register(EROSION_1, Source.constant(-0.5775));
		ctx.register(EROSION_2, Source.constant(-0.29874998));
		ctx.register(EROSION_3, Source.constant(-0.08625));
		ctx.register(EROSION_4, Source.constant(0.25));
		ctx.register(EROSION_5, Source.constant(0.5));
		ctx.register(EROSION_6, Source.constant(0.775));
	}
	
	private static void registerRidges(BootstapContext<Noise> ctx) {
		Noise peaks = registerAndWrap(ctx, PEAKS, Source.constant(-0.6666667));
		Noise highSlice = registerAndWrap(ctx, HIGH_SLICE, Source.constant(-0.48333335));
		Noise midSlice = registerAndWrap(ctx, MID_SLICE, Source.constant(-0.33333334));
		Noise lowSlice = registerAndWrap(ctx, LOW_SLICE, Source.constant(-0.15833335));
		ctx.register(VALLEYS, Source.ZERO);
		ctx.register(LOW_SLICE_WEIRD, lowSlice.negate());
		ctx.register(MID_SLICE_WEIRD, midSlice.negate());
		ctx.register(HIGH_SLICE_WEIRD, highSlice.negate());
		ctx.register(PEAKS_WEIRD, peaks.negate());
	}

	private static void registerDales(BootstapContext<Noise> ctx, HolderGetter<Noise> noise, Seed seed) {
		Noise hillSelector = registerAndWrap(ctx, DALES_HILL_SELECTOR, Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0));
		Noise hills1 = registerAndWrap(ctx, DALES_HILL_1, Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75));
		Noise hills2 = registerAndWrap(ctx, DALES_HILL_2, Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25));
		Noise combined = hillSelector.blend(hills1, hills2, 0.4, 0.75);
		int warpSeed = seed.next();
		
		Noise hills3 = combined.pow(1.125).warp(warpSeed, 300, 1, 100.0);
		Noise variance = hills3.scale(0.4);
	
		Noise erosion2 = wrapNoise(noise, EROSION_2);
		Noise erosion4 = wrapNoise(noise, EROSION_4);
		
		ctx.register(DALES_VARIANCE, variance);
		ctx.register(DALES_EROSION, hillSelector.pow(1.125).warp(warpSeed, 300, 1, 100.0).threshold(0.1D, hills1.threshold(0.4, erosion4, erosion2), erosion4));// hillSelector.warp(warpSeed, 300, 1, 100.0).threshold(0.5D, hills1.threshold(0.45D, erosion4, erosion2), erosion4));
		ctx.register(DALES_RIDGE, wrapNoise(noise, MID_SLICE));
	}

    private static Noise makeDeepOcean(int seed, float seaLevel) {
        Noise hills = Source.perlin(++seed, 150, 3).scale(seaLevel * 0.7).bias(Source.perlin(++seed, 200, 1).scale(seaLevel * 0.2f));
        Noise canyons = Source.perlin(++seed, 150, 4).powCurve(0.2).invert().scale(seaLevel * 0.7).bias(Source.perlin(++seed, 170, 1).scale(seaLevel * 0.15f));
        return Source.perlin(++seed, 500, 1).blend(hills, canyons, 0.6, 0.65).warp(++seed, 50, 2, 50.0);
    }
	
	private static Noise applyRegionOffset(float frequency, Noise warpX, Noise warpZ, float warpStrength, Noise region, Noise offsetX, Noise offsetZ, float offsetDistance) {
		return new ClimateRegionOffset(new ClimateRegionEdge(frequency, warpX, warpZ, warpStrength), new ClimateRegion(region, frequency, warpX, warpZ, warpStrength), offsetX, offsetZ, 0.4F, offsetDistance);
	}
	
	private static Noise registerAndWrap(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise noise) {
		return new HolderNoise(ctx.register(key, noise));
	}
	
	private static Noise wrapNoise(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
		return new HolderNoise(noise.getOrThrow(key));
	}
	
	private static ResourceKey<Noise> createKey(String string) {
		return ReTerraForged.resolve(RTFRegistries.NOISE, string);
	}

	private static ResourceKey<Noise> createStrataKey(String key) {
		return createKey("strata/" + key);
	}
	
	private static ResourceKey<Noise> createClimateKey(String key) {
		return createKey("climate/" + key);
	}
	
	private static ResourceKey<Noise> createContinentKey(String key) {
		return createKey("continent/" + key);
	}
	
	private static ResourceKey<Noise> createTerrainKey(String key) {
		return createKey("terrain/" + key);
	}
	
	private static ResourceKey<Noise> createFeatureKey(String key) {
		return createTerrainKey("feature/" + key);
	}
	
	private static ResourceKey<Noise> createErosionKey(String key) {
		return createTerrainKey("erosion/" + key);
	}
	
	private static ResourceKey<Noise> createRidgeKey(String key) {
		return createTerrainKey("ridge/" + key);
	}
}
