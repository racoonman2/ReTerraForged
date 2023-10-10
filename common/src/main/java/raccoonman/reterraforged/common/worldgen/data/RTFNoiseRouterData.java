package raccoonman.reterraforged.common.worldgen.data;

import java.util.stream.Stream;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.density.FlatCache;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;

public final class RTFNoiseRouterData {
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
    private static final ResourceKey<DensityFunction> CONTINENTS = createKey("continents");
    private static final ResourceKey<DensityFunction> EROSION = createKey("erosion");
    private static final ResourceKey<DensityFunction> RIDGES = createKey("ridges");
    private static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("ridges_folded");
    private static final ResourceKey<DensityFunction> OFFSET = createKey("offset");
    private static final ResourceKey<DensityFunction> FACTOR = createKey("factor");
    private static final ResourceKey<DensityFunction> DEPTH = createKey("depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKey("caves/entrances");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("caves/spaghetti_2d");

    public static void bootstrap(BootstapContext<DensityFunction> ctx, Preset preset) {
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> functions = ctx.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);

        int minY = DimensionType.MIN_Y * 2;
        int maxY = DimensionType.MAX_Y * 2;
        ctx.register(Y, DensityFunctions.yClampedGradient(minY, maxY, minY, maxY));
        DensityFunction densityFunction = registerAndWrap(ctx, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(noiseParams.getOrThrow(Noises.SHIFT)))));
        DensityFunction densityFunction2 = registerAndWrap(ctx, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(noiseParams.getOrThrow(Noises.SHIFT)))));
        Holder.Reference<DensityFunction> continent = ctx.register(CONTINENTS, DensityFunctions.flatCache(new NoiseWrapper.Marker(wrapNoise(noise, RTFNoiseData.CONTINENT).map(-1.0D, 1.0D))));
        Holder.Reference<DensityFunction> erosion = ctx.register(EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, noiseParams.getOrThrow(Noises.EROSION))));
        DensityFunction ridges = registerAndWrap(ctx, RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, noiseParams.getOrThrow(Noises.RIDGE))));
        ctx.register(RIDGES_FOLDED, peaksAndValleys(ridges));
        registerTerrainNoises(ctx, functions, noise, continent, erosion, OFFSET, FACTOR, DEPTH, SLOPED_CHEESE);
        ctx.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noiseParams));
        ctx.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
        ctx.register(SPAGHETTI_2D, spaghetti2D(functions, noiseParams));
        ctx.register(ENTRANCES, entrances(functions, noiseParams));
        ctx.register(NOODLE, noodle(functions, noiseParams));
        ctx.register(PILLARS, pillars(noiseParams));
    }

	private static void registerTerrainNoises(BootstapContext<DensityFunction> ctx, HolderGetter<DensityFunction> functions, HolderGetter<Noise> noise, Holder<DensityFunction> continent, Holder<DensityFunction> erosion, ResourceKey<DensityFunction> offsetKey, ResourceKey<DensityFunction> factorKey, ResourceKey<DensityFunction> depthKey, ResourceKey<DensityFunction> slopedCheeseKey) {
        DensityFunctions.Spline.Coordinate continentCoord = new DensityFunctions.Spline.Coordinate(continent);
        DensityFunctions.Spline.Coordinate erosionCoord = new DensityFunctions.Spline.Coordinate(erosion);
        DensityFunctions.Spline.Coordinate ridgesCoord = new DensityFunctions.Spline.Coordinate(functions.getOrThrow(RIDGES));
        DensityFunctions.Spline.Coordinate ridgesFoldedCoord = new DensityFunctions.Spline.Coordinate(functions.getOrThrow(RIDGES_FOLDED));
        DensityFunction offset = registerAndWrap(ctx, offsetKey, splineWithBlending(DensityFunctions.add(DensityFunctions.constant(-0.50375F), DensityFunctions.spline(TerrainProvider.overworldOffset(continentCoord, erosionCoord, ridgesFoldedCoord, false))), DensityFunctions.blendOffset()));
        DensityFunction factor = registerAndWrap(ctx, factorKey, new FlatCache.Marker(new NoiseWrapper.Marker(wrapNoise(noise, RTFNoiseData.HEIGHT)), 1));
        DensityFunction depth = registerAndWrap(ctx, depthKey, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5, -1.5), DensityFunctions.constant(-0.50375F)));
        ctx.register(slopedCheeseKey, noiseGradientDensity(DensityFunctions.constant(1.0D), DensityFunctions.add(depth, factor)));
    }

    private static DensityFunction peaksAndValleys(DensityFunction function) {
        return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(function.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant(-3.0));
    }

    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghettiRoughness = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction spaghettiRoughnessModulator = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(spaghettiRoughnessModulator, DensityFunctions.add(spaghettiRoughness.abs(), DensityFunctions.constant(-0.4))));
    }

    private static DensityFunction entrances(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghetti3dRarity = DensityFunctions.cacheOnce(DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
        DensityFunction spaghetti3dThickness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
        DensityFunction spaghetti3d1 = DensityFunctions.weirdScaledSampler(spaghetti3dRarity, noiseParams.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction spaghetti3d2 = DensityFunctions.weirdScaledSampler(spaghetti3dRarity, noiseParams.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction spaghetti3dBlend = DensityFunctions.add(DensityFunctions.max(spaghetti3d1, spaghetti3d2), spaghetti3dThickness).clamp(-1.0, 1.0);
        DensityFunction spaghettiRoughness = getFunction(functions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction entrance = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
        DensityFunction spaghettiEntranceBlend = DensityFunctions.add(DensityFunctions.add(entrance, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0));
        return DensityFunctions.cacheOnce(DensityFunctions.min(spaghettiEntranceBlend, DensityFunctions.add(spaghettiRoughness, spaghetti3dBlend)));
    }

    private static DensityFunction noodle(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction y = getFunction(functions, Y);
        DensityFunction noodle = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
        DensityFunction noodleThickness = yLimitedInterpolatable(y, DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
        DensityFunction noodleRidgeA = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction noodleRidgeB = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction noodleRidgeBlend = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(noodleRidgeA.abs(), noodleRidgeB.abs()));
        return DensityFunctions.rangeChoice(noodle, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(noodleThickness, noodleRidgeBlend));
    }

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction pillar = DensityFunctions.noise(noiseParams.getOrThrow(Noises.PILLAR), 25.0, 0.3);
        DensityFunction pillarRareness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction pillarThickness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction pillarSelector = DensityFunctions.add(DensityFunctions.mul(pillar, DensityFunctions.constant(2.0)), pillarRareness);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(pillarSelector, pillarThickness.cube()));
    }

    private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghetti2dModulator = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction spaghetti2dSampler = DensityFunctions.weirdScaledSampler(spaghetti2dModulator, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction spaghetti2dElevation = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(-64, 8), 8.0);
        DensityFunction spaghetti2dThicknessModulator = getFunction(functions, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction spaghetti2dGradient = DensityFunctions.add(spaghetti2dElevation, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction spaghetti2dCubed = DensityFunctions.add(spaghetti2dGradient, spaghetti2dThicknessModulator).cube();
        DensityFunction spaghetti2d = DensityFunctions.add(spaghetti2dSampler, DensityFunctions.mul(DensityFunctions.constant(0.083), spaghetti2dThicknessModulator));
        return DensityFunctions.max(spaghetti2d, spaghetti2dCubed).clamp(-1.0, 1.0);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction function) {
        DensityFunction spaghetti2d = getFunction(functions, SPAGHETTI_2D);
        DensityFunction spaghettiRoughnessFunction = getFunction(functions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveLayerNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction caveSelector = DensityFunctions.mul(DensityFunctions.constant(4.0), caveLayerNoise.square());
        DensityFunction caveCheeseNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction caveCheese = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), caveCheeseNoise).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), function)).clamp(0.0, 0.5));
        DensityFunction caveBlend = DensityFunctions.add(caveSelector, caveCheese);
        DensityFunction caveEntranceBlend = DensityFunctions.min(DensityFunctions.min(caveBlend, getFunction(functions, ENTRANCES)), DensityFunctions.add(spaghetti2d, spaghettiRoughnessFunction));
        DensityFunction pillars = getFunction(functions, PILLARS);
        DensityFunction pillarSelector = DensityFunctions.rangeChoice(pillars, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), pillars);
        return DensityFunctions.max(caveEntranceBlend, pillarSelector);
    }

    private static DensityFunction postProcess(DensityFunction function) {
        DensityFunction blended = DensityFunctions.blendDensity(function);
        return DensityFunctions.mul(DensityFunctions.interpolated(blended), DensityFunctions.constant(0.64)).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, Preset preset) {
    	TerrainSettings terrain = preset.terrain();
    	
        DensityFunction aquiferBarrier = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction aquiferFluidLevelFloodedness = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction aquiferFluidLevelSpread = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction aquiferLava = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction shiftX = getFunction(functions, SHIFT_X);
        DensityFunction shiftZ = getFunction(functions, SHIFT_Z);
        DensityFunction temperature = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseParams.getOrThrow(Noises.TEMPERATURE));
        DensityFunction vegetation = DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseParams.getOrThrow(Noises.VEGETATION));
        DensityFunction factor = getFunction(functions, FACTOR);
        DensityFunction depth = getFunction(functions, DEPTH);
        DensityFunction initialDensity = noiseGradientDensity(DensityFunctions.add(factor, DensityFunctions.constant(0.6D)), depth);
        DensityFunction slopedCheese = getFunction(functions, SLOPED_CHEESE);
        DensityFunction caveSurfaceBlend = DensityFunctions.rangeChoice(slopedCheese, -1000000.0, 1.5625, slopedCheese, postProcess(underground(functions, noiseParams, slopedCheese)));
        DensityFunction finalDensity = DensityFunctions.min(slideOverworld(caveSurfaceBlend, terrain.general.yScale), getFunction(functions, NOODLE));
        DensityFunction y = getFunction(functions, Y);
        int i = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int j = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction oreVeinness = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), i, j, 0);
        DensityFunction oreVeinA = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), i, j, 0).abs();
        DensityFunction oreVeinB = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), i, j, 0).abs();
        DensityFunction oreSelector = DensityFunctions.add(DensityFunctions.constant(-0.08F), DensityFunctions.max(oreVeinA, oreVeinB));
        DensityFunction oreGap = DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(aquiferBarrier, aquiferFluidLevelFloodedness, aquiferFluidLevelSpread, aquiferLava, temperature, vegetation, getFunction(functions, CONTINENTS), getFunction(functions, EROSION), depth, getFunction(functions, RIDGES), slideOverworld(initialDensity.clamp(-64.0, 64.0), terrain.general.yScale), finalDensity, oreVeinness, oreSelector, oreGap);
    }

    private static DensityFunction slideOverworld(DensityFunction function, int yScale) {
        return slide(function, -64, yScale, 80, 64, -0.078125, 0, 24, 0.1171875);
    }

    private static DensityFunction splineWithBlending(DensityFunction function, DensityFunction blendOffset) {
        DensityFunction blended = DensityFunctions.lerp(DensityFunctions.blendAlpha(), blendOffset, function);
        return DensityFunctions.flatCache(DensityFunctions.cache2d(blended));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction factor, DensityFunction height) {
        DensityFunction scaled = DensityFunctions.mul(height, factor);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), scaled.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction input, DensityFunction whenInRange, int minInclusive, int maxInclusive, int whenOutOfRange) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(input, minInclusive, maxInclusive + 1, whenInRange, DensityFunctions.constant(whenOutOfRange)));
    }

    private static DensityFunction slide(DensityFunction function, int minY, int maxY, int baseFrom, int baseTo, double baseMin, int caveFrom, int caveTo, double caveMin) {
        DensityFunction gradient = function;
        DensityFunction baseGradient = DensityFunctions.yClampedGradient(minY + maxY - baseFrom, minY + maxY - baseTo, 1.0, 0.0);
        gradient = DensityFunctions.lerp(baseGradient, baseMin, gradient);
        DensityFunction caveGradient = DensityFunctions.yClampedGradient(minY + caveFrom, minY + caveTo, 0.0, 1.0);
        gradient = DensityFunctions.lerp(caveGradient, caveMin, gradient);
        return gradient;
    }

    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction function) {
        return new DensityFunctions.HolderHolder(ctx.register(key, function));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> functions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(functions.getOrThrow(key));
    }

    private static Noise wrapNoise(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
    	return new HolderNoise(noise.getOrThrow(key));
    }

    private static ResourceKey<DensityFunction> createKey(String string) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, ReTerraForged.resolve(string));
    }
}

