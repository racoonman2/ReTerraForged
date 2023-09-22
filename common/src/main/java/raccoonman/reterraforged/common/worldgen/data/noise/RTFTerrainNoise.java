package raccoonman.reterraforged.common.worldgen.data.noise;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Valley;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper2;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper3;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionBlender;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionBorder;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionCell;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionLerper;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionSelector;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.RegionSelector.Region;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.Volcano;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.General;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings.Terrain;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

//TODO mountains2 and 3 don't take scaling into account (1.16 is like this too!)
public final class RTFTerrainNoise {
	public static final ResourceKey<Noise> REGION_WARP_X = createKey("region_warp_x");
	public static final ResourceKey<Noise> REGION_WARP_Z = createKey("region_warp_z");
	public static final ResourceKey<Noise> REGION_BORDER = createKey("region_border");
	public static final ResourceKey<Noise> REGION_CELL = createKey("region_cell");
	public static final ResourceKey<Noise> REGION = createKey("region");
	public static final ResourceKey<Noise> REGION_LERPER = createKey("region_lerper");
	
	public static final ResourceKey<Noise> GROUND = createFeatureKey("ground");
	public static final ResourceKey<Noise> STEPPE = createFeatureKey("steppe");
	public static final ResourceKey<Noise> PLAINS = createFeatureKey("plains");
	public static final ResourceKey<Noise> DALES = createFeatureKey("dales");
	public static final ResourceKey<Noise> HILLS_1 = createFeatureKey("hills_1");
	public static final ResourceKey<Noise> HILLS_2 = createFeatureKey("hills_2");
	public static final ResourceKey<Noise> TORRIDONIAN = createFeatureKey("torridonian");
	public static final ResourceKey<Noise> PLATEAU = createFeatureKey("plateau");
	public static final ResourceKey<Noise> BADLANDS = createFeatureKey("badlands");
	public static final ResourceKey<Noise> MOUNTAINS = createFeatureKey("mountains");
	public static final ResourceKey<Noise> MOUNTAINS_2 = createFeatureKey("mountains_2");
	public static final ResourceKey<Noise> MOUNTAINS_3 = createFeatureKey("mountains_3");
	public static final ResourceKey<Noise> VOLCANO = createFeatureKey("volcano");
	public static final ResourceKey<Noise> MOUNTAIN_SHAPE = createFeatureKey("mountain_shape");
	public static final ResourceKey<Noise> BORDER = createFeatureKey("border");
	public static final ResourceKey<Noise> MOUNTAIN_CHAIN = createFeatureKey("mountain_chain");
	public static final ResourceKey<Noise> COAST = createFeatureKey("coast");
	public static final ResourceKey<Noise> SHALLOW_OCEAN = createFeatureKey("shallow_ocean");
	public static final ResourceKey<Noise> DEEP_OCEAN = createFeatureKey("deep_ocean");

	public static final ResourceKey<Noise> LAND = createKey("land");
	public static final ResourceKey<Noise> OCEANS = createKey("oceans");
	public static final ResourceKey<Noise> ROOT = createKey("root");
	
	public static void bootstrap(BootstapContext<Noise> ctx, WorldSettings worldSettings, TerrainSettings terrainSettings, Seed seed) {
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		General general = terrainSettings.general;
		
		Seed regionSeed = seed.offset(789124);
		Seed regionWarpSeed = seed.offset(8934);
		
		int regionWarpScale = 400;
		int regionWarpStrength = 200;
		Noise regionWarpX = registerAndWrap(ctx, REGION_WARP_X, Source.simplex(regionWarpSeed.next(), regionWarpScale, 1));
		Noise regionWarpZ = registerAndWrap(ctx, REGION_WARP_Z, Source.simplex(regionWarpSeed.next(), regionWarpScale, 1));
		//TODO we need to apply this
		float terrainFrequency = 1.0F / general.globalHorizontalScale;

		float regionFrequency = 1.0F / general.terrainRegionSize;
		int terrainSeed = seed.offset(7).get();
		Domain regionDomain = Domain.warp(regionWarpX, regionWarpZ, Source.constant(regionWarpStrength));
		Noise regionBorder = registerAndWrap(ctx, REGION_BORDER, new RegionBorder(regionDomain, regionFrequency, 0.0F, 0.5F).shift(terrainSeed));
		Noise regionCell = registerAndWrap(ctx, REGION_CELL, new RegionCell(regionDomain, regionFrequency).shift(terrainSeed));

		Seed featureSeed = seed.offset(general.terrainSeedOffset);
		Noise ground = registerAndWrap(ctx, GROUND, Source.constant((float) worldSettings.properties.seaLevel / (float) general.yScale));
		Noise steppe = registerAndWrap(ctx, STEPPE, makeSteppe(terrainSettings.steppe, featureSeed));
		Noise plains = registerAndWrap(ctx, PLAINS, makePlains(general, terrainSettings.plains, featureSeed));
		Noise dales = registerAndWrap(ctx, DALES, makeDales(featureSeed));
		Noise hills1 = registerAndWrap(ctx, HILLS_1, makeHills1(general, featureSeed));
		Noise hills2 = registerAndWrap(ctx, HILLS_2, makeHills2(general, featureSeed));
		Noise torridonian = registerAndWrap(ctx, TORRIDONIAN, makeTorridonian(general, featureSeed));
		Noise plateau = registerAndWrap(ctx, PLATEAU, makePlateau(general, featureSeed));
		Noise badlands = registerAndWrap(ctx, BADLANDS, makeBadlands(featureSeed));
		Noise mountains = registerAndWrap(ctx, MOUNTAINS, makeMountains(general, terrainSettings.mountains, featureSeed));
		Noise mountains2 = registerAndWrap(ctx, MOUNTAINS_2, makeMountains2(general, featureSeed));
		Noise mountains3 = registerAndWrap(ctx, MOUNTAINS_3, makeMountains3(general, featureSeed));
		Noise volcano = registerAndWrap(ctx, VOLCANO, makeVolcano(general, terrainSettings.volcano, regionSeed, featureSeed, regionWarpX, regionWarpZ, regionWarpStrength));
		Noise borders = registerAndWrap(ctx, BORDER, scaleFeature(ground, terrainSettings.steppe, makePlains(general, terrainSettings.plains, featureSeed)));
		// TODO allow the mountain chain type to be chosen
        Seed mountainSeed = seed.offset(general.terrainSeedOffset);
        Noise mountainShapeBase = Source.cellEdge(mountainSeed.next(), 1000, EdgeFunction.DISTANCE_2_ADD).warp(mountainSeed.next(), 333, 2, 250.0);
        Noise mountainShape = registerAndWrap(ctx, MOUNTAIN_SHAPE, mountainShapeBase.curve(Interpolation.CURVE3).clamp(0.0, 0.9).map(0.0, 1.0));
		Noise mountainChain = registerAndWrap(ctx, MOUNTAIN_CHAIN, scaleFeature(ground, terrainSettings.mountains, makeMountains(general, terrainSettings.mountains, mountainSeed)));
		float waterLevel = (float) (worldSettings.properties.seaLevel - 1) / (float) general.yScale;
		Noise coast = registerAndWrap(ctx, COAST, Source.constant(waterLevel));
		Noise shallowOcean = registerAndWrap(ctx, SHALLOW_OCEAN, Source.constant((float) (worldSettings.properties.seaLevel - 8) / (float) general.yScale));
		Noise deepOcean = registerAndWrap(ctx, DEEP_OCEAN, makeDeepOcean(seed.next(), waterLevel));
		
		Noise region = registerAndWrap(ctx, REGION, makeRegionSelector(seed, general, terrainSettings, regionCell, ground, steppe, plains, dales, hills1, hills2, torridonian, plateau, badlands, mountains, mountains2, mountains3, volcano));
		Noise regionLerper = registerAndWrap(ctx, REGION_LERPER, new RegionLerper(regionBorder, borders, region));
		Noise land = registerAndWrap(ctx, LAND, new RegionBlender(mountainShape, regionLerper, mountainChain, 0.3F, 0.8F));
		
		Noise continent = getNoise(noise, worldSettings.continent.continentType);
		Noise oceans = registerAndWrap(ctx, OCEANS, new ContinentLerper3(continent, deepOcean, shallowOcean, coast, Interpolation.CURVE3, worldSettings.controlPoints.deepOcean, worldSettings.controlPoints.shallowOcean, worldSettings.controlPoints.coast));

		ctx.register(ROOT, new ContinentLerper2(continent, oceans, land, worldSettings.controlPoints.shallowOcean, worldSettings.controlPoints.inland, Interpolation.LINEAR));
	}

	// TODO all the make/Feature/ functions should return this
	// we can stitch together an erosion/ridge map from that
	private record TerrainFeature(Noise variance, Noise erosion, Noise ridge) {
	}
	
	private static Noise makeSteppe(Terrain settings, Seed seed) {
        int scaleH = Math.round(250.0f * settings.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.0).perlin();
        Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 200.0);
        return module.scale(0.08).bias(-0.02);
	}
	
	private static Noise makePlains(General general, Terrain terrain, Seed seed) {
        int scaleH = Math.round(250.0f * terrain.horizontalScale);
        double erosionAmount = 0.45;
        Noise erosion = Source.build(seed.next(), scaleH * 2, 3).lacunarity(3.75).perlin().alpha(erosionAmount);
        Noise warpX = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise warpY = Source.build(seed.next(), scaleH / 4, 3).lacunarity(3.5).perlin();
        Noise module = Source.perlin(seed.next(), scaleH, 1).mul(erosion).warp(warpX, warpY, Source.constant(scaleH / 4.0f)).warp(seed.next(), 256, 1, 256.0);
        return module.scale(0.15f * general.globalVerticalScale).bias(-0.02);
    }
    
    private static Noise makeDales(Seed seed) {
        Noise hills1 = Source.build(seed.next(), 300, 4).gain(0.8).lacunarity(4.0).billow().powCurve(0.5).scale(0.75);
        Noise hills2 = Source.build(seed.next(), 350, 3).gain(0.8).lacunarity(4.0).billow().pow(1.25);
        Noise combined = Source.perlin(seed.next(), 400, 1).clamp(0.3, 0.6).map(0.0, 1.0).blend(hills1, hills2, 0.4, 0.75);
        Noise hills3 = combined.pow(1.125).warp(seed.next(), 300, 1, 100.0);
        return hills3.scale(0.4);
    }
    
    private static Noise makeHills1(General general, Seed seed) {
        return Source.perlin(seed.next(), 200, 3).mul(Source.billow(seed.next(), 400, 3).alpha(0.5)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).scale(0.6f * general.globalVerticalScale);
    }
    
    private static Noise makeHills2(General general, Seed seed) {
        return Source.cubic(seed.next(), 128, 2).mul(Source.perlin(seed.next(), 32, 4).alpha(0.075)).warp(seed.next(), 30, 3, 20.0).warp(seed.next(), 400, 3, 200.0).mul(Source.ridge(seed.next(), 512, 2).alpha(0.8)).scale(0.55f * general.globalVerticalScale);
    }
    
    private static Noise makeTorridonian(General general, Seed seed) {
    	Noise plains = Source.perlin(seed.next(), 100, 3).warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 20, 1, 40.0).scale(0.15);
    	Noise hills = Source.perlin(seed.next(), 150, 4).warp(seed.next(), 300, 1, 200.0).warp(seed.next(), 20, 2, 20.0).boost();
    	Noise module = Source.perlin(seed.next(), 200, 3).blend(plains, hills, 0.6, 0.6).terrace(Source.perlin(seed.next(), 120, 1).scale(0.25), Source.perlin(seed.next(), 200, 1).scale(0.5).bias(0.5), Source.constant(0.5), 0.0, 0.3, 6, 1).boost();
        return module.scale(0.5);
    }
    
    private static Noise makePlateau(General general, Seed seed) {
    	Noise valley = Source.ridge(seed.next(), 500, 1).invert().warp(seed.next(), 100, 1, 150.0).warp(seed.next(), 20, 1, 15.0);
    	Noise top = Source.build(seed.next(), 150, 3).lacunarity(2.45).ridge().warp(seed.next(), 300, 1, 150.0).warp(seed.next(), 40, 2, 20.0).scale(0.15).mul(valley.clamp(0.02, 0.1).map(0.0, 1.0));
    	Noise surface = Source.perlin(seed.next(), 20, 3).scale(0.05).warp(seed.next(), 40, 2, 20.0);
    	Noise module = valley.mul(Source.cubic(seed.next(), 500, 1).scale(0.6).bias(0.3)).add(top).terrace(Source.constant(0.9), Source.constant(0.15), Source.constant(0.35), 4, 0.4).add(surface);
        return module.scale(0.475 * general.globalVerticalScale);
    }
	
    private static Noise makeBadlands(Seed seed) {
    	Noise mask = Source.build(seed.next(), 270, 3).perlin().clamp(0.35, 0.65).map(0.0, 1.0);
    	Noise hills = Source.ridge(seed.next(), 275, 4).warp(seed.next(), 400, 2, 100.0).warp(seed.next(), 18, 1, 20.0).mul(mask);
        double modulation = 0.4;
        double alpha = 1.0 - modulation;
        Noise mod1 = hills.warp(seed.next(), 100, 1, 50.0).scale(modulation);
        Noise lowFreq = hills.steps(4, 0.6, 0.7).scale(alpha).add(mod1);
        Noise highFreq = hills.steps(10, 0.6, 0.7).scale(alpha).add(mod1);
        Noise detail = lowFreq.add(highFreq);
        Noise mod2 = hills.mul(Source.perlin(seed.next(), 200, 3).scale(modulation));
        Noise shape = hills.steps(4, 0.65, 0.75, Interpolation.CURVE3).scale(alpha).add(mod2).scale(alpha);
        Noise badlands = shape.mul(detail.alpha(0.5));
        return badlands.scale(0.55).bias(0.025);
    }
    
    private static Noise makeMountains(General general, Terrain terrain, Seed seed) {
        int scaleH = Math.round(410.0f * terrain.horizontalScale);
        Noise module = Source.build(seed.next(), scaleH, 4).gain(1.15).lacunarity(2.35).ridge().mul(Source.perlin(seed.next(), 24, 4).alpha(0.075)).warp(seed.next(), 350, 1, 150.0);
        return makeFancy(general, seed, module).scale(0.7 * general.globalVerticalScale);
    }
    
    private static Noise makeMountains2(General general, Seed seed) {
        Noise cell = Source.cellEdge(seed.next(), 360, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        return makeFancy(general, seed, mountains).scale(0.645 * general.globalVerticalScale);
    }
    
    private static Noise makeMountains3(General general, Seed seed) {
        Noise cell = Source.cellEdge(seed.next(), 400, EdgeFunction.DISTANCE_2).scale(1.2).clamp(0.0, 1.0).warp(seed.next(), 200, 2, 100.0);
        Noise blur = Source.perlin(seed.next(), 10, 1).alpha(0.025);
        Noise surface = Source.ridge(seed.next(), 125, 4).alpha(0.37);
        Noise mountains = cell.clamp(0.0, 1.0).mul(blur).mul(surface).pow(1.1);
        Noise terraced = mountains.terrace(Source.perlin(seed.next(), 50, 1).scale(0.5), Source.perlin(seed.next(), 100, 1).clamp(0.5, 0.95).map(0.0, 1.0), Source.constant(0.45), 0.20000000298023224, 0.44999998807907104, 24, 1);
        return makeFancy(general, seed, terraced).scale(0.645 * general.globalVerticalScale);
    }
    
    private static Noise makeFancy(General general, Seed seed, Noise noise) {
        if (general.fancyMountains) {
            Domain warp = Domain.direction(Source.perlin(seed.next(), 10, 1), Source.constant(2.0));
            return new Valley(noise, 2, 0.65F, 128.0F, 0.15F, 3.1F, 0.8F, Valley.Mode.CONSTANT).shift(seed.next()).warp(warp);
        }
        return noise;
    }
   
    private static Noise makeVolcano(General general, Terrain terrain, Seed regionSeed, Seed featureSeed, Noise warpX, Noise warpZ, float warpStrength) {
        float midpoint = 0.3f;
        float range = 0.3f;
        Noise heightNoise = Source.perlin(featureSeed.next(), 2, 1).map(0.45, 0.65);
        Noise height = Source.cellNoise(regionSeed.get(), general.terrainRegionSize, heightNoise).warp(warpX, warpZ, warpStrength);
        Noise cone = Source.cellEdge(regionSeed.get(), general.terrainRegionSize, EdgeFunction.DISTANCE_2_DIV).invert().warp(warpX, warpZ, warpStrength).powCurve(11.0).clamp(0.475, 1.0).map(0.0, 1.0).grad(0.0, 0.5, 0.5).warp(featureSeed.next(), 15, 2, 10.0).scale(height);
        Noise lowlands = Source.ridge(featureSeed.next(), 150, 3).warp(featureSeed.next(), 30, 1, 30.0).scale(0.1);
        float inversionPoint = 0.94f;
        float blendLower = midpoint - range / 2.0f;
        float blendUpper = blendLower + range;
    	return new Volcano(cone, height, lowlands, inversionPoint, blendLower, blendUpper);
    }

    private static Noise makeDeepOcean(int seed, float seaLevel) {
    	Noise hills = Source.perlin(++seed, 150, 3).scale(seaLevel * 0.7).bias(Source.perlin(++seed, 200, 1).scale(seaLevel * 0.2f));
    	Noise canyons = Source.perlin(++seed, 150, 4).powCurve(0.2).invert().scale(seaLevel * 0.7).bias(Source.perlin(++seed, 170, 1).scale(seaLevel * 0.15f));
        return Source.perlin(++seed, 500, 1).blend(hills, canyons, 0.6, 0.65).warp(++seed, 50, 2, 50.0);
    }
    
    private static Noise scaleFeature(Noise ground, Terrain settings, Noise feature) {
    	return ground.mul(Source.constant(settings.baseScale)).add(feature).mul(Source.constant(settings.verticalScale));
    }
    //TODO we still can't use this for erosion/ridge because it applies the scaling
    //maybe we should apply the scaling to erosion/ridge??
    private static Noise makeRegionSelector(Seed seed, General general, TerrainSettings terrain, Noise regionCell, Noise ground, Noise steppe, Noise plains, Noise dales, Noise hills1, Noise hills2, Noise torridonian, Noise plateau, Noise badlands, Noise mountains, Noise mountains2, Noise mountains3, Noise volcano) {
		List<Region> regions = new ArrayList<>();
		regions.add(makeRegion(ground, steppe, terrain.steppe));
		regions.add(makeRegion(ground, plains, terrain.plains));
		regions.add(makeRegion(ground, hills1, terrain.hills));
		regions.add(makeRegion(ground, hills2, terrain.hills));
		regions.add(makeRegion(ground, torridonian, terrain.torridonian));
		regions.add(makeRegion(ground, plateau, terrain.plateau));
		regions.add(makeRegion(ground, badlands, terrain.badlands));
		regions = blendRegions(regions, ground, seed, general.terrainRegionSize / 2);
		regions.add(makeRegion(ground, badlands, terrain.badlands));
		regions.add(makeRegion(ground, mountains, terrain.mountains));
		regions.add(makeRegion(ground, mountains2, terrain.mountains));
		regions.add(makeRegion(ground, mountains3, terrain.mountains));
		regions.add(makeRegion(ground, volcano, terrain.volcano));
        Collections.shuffle(regions, new Random(seed.next()));
		return new RegionSelector(regionCell, regions);
	}
	
	private static List<Region> blendRegions(List<Region> input, Noise base, Seed seed, int scale) {
        int length = input.size();
        for (int i = 1; i < input.size(); ++i) {
            length += input.size() - i;
        }
        List<Region> result = new ArrayList<>(length);
        for (int j = 0; j < length; ++j) {
            result.add(null);
        }
        int j = 0;
        int k = input.size();
        while (j < input.size()) {
        	Region t1 = input.get(j);
            result.set(j, t1);
            for (int l = j + 1; l < input.size(); ++l, ++k) {
            	Region t2 = input.get(l);
                Region t3 = blend(t1, t2, base, seed, scale);
                result.set(k, t3);
            }
            ++j;
        }
        return result;
	}
	
	private static Region blend(Region region1, Region region2, Noise base, Seed seed, int scale) {
		Noise combined = Source.perlin(seed.next(), scale, 1).warp(seed.next(), scale / 2, 2, scale / 2.0).blend(region1.variance(), region2.variance(), 0.5, 0.25);// removing the clamp here shouldn't do anything right?? .clamp(0.0, 1.0);
		float weight = (region1.weight() + region2.weight()) / 2.0f;
		return new Region(weight, base, combined, 1.0F, 1.0F);
	}
	
	private static Region makeRegion(Noise base, Noise variance, Terrain settings) {
		return new Region(settings.weight, base, variance, settings.baseScale, settings.verticalScale);
	}
	
	private static Noise registerAndWrap(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise noise) {
		return new HolderNoise(ctx.register(key, noise));
	}
	
	private static Noise getNoise(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
		return new HolderNoise(noise.getOrThrow(key));
	}
	
	private static ResourceKey<Noise> createKey(String string) {
		return RTFNoiseData.createKey("terrain/" + string);
	}
	
	private static ResourceKey<Noise> createFeatureKey(String string) {
		return createKey("feature/" + string);
	}
}
