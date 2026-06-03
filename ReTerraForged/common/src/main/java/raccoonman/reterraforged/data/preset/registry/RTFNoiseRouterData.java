package raccoonman.reterraforged.data.preset.registry;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.pages.CaveOptions;
import raccoonman.reterraforged.preset.pages.WorldOptions;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.registry.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.ContinentPoints;
import raccoonman.reterraforged.world.worldgen.ErosionPoints;
import raccoonman.reterraforged.world.worldgen.HumidityPoints;
import raccoonman.reterraforged.world.worldgen.Seed;
import raccoonman.reterraforged.world.worldgen.TemperaturePoints;
import raccoonman.reterraforged.world.worldgen.WeirdnessPoints;
import raccoonman.reterraforged.world.worldgen.densityfunction.BlendFunction;
import raccoonman.reterraforged.world.worldgen.densityfunction.Cache2d;
import raccoonman.reterraforged.world.worldgen.densityfunction.RTFDensityFunctions;
import raccoonman.reterraforged.world.worldgen.densityfunction.RegionFunction;
import raccoonman.reterraforged.world.worldgen.densityfunction.SplineFunction;
import raccoonman.reterraforged.world.worldgen.densityfunction.WeightedFunction;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;
import raccoonman.reterraforged.world.worldgen.noise.Noise;
import raccoonman.reterraforged.world.worldgen.noise.warp.Warp;

public class RTFNoiseRouterData {
	private static final int BIOME_CACHE_SIZE = QuartPos.toBlock(4);
	private static final int TERRAIN_CACHE_SIZE = QuartPos.toBlock(5);
	public static final int Y_SCALER = 128;
	public static final float Y_UNIT = 1.0F / Y_SCALER;
	
    public static void bootstrap(Preset preset, BootstrapContext<DensityFunction> ctx) {
    	HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
    	HolderGetter<Warp> warps = ctx.lookup(RTFRegistries.WARP);
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
        
    	DensityFunction y = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.Y);
    	Holder<DensityFunction> continents = getFunction(densityFunctions, NoiseRouterKeys.CONTINENTS);
    	Holder<DensityFunction> erosion = getFunction(densityFunctions, NoiseRouterKeys.EROSION);
    	Holder<DensityFunction> ridges = getFunction(densityFunctions, NoiseRouterKeys.RIDGES);
    	Holder<DensityFunction> ridgesFolded = getFunction(densityFunctions, NoiseRouterKeys.RIDGES_FOLDED);
    	
    	DensityFunction rawContinents = registerAndWrap(ctx, NoiseRouterKeys.RAW_CONTINENTS, rawContinents(preset, ctx));
    	register(ctx, NoiseRouterKeys.CONTINENTS, continents(preset, ctx, rawContinents));
    	register(ctx, NoiseRouterKeys.EROSION, erosion(preset));
    	DensityFunction ridgesWrapped = registerAndWrap(ctx, NoiseRouterKeys.RIDGES, ridges());
    	register(ctx, NoiseRouterKeys.RIDGES_FOLDED, ridgesFolded(ridgesWrapped));
    	DensityFunction offset = registerAndWrap(ctx, NoiseRouterKeys.OFFSET, offset(preset, ctx));
    	DensityFunction depth = registerAndWrap(ctx, NoiseRouterKeys.DEPTH, depth(preset, offset));
    	DensityFunction factor = registerAndWrap(ctx, NoiseRouterKeys.FACTOR, factor(continents, erosion, ridges, ridgesFolded));
    	DensityFunction slopedCheese = registerAndWrap(ctx, NoiseRouterKeys.SLOPED_CHEESE, slopedCheese(factor, depth));
    	DensityFunction noodle = registerAndWrap(ctx, NoiseRouterKeys.NOODLE, noodle(preset, noiseParams, y));
    	DensityFunction spaghetti2dThicknessModulator = registerAndWrap(ctx, NoiseRouterKeys.SPAGHETTI_2D_THICKNESS_MODULATOR, spaghetti2dThicknessModulator(noiseParams));
    	DensityFunction spaghettiRoughnessFunction = registerAndWrap(ctx, NoiseRouterKeys.SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noiseParams));
    	DensityFunction spaghetti2d = registerAndWrap(ctx, NoiseRouterKeys.SPAGHETTI_2D, spaghetti2D(preset, noiseParams, spaghetti2dThicknessModulator));
    	DensityFunction pillars = registerAndWrap(ctx, NoiseRouterKeys.PILLARS, pillars(noiseParams));
    	DensityFunction entrances = registerAndWrap(ctx, NoiseRouterKeys.ENTRANCES, entrances(preset, noiseParams, spaghettiRoughnessFunction));
    	DensityFunction underground = registerAndWrap(ctx, NoiseRouterKeys.UNDERGROUND, underground(noiseParams, slopedCheese, entrances, spaghetti2d, spaghettiRoughnessFunction, pillars));
    	register(ctx, NoiseRouterKeys.FINAL_DENSITY, finalDensity(preset, ctx, slopedCheese, underground, entrances, noodle));
    	
    	int minVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
    	int maxVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
    	register(ctx, NoiseRouterKeys.ORE_VEININESS, oreVeininess(ctx, minVeinY, maxVeinY));
    	register(ctx, NoiseRouterKeys.ORE_VEIN, oreVein(ctx, minVeinY, maxVeinY));
    	register(ctx, NoiseRouterKeys.ORE_GAP, oreGap(ctx));
    	register(ctx, NoiseRouterKeys.AQUIFER_BARRIER, aquiferBarrier(noiseParams));
    	register(ctx, NoiseRouterKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS, aquiferFluidLevelFloodedness(noiseParams));
    	register(ctx, NoiseRouterKeys.AQUIFER_FLUID_LEVEL_SPREAD, aquiferFluidLevelSpread(noiseParams));
    	register(ctx, NoiseRouterKeys.AQUIFER_LAVA, aquiferLava(noiseParams));
    	register(ctx, NoiseRouterKeys.TEMPERATURE, temperature(preset, noise));
    	register(ctx, NoiseRouterKeys.VEGETATION, humidity(preset, noise));
    	register(ctx, NoiseRouterKeys.PRELIMINARY_SURFACE_LEVEL, preliminarySurfaceLevel(preset, factor, offset));
//    	register(ctx, NoiseRouterKeys.BIOME_REGION, biomeRegion(noise));
    	register(ctx, NoiseRouterKeys.TERRAIN_HEIGHT, terrainHeight(noise, warps));
    }

    public static NoiseRouter overworld(HolderGetter<DensityFunction> densityFunctions) {
    	DensityFunction aquiferBarrier = getWrappedFunction(densityFunctions, NoiseRouterKeys.AQUIFER_BARRIER);
        DensityFunction aquiferFluidLevelFloodedness = getWrappedFunction(densityFunctions, NoiseRouterKeys.AQUIFER_FLUID_LEVEL_FLOODEDNESS);
        DensityFunction aquiferFluidLevelSpread = getWrappedFunction(densityFunctions, NoiseRouterKeys.AQUIFER_FLUID_LEVEL_SPREAD);
        DensityFunction aquiferLava = getWrappedFunction(densityFunctions, NoiseRouterKeys.AQUIFER_LAVA);
        DensityFunction temperature = getWrappedFunction(densityFunctions, NoiseRouterKeys.TEMPERATURE);
        DensityFunction vegetation = getWrappedFunction(densityFunctions, NoiseRouterKeys.VEGETATION);
        DensityFunction continents = getWrappedFunction(densityFunctions, NoiseRouterKeys.CONTINENTS);
        DensityFunction erosion = getWrappedFunction(densityFunctions, NoiseRouterKeys.EROSION);
        DensityFunction depth = getWrappedFunction(densityFunctions, NoiseRouterKeys.DEPTH);
        DensityFunction ridges = getWrappedFunction(densityFunctions, NoiseRouterKeys.RIDGES);
        DensityFunction initialDensityWithoutJaggedness = getWrappedFunction(densityFunctions, NoiseRouterKeys.PRELIMINARY_SURFACE_LEVEL);
        DensityFunction finalDensity = getWrappedFunction(densityFunctions, NoiseRouterKeys.FINAL_DENSITY);
        DensityFunction oreVeininess = getWrappedFunction(densityFunctions, NoiseRouterKeys.ORE_VEININESS);
        DensityFunction oreVein = getWrappedFunction(densityFunctions, NoiseRouterKeys.ORE_VEIN);
        DensityFunction oreGap = getWrappedFunction(densityFunctions, NoiseRouterKeys.ORE_GAP);
        return new NoiseRouter(aquiferBarrier, aquiferFluidLevelFloodedness, aquiferFluidLevelSpread, aquiferLava, temperature, vegetation, continents, erosion, depth, ridges, initialDensityWithoutJaggedness, finalDensity, oreVeininess, oreVein, oreGap);
	}

    private static DensityFunction rawContinents(Preset preset, BootstrapContext<DensityFunction> ctx) {
//    	WorldOptions.ContinentType continentType = preset.getOption(WorldOptions.CONTINENT_TYPE);
//    	return Cache2d.makeGlobal(continentType.makeFunction(preset, ctx));
    	return DensityFunctions.constant(ContinentPoints.MID_INLAND + 0.1F);
    }
    
    private static DensityFunction continents(Preset preset, BootstrapContext<DensityFunction> ctx, DensityFunction fallback) {
    	return DensityFunctions.constant(ContinentPoints.MID_INLAND + 0.1F);//// layer(preset, ctx, CONTINENTS, BIOME_CACHE_SIZE, fallback);
    }
    
    private static DensityFunction erosion(Preset preset) {
		return DensityFunctions.constant(Mth.lerp(0.5F, ErosionPoints.LEVEL_4, ErosionPoints.LEVEL_5));
    }

	private static DensityFunction ridges() {
		return DensityFunctions.constant(-(WeirdnessPoints.HIGH_SLICE + 0.025D));
	}
	
	private static DensityFunction ridgesFolded(DensityFunction ridges) {
		return DensityFunctions.mul(
			DensityFunctions.add(
				DensityFunctions.add(ridges.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)
			),
			DensityFunctions.constant(-3.0)
		);
	}
    
    private static DensityFunction initialHeight(Preset preset, HolderGetter<Noise> noises, DensityFunction continentalness) {
		DensityFunction seaLevel = DensityFunctions.constant(preset.getOption(WorldOptions.SEA_LEVEL));
		DensityFunction deepOcean = DensityFunctions.constant(-60);
		DensityFunction ocean = DensityFunctions.constant(-30);

		SplineFunction.Builder spline = SplineFunction.builder(continentalness);
		spline.addPoint(ContinentPoints.OCEAN - 0.01F, blocks(seaLevel, deepOcean));
		spline.addPoint(ContinentPoints.OCEAN, blocks(seaLevel, ocean));
		spline.addPoint(ContinentPoints.COAST + 0.01F, blocks(seaLevel, -1.0D));
		spline.addPoint(ContinentPoints.NEAR_INLAND, blocks(seaLevel, 3.5D));
		spline.addPoint(ContinentPoints.MID_INLAND, blocks(seaLevel, 35.0D));
		spline.addPoint(ContinentPoints.FAR_INLAND, blocks(seaLevel, 150.0D));
		spline.addPoint(ContinentPoints.MAX, blocks(seaLevel, 500.0D));
		return spline.build();
	}

	private static DensityFunction offset(Preset preset, BootstrapContext<DensityFunction> ctx) {
		DensityFunction height = layer(preset, ctx, RTFLayers.TERRAIN_HEIGHT, TERRAIN_CACHE_SIZE, DensityFunctions.zero());
		DensityFunction offset = DensityFunctions.mul(DensityFunctions.constant(RTFNoiseRouterData.Y_UNIT), height);
        return DensityFunctions.add(DensityFunctions.constant(-1.0F), offset);
	}
	
	private static DensityFunction factor(Holder<DensityFunction> continents, Holder<DensityFunction> erosion, Holder<DensityFunction> ridges, Holder<DensityFunction> ridgesFolded) {
		DensityFunctions.Spline.Coordinate continentsCoord = new DensityFunctions.Spline.Coordinate(continents);
		DensityFunctions.Spline.Coordinate erosionCoord = new DensityFunctions.Spline.Coordinate(erosion);
		DensityFunctions.Spline.Coordinate ridgesCoord = new DensityFunctions.Spline.Coordinate(ridges);
		DensityFunctions.Spline.Coordinate ridgesFoldedCoord = new DensityFunctions.Spline.Coordinate(ridgesFolded);
		CubicSpline<DensityFunctions.Spline.Point, DensityFunctions.Spline.Coordinate> spline = TerrainProvider.overworldFactor(continentsCoord, erosionCoord, ridgesCoord, ridgesFoldedCoord, false);
		return DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.spline(spline)));
	}
	
	private static DensityFunction depth(Preset preset, DensityFunction offset) {
		return offsetToDepth(preset, offset);
	}

	private static DensityFunction slopedCheese(DensityFunction factor, DensityFunction depth) {
		return NoiseRouterData.noiseGradientDensity(factor, depth);
	}

//	private static DensityFunction biomeRegion(HolderGetter<Noise> noises) {
//		return RTFNoiseRouterData.noiseSampler(noises, RTFNoise.BIOME_REGION);
//	}

    private static DensityFunction underground(HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction slopedCheese, DensityFunction entrances, DensityFunction spaghetti2d, DensityFunction spaghettiRoughnessFunction, DensityFunction pillars) {
        DensityFunction caveLayerNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction caveLayer = DensityFunctions.mul(DensityFunctions.constant(4.0), caveLayerNoise.square());
        DensityFunction caveCheese = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction slopedCaves = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), caveCheese).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), slopedCheese)).clamp(0.0, 0.5));
        DensityFunction slopedCaveLayered = DensityFunctions.add(caveLayer, slopedCaves);
        DensityFunction underground = DensityFunctions.min(DensityFunctions.min(slopedCaveLayered, entrances), DensityFunctions.add(spaghetti2d, spaghettiRoughnessFunction));
        DensityFunction pillarRange = DensityFunctions.rangeChoice(pillars, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), pillars);
        return DensityFunctions.max(underground, pillarRange);
    }

    private static DensityFunction spaghetti2dThicknessModulator(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
    	return DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3));
    }
    
    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
    	DensityFunction roughness = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
		DensityFunction roughnessModulator = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
		return DensityFunctions.cacheOnce(DensityFunctions.mul(roughnessModulator, DensityFunctions.add(roughness.abs(), DensityFunctions.constant(-0.4))));
    }
    
    private static DensityFunction spaghetti2D(Preset preset, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction thicknessModulator) {
    	int minY = preset.getOption(CaveOptions.SPAGHETTI_CAVES_MIN_Y);
        int maxY = preset.getOption(CaveOptions.SPAGHETTI_CAVES_MAX_Y);
        float thickness = 0.083F;//preset.getOption(CaveOptions.SPAGHETTI_CAVES_THICKNESS);
        
    	DensityFunction modulator = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction sampler = DensityFunctions.weirdScaledSampler(modulator, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction elevation = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(minY, 8), 8.0);
        DensityFunction elevationGradient = DensityFunctions.add(elevation, DensityFunctions.yClampedGradient(minY, maxY, minY / -8.0D, maxY / -8.0D)).abs();
        DensityFunction spaghetti1 = DensityFunctions.add(elevationGradient, thicknessModulator).cube();
        DensityFunction spaghetti2 = DensityFunctions.add(sampler, DensityFunctions.mul(DensityFunctions.constant(thickness), thicknessModulator));
        return DensityFunctions.max(spaghetti2, spaghetti1).clamp(-1.0D, 1.0D);
    }

    private static DensityFunction noodle(Preset preset, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction y) {
        int minY = preset.getOption(CaveOptions.NOODLE_CAVES_MIN_Y);
        int maxY = preset.getOption(CaveOptions.NOODLE_CAVES_MAX_Y);
    	int baseY = minY + 4;
        
        DensityFunction selector = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE), 1.0, 1.0), baseY, maxY, -1);
        DensityFunction thickness = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), baseY, maxY, 0);
        DensityFunction ridgeA = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), baseY, maxY, 0);
        DensityFunction ridgeB = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), baseY, maxY, 0);
        DensityFunction ridge = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(ridgeA.abs(), ridgeB.abs()));
        return DensityFunctions.rangeChoice(selector, -1000000.0, 0.0D, DensityFunctions.constant(64.0), DensityFunctions.add(thickness, ridge));
    }
    
    private static DensityFunction entrances(Preset preset, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction spaghettiRoughnessFunction) {
		DensityFunction rarity3d = DensityFunctions.cacheOnce(DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
		DensityFunction thickness3d = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
		DensityFunction spaghetti3d1 = DensityFunctions.weirdScaledSampler(
			rarity3d, noiseParams.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
		);
		DensityFunction spaghetti3d2 = DensityFunctions.weirdScaledSampler(
			rarity3d, noiseParams.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1
		);
		DensityFunction weirdnessSampler = DensityFunctions.add(DensityFunctions.max(spaghetti3d1, spaghetti3d2), thickness3d).clamp(-1.0, 1.0);
		DensityFunction entranceNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
		DensityFunction entrances = DensityFunctions.add(
			DensityFunctions.add(entranceNoise, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0)
		);
		return DensityFunctions.cacheOnce(DensityFunctions.min(entrances, DensityFunctions.add(spaghettiRoughnessFunction, weirdnessSampler)));
    }
    
	private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction baseNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.PILLAR), 25.0, 0.3);
		DensityFunction rareness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
		DensityFunction thickness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
		DensityFunction pillar = DensityFunctions.add(DensityFunctions.mul(baseNoise, DensityFunctions.constant(2.0)), rareness);
		return DensityFunctions.cacheOnce(DensityFunctions.mul(pillar, thickness.cube()));
	}

	private static DensityFunction preliminarySurfaceLevel(Preset preset, DensityFunction factor, DensityFunction offset) {
		int minY = preset.getOption(WorldOptions.MIN_Y);
		int maxY = preset.getOption(WorldOptions.MAX_Y);
		
		DensityFunction cachedFactor = DensityFunctions.cache2d(factor);
		DensityFunction cachedOffset = DensityFunctions.cache2d(offset);
		DensityFunction offsetGradient = remap(
			DensityFunctions.add(
				DensityFunctions.mul(DensityFunctions.constant(Y_UNIT * 35.0D), cachedFactor.invert()),
				DensityFunctions.mul(DensityFunctions.constant(-1.0D), cachedOffset)
			),
			yGradientRange(minY),
			yGradientRange(maxY),
			minY,
			maxY
		);
		offsetGradient = offsetGradient.clamp(minY + 24.0D, maxY);
		DensityFunction preliminarySurface = DensityFunctions.add(
			slideOverworld(
				preset,
				DensityFunctions.add(NoiseRouterData.noiseGradientDensity(cachedFactor, offsetToDepth(preset, cachedOffset)), DensityFunctions.constant(Y_UNIT * -90)).clamp(-64.0, 64.0)
			),
			DensityFunctions.constant(-0.390625D)
		);
		return DensityFunctions.findTopSurface(preliminarySurface, offsetGradient, minY, 8);
	}

	private static DensityFunction finalDensity(Preset preset, BootstrapContext<DensityFunction> ctx, DensityFunction slopedCheese, DensityFunction underground, DensityFunction entrances, DensityFunction noodle) {
        float entranceCaveSize = preset.getOption(CaveOptions.ENTRANCE_CAVE_SIZE);
        DensityFunction interpolatedEntrances = entranceCaveSize > 0.0F ? DensityFunctions.min(slopedCheese, DensityFunctions.mul(DensityFunctions.constant(5.0D * entranceCaveSize), DensityFunctions.interpolated(entrances))) : slopedCheese;
        DensityFunction slopedCheeseRange = DensityFunctions.mul(DensityFunctions.rangeChoice(slopedCheese, -1000000.0D, 1.5625D, interpolatedEntrances, DensityFunctions.interpolated(slideOverworld(preset, underground))), DensityFunctions.constant(0.64)).squeeze();
		return DensityFunctions.min(slopedCheeseRange, noodle);
	}
	
	private static DensityFunction temperature(Preset preset, HolderGetter<Noise> noise) {
		return DensityFunctions.constant(TemperaturePoints.TEMPERATE + 0.05F);
	}

	private static DensityFunction humidity(Preset preset, HolderGetter<Noise> noise) {
		return DensityFunctions.constant(HumidityPoints.MILD + 0.05F);
	}
    
	private static DensityFunction aquiferBarrier(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		return DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_BARRIER), 0.5D);
	}
	
	private static DensityFunction aquiferFluidLevelFloodedness(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		return DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67D);
	}
	
	private static DensityFunction aquiferFluidLevelSpread(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		return DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
	}
	
	private static DensityFunction aquiferLava(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		return DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_LAVA));
	}
	
	private static DensityFunction oreVeininess(BootstrapContext<DensityFunction> ctx, int minVeinY, int maxVeinY) {
        HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		DensityFunction y = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.Y);
		return NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), minVeinY, maxVeinY, 0);
	}
	
	private static DensityFunction oreVein(BootstrapContext<DensityFunction> ctx, int minVeinY, int maxVeinY) {
        HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		DensityFunction y = NoiseRouterData.getFunction(densityFunctions, NoiseRouterData.Y);
		DensityFunction oreVeinA = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), minVeinY, maxVeinY, 0).abs();
		DensityFunction oreVeinB = NoiseRouterData.yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), minVeinY, maxVeinY, 0).abs();
		return DensityFunctions.add(DensityFunctions.constant(-0.08F), DensityFunctions.max(oreVeinA, oreVeinB));
	}
	
	private static DensityFunction oreGap(BootstrapContext<DensityFunction> ctx) {
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		return DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_GAP));
	}
    
	private static DensityFunction offsetToDepth(Preset preset, DensityFunction offset) {
    	int minY = preset.getOption(WorldOptions.MIN_Y);
        int maxY = preset.getOption(WorldOptions.MAX_Y);
        DensityFunction yGradient = DensityFunctions.yClampedGradient(minY, maxY, yGradientRange(minY), yGradientRange(maxY));
        return DensityFunctions.add(yGradient, offset);	
	}
	
	private static DensityFunction regionFunction(Seed seed, HolderGetter<Warp> warpGetter, int field) {
		ResourceLocation cacheId = RTFCommon.location("region");
		Holder<Warp> warp = warpGetter.getOrThrow(RTFWarps.TERRAIN_REGION);
		int terrainRegionScale = 1200;
		return new RegionFunction(cacheId, field, warp, 1.0F / terrainRegionScale);
	}

	private static DensityFunction terrain(DensityFunction ground, HolderGetter<Noise> noiseGetter, ResourceKey<Noise> noiseKey) {
		Holder<Noise> noise = noiseGetter.getOrThrow(noiseKey);
		DensityFunction region = RTFDensityFunctions.noise(noise);
		return DensityFunctions.add(ground, region);
	}
	
	private static WeightedFunction.Entry weightedTerrain(float weight, DensityFunction ground, HolderGetter<Noise> noiseGetter, ResourceKey<Noise> noiseKey) {
		DensityFunction function = terrain(ground, noiseGetter, noiseKey);
		return new WeightedFunction.Entry(weight, function);
	}
	
	private static DensityFunction terrainHeight(HolderGetter<Noise> noiseGetter, HolderGetter<Warp> warpGetter) {
		Seed seed = new Seed(0);

		DensityFunction ground = DensityFunctions.constant(64.0F / 256.0F);
		List<WeightedFunction.Entry> regions = List.of(
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.STEPPE),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.PLAINS),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.DALES),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.HILLS_1),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.HILLS_2),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.TORRIDONIAN),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.PLATEAU),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.BADLANDS),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.MOUNTAINS_1),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.MOUNTAINS_2),
			weightedTerrain(1.0F, ground, noiseGetter, RTFNoises.MOUNTAINS_3)
		);
		
		DensityFunction border = terrain(ground, noiseGetter, RTFNoises.STEPPE);
		DensityFunction regionEdge = regionFunction(seed, warpGetter, RegionFunction.EDGE);
		DensityFunction regionValue = regionFunction(seed, warpGetter, RegionFunction.VALUE);
		DensityFunction region = new WeightedFunction.Marker(regionValue, regions);
		return new BlendFunction(border, region, regionEdge);
	}
	
    private static DensityFunction noiseSampler(HolderGetter<Noise> noises, ResourceKey<Noise> noise) {
    	return sampler(RTFDensityFunctions.noise(noises.getOrThrow(noise)));
    }
    
    private static DensityFunction sampler(DensityFunction function) {
    	return Cache2d.makeGlobal(DensityFunctions.flatCache(function));
    }

	private static SplineFunction.Builder noiseSpline(HolderGetter<Noise> noises, ResourceKey<Noise> noise) {
    	return SplineFunction.builder(RTFDensityFunctions.noise(noises.getOrThrow(noise)));
    }
    
    private static DensityFunction slideOverworld(Preset preset, DensityFunction function) {
		int minY = preset.getOption(WorldOptions.MIN_Y);
        return slide(function, minY, 0, 24, Y_UNIT * 15);
    }
    
    private static DensityFunction slide(DensityFunction function, int minY, int bottomGradientStart, int bottomGradientEnd, double bottomGradientTarget) {
        DensityFunction bottomGradient = DensityFunctions.yClampedGradient(minY + bottomGradientStart, minY + bottomGradientEnd, 0.0, 1.0);
        return DensityFunctions.lerp(bottomGradient, bottomGradientTarget, function);
    }
    
	private static DensityFunction remap(DensityFunction densityFunction, double d, double e, double f, double g) {
		double h = (g - f) / (e - d);
		double i = f - d * h;
		return DensityFunctions.add(DensityFunctions.mul(densityFunction, DensityFunctions.constant(h)), DensityFunctions.constant(i));
	}
    
    private static float yGradientRange(float range) {
    	return 1.0F - (range / Y_SCALER);
    }

	private static DensityFunction blocks(DensityFunction base, DensityFunction blocks) {
		return DensityFunctions.add(base, blocks);
	}
	
	private static DensityFunction blocks(DensityFunction base, double blocks) {
		DensityFunction blocksFunction = DensityFunctions.constant(blocks);
		return blocks(base, blocksFunction);
	}

	private static DensityFunction layer(Preset preset, BootstrapContext<DensityFunction> ctx, ResourceKey<Layer.Factory<Reference<DensityFunction>>> key, int cacheSize, DensityFunction fallback) {
		Holder<Layer.Factory<Reference<DensityFunction>>> layer = RegistryUtil.lookupTyped(ctx, key);
		return Cache2d.makeGlobal(RTFDensityFunctions.flatCache2(RTFDensityFunctions.layer(layer, fallback), cacheSize));
	}

    private static DensityFunction registerAndWrap(BootstrapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction function) {
    	return NoiseRouterData.registerAndWrap(ctx, key, function);
    }

    private static Holder<DensityFunction> register(BootstrapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction function) {
    	return ctx.register(key, function);
    }
    
    protected static Holder<DensityFunction> getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
    	return densityFunctions.getOrThrow(key);
    }
    
    protected static DensityFunction getWrappedFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
    	return NoiseRouterData.getFunction(densityFunctions, key);
    }
    
    protected static ResourceKey<DensityFunction> createKey(String name) {
    	return RTFRegistries.createKey(Registries.DENSITY_FUNCTION, name);
    }
}