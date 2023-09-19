package raccoonman.reterraforged.common.worldgen.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.Blender;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Moisture;
import raccoonman.reterraforged.common.level.levelgen.noise.climate.Temperature;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper2;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper3;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.LandForm;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.Volcano;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionEdge;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionId;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionLerper;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionSelector;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.ClimateSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.Terrain;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.ControlPoints;

public final class RTFNoiseData {
	public static final ResourceKey<Noise> TEMPERATURE = createKey("temperature");
	public static final ResourceKey<Noise> MOISTURE = createKey("moisture");
	public static final ResourceKey<Noise> MOUNTAIN_SHAPE = createKey("mountain_shape");
	public static final ResourceKey<Noise> ROOT = createKey("root");
	
	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		Seed seed = new Seed(0);
		
		WorldSettings worldSettings = preset.world();
		ControlPoints controlPoints = worldSettings.controlPoints;
		TerrainSettings terrainSettings = preset.terrain();
		int yScale = preset.terrain().general.yScale;
		
		Seed regionSeed = seed.offset(789124);
		Seed regionWarpSeed = seed.offset(8934);
		int regionWarpScale = 400;
		int regionWarpStrength = 200;
        RegionConfig regionConfig = new RegionConfig(regionSeed.get(), terrainSettings.general.terrainRegionSize, Source.simplex(regionWarpSeed.next(), regionWarpScale, 1), Source.simplex(regionWarpSeed.next(), regionWarpScale, 1), regionWarpStrength);

        Domain regionWarp = Domain.warp(regionConfig.warpX, regionConfig.warpZ, Source.constant(regionConfig.warpStrength));
        float regionFrequency = 1.0F / regionConfig.scale;
        Noise regionId = makeRegionId(regionWarp, regionFrequency);
        
        Seed mountainSeed = seed.offset(terrainSettings.general.terrainSeedOffset);
        Noise mountainShapeBase = Source.cellEdge(mountainSeed.next(), 1000, EdgeFunction.DISTANCE_2_ADD).warp(mountainSeed.next(), 333, 2, 250.0);
        Noise mountainShape = register(ctx, MOUNTAIN_SHAPE, mountainShapeBase.curve(Interpolation.CURVE3).clamp(0.0, 0.9).map(0.0, 1.0));
        //TODO use mountain shape noise as ridge (or maybe erosion??) noise (very good idea) (i swear)
        float waterLevel = (float) worldSettings.properties.seaLevel / yScale;
		Noise base = Source.constant(waterLevel);
        Noise regionSelector = createRegionSelector(regionId, base, seed.offset(terrainSettings.general.terrainSeedOffset), regionConfig, worldSettings, terrainSettings);
        Noise regionBorders = makeLandForm(terrainSettings.steppe, base, makePlains(seed, terrainSettings));
        Noise regionLerper = new RegionLerper(makeRegionEdge(regionWarp, regionFrequency), regionBorders, regionSelector);
        
        Noise mountains = makeLandForm(terrainSettings.mountains, base, makeMountains(mountainSeed, terrainSettings));
		
        Noise continent = worldSettings.continent.continentType.create(worldSettings, seed);
        //TODO create climate here
        
        ClimateSettings climateSettings = preset.climate();
        int biomeSize = climateSettings.biomeShape.biomeSize;
        float tempScaler = (float)climateSettings.temperature.scale;
        float moistScaler = climateSettings.moisture.scale * 2.5f;
        float biomeFreq = 1.0f / biomeSize;
        float moistureSize = moistScaler * biomeSize;
        float temperatureSize = tempScaler * biomeSize;
        int moistScale = NoiseUtil.round(moistureSize * biomeFreq);
        int tempScale = NoiseUtil.round(temperatureSize * biomeFreq);
        int warpScale = climateSettings.biomeShape.biomeWarpScale;
        Noise warpX = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
        Noise warpZ = Source.simplex(seed.next(), warpScale, 2).bias(-0.5);
        Seed moistureSeed = seed.offset(climateSettings.moisture.seedOffset);
        Noise moisture = climateSettings.moisture.apply(new Moisture(Source.simplex(moistureSeed.next(), moistScale, 1).clamp(0.125, 0.875).map(0.0, 1.0), Source.constant(climateSettings.moisture.falloff)).shift(moistureSeed.next())).warp(moistureSeed.next(), Math.max(1, moistScale / 2), 1, moistScale / 4.0).warp(moistureSeed.next(), Math.max(1, moistScale / 6), 2, moistScale / 12.0);
        moisture = climate(moisture).freq(biomeFreq, biomeFreq).warp(warpX, warpZ, climateSettings.biomeShape.biomeWarpStrength);
        moisture = register(ctx, MOISTURE, moisture);
        
        Seed tempSeed = seed.offset(climateSettings.temperature.seedOffset);
        Noise temperature = climateSettings.temperature.apply(new Temperature(Source.constant(1.0f / tempScale), Source.constant(climateSettings.temperature.falloff))).warp(tempSeed.next(), tempScale * 4, 2, tempScale * 4).warp(tempSeed.next(), tempScale, 1, tempScale);
        temperature = climate(temperature).freq(biomeFreq, biomeFreq).warp(warpX, warpZ, climateSettings.biomeShape.biomeWarpStrength);
        temperature = register(ctx, TEMPERATURE, temperature);
        
        Noise land = new Blender(mountainShape, regionLerper, mountains, 0.3F, 0.8F, 0.575F, Interpolation.LINEAR);
        
        Noise deepOcean = makeDeepOcean(seed.next(), worldSettings, terrainSettings);
        Noise shallowOcean = Source.constant(((float) worldSettings.properties.seaLevel - 7) / yScale);
        
        Noise ocean = new ContinentLerper3(continent, deepOcean, shallowOcean, base, controlPoints.deepOcean, controlPoints.shallowOcean, controlPoints.coast, Interpolation.CURVE3);
        Noise root = new ContinentLerper2(continent, ocean, land, controlPoints.shallowOcean, controlPoints.inland, Interpolation.LINEAR);

        ctx.register(ROOT, land);
	}
	
	private static Noise climate(Noise noise) {
		return new Climate(noise, DistanceFunction.EUCLIDEAN);
	}
	
	private static Noise makeRegionId(Domain regionWarp, float frequency) {
		return new RegionId(DistanceFunction.NATURAL, 0.7F, regionWarp, frequency);
	}
	
	private static Noise makeRegionEdge(Domain regionWarp, float frequency) {
		return new RegionEdge(0.0F, 0.5F, EdgeFunction.DISTANCE_2_DIV, DistanceFunction.NATURAL, 0.7F, regionWarp, frequency);
	}
	
	private static Noise makeDeepOcean(int seed, WorldSettings worldSettings, TerrainSettings terrainSettings) {
        float waterLevel = (float) worldSettings.properties.seaLevel / terrainSettings.general.yScale;
        Noise hills = Source.perlin(++seed, 150, 3).scale(waterLevel * 0.7).bias(Source.perlin(++seed, 200, 1).scale(waterLevel * 0.2f));
        Noise canyons = Source.perlin(++seed, 150, 4).powCurve(0.2).invert().scale(waterLevel * 0.7).bias(Source.perlin(++seed, 170, 1).scale(waterLevel * 0.15f));
        return Source.perlin(++seed, 500, 1).blend(hills, canyons, 0.6, 0.65).warp(++seed, 50, 2, 50.0).freq(1.0F / terrainSettings.general.globalHorizontalScale, 1.0F / terrainSettings.general.globalHorizontalScale);
    }
	
	private static Noise makeSteppe(Seed seed, TerrainSettings settings) {
        int scaleH = Math.round(250.0F * settings.steppe.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 200.0);
        return module.scale(0.08).bias(-0.02).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
	}
	
	private static Noise makePlains(Seed seed, TerrainSettings settings) {
        final int scaleH = Math.round(250.0f * settings.plains.horizontalScale);
        final double erosionAmount = 0.45;
        final Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        final Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        final Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        final Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 256.0);
        return module.scale(0.15f * settings.general.globalVerticalScale).bias(-0.02).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
	
    private static Noise makeDales(Seed seed, TerrainSettings settings) {
    	Noise hills1 = Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75);
        Noise hills2 = Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25);
        Noise combined = Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
        Noise hills3 = combined.pow(1.125).warp(seed.next(), 300, 1, 100.0);
        return hills3.scale(0.4).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
    
    private static Noise makeHills1(Seed seed, TerrainSettings settings) {
        return Source.perlin(seed.next(), 200, 3).mul(Source.billow(seed.next(), 400, 3).alpha(0.5)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).scale(0.6f * settings.general.globalVerticalScale).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
    
    private static Noise makeHills2(Seed seed, TerrainSettings settings) {
        return Source.cubic(seed.next(), 128, 2).mul(Source.perlin(seed.next(), 32, 4).alpha(0.075)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).mul(Source.ridge(seed.next(), 512, 2).alpha(0.8)).scale(0.55f * settings.general.globalVerticalScale).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
    
    private static Noise makeTorridonian(Seed seed, TerrainSettings settings) {
    	Noise plains = Source.perlin(seed.next(), 100, 3).warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 20, 1, 40.0).scale(0.15);
    	Noise hills = Source.perlin(seed.next(), 150, 4).warp(seed.next(), 300, 1, 200.0).warp(seed.next(), 20, 2, 20.0).boost();
    	Noise module = Source.perlin(seed.next(), 200, 3).blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(seed.next(), 120, 1).scale(0.25), Source.perlin(seed.next(), 200, 1).scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
        return module.scale(0.5).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
    
    private static Noise makePlateau(Seed seed, TerrainSettings settings) {
    	Noise valley = Source.ridge(seed.next(), 500, 1).invert().warp(seed.next(), 100, 1, 150.0).warp(seed.next(), 20, 1, 15.0);
    	Noise top = Source.build(seed.next(), 150, 3).lacunarity(2.45).ridge().warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 40, 2, 20.0).scale(0.15).mul(valley.clamp(0.02, 0.1).map(0.0, 1.0));
    	Noise surface = Source.perlin(seed.next(), 20, 3).scale(0.05).warp(seed.next(), 40, 2, 20.0);
    	Noise module = valley.mul(Source.cubic(seed.next(), 500, 1).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
        return module.scale(0.475 * settings.general.globalVerticalScale).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
    
    private static Noise makeBadlands(Seed seed, TerrainSettings settings) {
        Noise mask = Source.build(seed.next(), 270, 3).perlin().clamp(0.35, 0.65).map(0.0, 1.0);
        Noise hills = Source.ridge(seed.next(), 275, 4).warp(seed.next(), 400, 2, 100.0).warp(seed.next(), 18, 1, 20.0).mul(mask);
        final double modulation = 0.4;
        final double alpha = 1.0 - modulation;
        Noise mod1 = hills.warp(seed.next(), 100, 1, 50.0).scale(modulation);
        Noise lowFreq = hills.steps(4, 0.6, 0.7).scale(alpha).add(mod1);
        Noise highFreq = hills.steps(10, 0.6, 0.7).scale(alpha).add(mod1);
        Noise detail = lowFreq.add(highFreq);
        Noise mod2 = hills.mul(Source.perlin(seed.next(), 200, 3).scale(modulation));
        Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
        Noise badlands = shape.mul(detail.alpha(0.5));
        return badlands.scale(0.55).bias(0.025).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }

	private static Noise makeVolcano(Seed seed, RegionConfig region, TerrainSettings settings) {
        float midpoint = 0.3F;
        float range = 0.3F;
        Noise heightNoise = Source.perlin(seed.next(), 2, 1).map(0.45, 0.65);
        Noise height = Source.cellNoise(region.seed, region.scale, heightNoise).warp(region.warpX, region.warpZ, region.warpStrength);
        Noise cone = Source.cellEdge(region.seed, region.scale, EdgeFunction.DISTANCE_2_DIV).invert().warp(region.warpX, region.warpZ, region.warpStrength).powCurve(11.0).clamp(0.475, 1.0).map(0.0, 1.0).grad(0.0, 0.5, 0.5).warp(seed.next(), 15, 2, 10.0).scale(height);
        Noise lowlands = Source.ridge(seed.next(), 150, 3).warp(seed.next(), 30, 1, 30.0).scale(0.1);
        float inversionPoint = 0.94F;
        float blendLower = midpoint - range / 2.0F;
        float blendUpper = blendLower + range;
        float blendRange = blendUpper - blendLower;
        return new Volcano(cone, height, lowlands, inversionPoint, blendLower, blendUpper, blendRange).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
	}
    
    private static Noise makeFancy(Seed seed, Noise module, TerrainSettings settings) {
        if (settings.general.fancyMountains) {
            final Domain warp = Domain.direction(Source.perlin(seed.next(), 10, 1), Source.constant(2.0));
            final Noise erosion = new Valley(module, 2, 0.65F, 128.0F, 0.15F, 3.1F, 0.8F, Valley.Mode.CONSTANT).shift(seed.next());
            return erosion.warp(warp);
        }
        return module;
    }
    
    private static Noise makeMountains(Seed seed, TerrainSettings settings) {
        final int scaleH = Math.round(410.0f * settings.mountains.horizontalScale);
        final Noise module = Source.build(seed.next(), scaleH, 4).gain(1.15).lacunarity(2.35).ridge().mul(Source.perlin(seed.next(), 24, 4).alpha(0.075)).warp(seed.next(), 350, 1, 150.0);
        return makeFancy(seed, module, settings).scale(0.7 * settings.general.globalVerticalScale).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
    
    private static Noise makeMountains2(Seed seed, TerrainSettings settings) {
        final Noise cell = Source.cellEdge(seed.next(), 360, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        final Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        final Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        final Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        return makeFancy(seed, mountains, settings).scale(0.645 * settings.general.globalVerticalScale).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }
    
    private static Noise makeMountains3(Seed seed, TerrainSettings settings) {
        final Noise cell = Source.cellEdge(seed.next(), 400, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        final Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        final Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        final Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        final Noise terraced = mountains.terrace(Source.perlin(seed.next(), 50, 1).scale(0.5), Source.perlin(seed.next(), 100, 1).clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.20000000298023224, 0.44999998807907104, 24, 1);
        return makeFancy(seed, terraced, settings).scale(0.645 * settings.general.globalVerticalScale).freq(1.0F / settings.general.globalHorizontalScale, 1.0F / settings.general.globalHorizontalScale);
    }

    private static Noise makeLandForm(Terrain settings, Noise base, Noise variance) {
    	return new LandForm(base.mul(Source.constant(settings.baseScale)), variance.mul(Source.constant(settings.verticalScale)));
    }
    
    private static RegionSelector.Region combine(Noise base, RegionSelector.Region tp1, RegionSelector.Region tp2, Seed seed, int scale) {
		Noise combinedVariance = Source.perlin(seed.next(), scale, 1).warp(seed.next(), scale / 2, 2, scale / 2.0F).blend(tp1.variance(), tp2.variance(), 0.5F, 0.25F);
		float weight = (tp1.weight() + tp2.weight()) / 2.0F;
		return new RegionSelector.Region(weight, new LandForm(base, combinedVariance));
	}
	
    private static RegionSelector.Region makeRegion(Terrain settings, Noise base, Noise variance) {
		return new RegionSelector.Region(settings.weight, makeLandForm(settings, base, variance));
	}
    
	private static Noise createRegionSelector(Noise regionId, Noise base, Seed seed, RegionConfig config, WorldSettings worldSettings, TerrainSettings terrainSettings) {
		List<RegionSelector.Region> regions = new ArrayList<>();
		regions.add(makeRegion(terrainSettings.steppe, base, makeSteppe(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.plains, base, makePlains(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.dales, base, makeDales(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.hills, base, makeHills1(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.hills, base, makeHills2(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.torridonian, base, makeTorridonian(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.plateau, base, makePlateau(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.badlands, base, makeBadlands(seed, terrainSettings)));
		regions = regions.stream().filter((entry) -> entry.weight() > 0.0F).toList();
		regions = combine(regions, (tp1, tp2) -> combine(base, tp1, tp2, seed, config.scale / 2));
		regions.add(makeRegion(terrainSettings.badlands, base, makeBadlands(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.mountains, base, makeMountains(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.mountains, base, makeMountains2(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.mountains, base, makeMountains3(seed, terrainSettings)));
		regions.add(makeRegion(terrainSettings.volcano, base, makeVolcano(seed, config, terrainSettings)));
        Collections.shuffle(regions, new Random(seed.next())); // FIXME: this random completely breaks compatibility with 1.16.5 because this has to match the level seed + seed offset
        return new RegionSelector(regionId, regions);
	}
	
	private static Noise getNoise(HolderGetter<Noise> getter, ResourceKey<Noise> key) {
		return new HolderNoise(getter.getOrThrow(key));
	}
	
	private static Noise register(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise value) {
		return new HolderNoise(ctx.register(key, value));
	}
	
	private static ResourceKey<Noise> createKey(String string) {
        return ResourceKey.create(RTFRegistries.NOISE, ReTerraForged.resolve(string));
    }
	    
	private static <T> List<T> combine(final List<T> input, final BiFunction<T, T, T> operator) {
		int length = input.size();
		for (int i = 1; i < input.size(); ++i) {
			length += input.size() - i;
		}
		final List<T> result = new ArrayList<T>(length);
		for (int j = 0; j < length; ++j) {
			result.add(null);
		}
		int j = 0;
		int k = input.size();
		while (j < input.size()) {
			T t1 = input.get(j);
			result.set(j, t1);
			for (int l = j + 1; l < input.size(); ++l, ++k) {
				T t2 = input.get(l);
				T t3 = operator.apply(t1, t2);
				result.set(k, t3);
			}
			++j;
		}
		return result;
	}

	private record RegionConfig(int seed, int scale, Noise warpX, Noise warpZ, double warpStrength) {
	}
}
