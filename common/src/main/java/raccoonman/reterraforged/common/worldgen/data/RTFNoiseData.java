package raccoonman.reterraforged.common.worldgen.data;

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

	public static final ResourceKey<Noise> COAST = createFeatureKey("coast");
	public static final ResourceKey<Noise> SHALLOW_OCEAN = createFeatureKey("shallow_ocean");
	public static final ResourceKey<Noise> DEEP_OCEAN = createFeatureKey("deep_ocean");
	
	public static final ResourceKey<Noise> LAND = createTerrainKey("land");
	public static final ResourceKey<Noise> OCEANS = createTerrainKey("oceans");
	public static final ResourceKey<Noise> HEIGHT = createTerrainKey("height");
	
	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		Seed seed = new Seed(0);
		
		WorldSettings world = preset.world();
		ClimateSettings climate = preset.climate();
		TerrainSettings terrain = preset.terrain();
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
		if(miscellaneous.strataDecorator) {
			Seed strataSeed = seed.split();
			
	        ctx.register(STRATA_SELECTOR, Source.cell(strataSeed.next(), miscellaneous.strataRegionSize)
	        	.warp(seed.next(), miscellaneous.strataRegionSize / 4, 2, miscellaneous.strataRegionSize / 2D)
	        	.warp(seed.next(), 15, 2, 30));
	        ctx.register(STRATA_DEPTH, Source.perlin(strataSeed.next(), 128, 3));
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
    
        ControlPoints controlPoints = world.controlPoints;
        float seaLevel = world.properties.seaLevel;
        float yScale = terrain.general.yScale;
        float waterY = seaLevel - 1.0F;
        float water = waterY / yScale;
        
        Noise coast = registerAndWrap(ctx, COAST, Source.constant(water));
        Noise shallowOcean = registerAndWrap(ctx, SHALLOW_OCEAN, coast.sub(Source.constant(7.0F / yScale)));
        Noise deepOcean = registerAndWrap(ctx, DEEP_OCEAN, makeDeepOcean(seed.next(), water));
        
        Noise land = registerAndWrap(ctx, LAND, Source.constant(63.0D / 256.0D).add(makeDales(seed)));
        Noise oceans = registerAndWrap(ctx, OCEANS, new ContinentLerper3(continent, deepOcean, shallowOcean, coast, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast, Interpolation.CURVE3));
        ctx.register(HEIGHT, Source.constant(80.0D / 256.0D).add(makeDales(seed)));// new ContinentLerper2(continent, oceans, land, controlPoints.shallowOcean, controlPoints.inland, Interpolation.LINEAR));
	}
	
	private static Noise makePlateau(Seed seed) {
		Noise valley = Source.ridge(seed.next(), 500, 1).invert().warp(seed.next(), 100, 1, 150.0).warp(seed.next(), 20, 1, 15.0);
		Noise top = Source.build(seed.next(), 150, 3).lacunarity(2.45).ridge().warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 40, 2, 20.0).scale(0.15).mul(valley.clamp(0.02, 0.1).map(0.0, 1.0));
		Noise surface = Source.perlin(seed.next(), 20, 3).scale(0.05).warp(seed.next(), 40, 2, 20.0);
		Noise module = valley.mul(Source.cubic(seed.next(), 500, 1).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
        return module.scale(0.475);
    }
	
	public static Noise makeDales(final Seed seed) {
		Noise hills1 = Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75);
		Noise hills2 = Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25);
		Noise combined = Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
		Noise hills3 = combined.pow(1.125).warp(seed.next(), 300, 1, 100.0);
		return hills3.scale(0.4);
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
	
	private static ResourceKey<Noise> createKey(String string) {
		return ReTerraForged.resolve(RTFRegistries.NOISE, string);
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
	
	private static ResourceKey<Noise> createStrataKey(String key) {
		return createKey("strata/" + key);
	}
}
