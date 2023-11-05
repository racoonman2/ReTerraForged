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
import raccoonman.reterraforged.common.level.levelgen.density.CellSampler;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class RTFNoiseRouterData {
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    public static final ResourceKey<DensityFunction> CONTINENTS = createKey("continents");
    public static final ResourceKey<DensityFunction> EROSION = createKey("erosion");
    public static final ResourceKey<DensityFunction> RIDGES = createKey("ridges");
    public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("ridges_folded");
    public static final ResourceKey<DensityFunction> OFFSET = createKey("offset");
    public static final ResourceKey<DensityFunction> FACTOR = createKey("factor");
    public static final ResourceKey<DensityFunction> DEPTH = createKey("depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("caves/spaghetti_2d");

    public static final ResourceKey<DensityFunction> HEIGHT = createKey("height");
    public static final ResourceKey<DensityFunction> STEEPNESS = createKey("steepness");
    public static final ResourceKey<DensityFunction> SEDIMENT = createKey("sediment");
    
    private static ResourceKey<DensityFunction> createKey(String string) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, RTFRegistries.resolve(string));
    }

    public static void bootstrap(BootstapContext<DensityFunction> ctx, Preset preset) {
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<Preset> presets = ctx.lookup(RTFRegistries.PRESET);
        
        int minY = DimensionType.MIN_Y * 2;
        int maxY = DimensionType.MAX_Y * 2;
        ctx.register(Y, DensityFunctions.yClampedGradient(minY, maxY, minY, maxY));
        Holder.Reference<DensityFunction> continents = ctx.register(CONTINENTS, wrapCell(presets, CellSampler.Channel.CONTINENT));
        Holder.Reference<DensityFunction> holder2 = ctx.register(EROSION, wrapCell(presets, CellSampler.Channel.EROSION));
        DensityFunction densityFunction3 = registerAndWrap(ctx, RIDGES, wrapCell(presets, CellSampler.Channel.RIDGE));
        ctx.register(RIDGES_FOLDED, peaksAndValleys(densityFunction3));
        registerTerrainNoises(ctx, presets, densityFunctions, continents, holder2);
        ctx.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noiseParams));
        ctx.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
        ctx.register(SPAGHETTI_2D, spaghetti2D(densityFunctions, noiseParams));
        ctx.register(NOODLE, noodle(densityFunctions, noiseParams));
        ctx.register(PILLARS, pillars(noiseParams));

        ctx.register(STEEPNESS, wrapCell(presets, CellSampler.Channel.STEEPNESS));
        ctx.register(SEDIMENT, wrapCell(presets, CellSampler.Channel.SEDIMENT));
    }
    
    private static DensityFunction wrapCell(HolderGetter<Preset> presets, CellSampler.Channel channel) {
        Holder<Preset> preset = presets.getOrThrow(Preset.KEY);
    	return new CellSampler.Marker(channel, preset);
    }
    
    private static void registerTerrainNoises(BootstapContext<DensityFunction> ctx, HolderGetter<Preset> presets, HolderGetter<DensityFunction> densityFunctions, Holder<DensityFunction> continent, Holder<DensityFunction> erosion) {
        DensityFunctions.Spline.Coordinate continentCoord = new DensityFunctions.Spline.Coordinate(continent);
        DensityFunctions.Spline.Coordinate erosionCoord = new DensityFunctions.Spline.Coordinate(erosion);
        DensityFunctions.Spline.Coordinate ridgesCoord = new DensityFunctions.Spline.Coordinate(densityFunctions.getOrThrow(RIDGES));
        DensityFunctions.Spline.Coordinate ridgesFoldedCoord = new DensityFunctions.Spline.Coordinate(densityFunctions.getOrThrow(RIDGES_FOLDED));

        DensityFunction height = registerAndWrap(ctx, HEIGHT, wrapCell(presets, CellSampler.Channel.HEIGHT));
        DensityFunction offset = registerAndWrap(ctx, OFFSET, DensityFunctions.add(DensityFunctions.constant(-127.0D * (1.0D / 128.0D) /*-0.9959375D*/), DensityFunctions.mul(height, DensityFunctions.constant(2.0D))));
        DensityFunction factor = registerAndWrap(ctx, FACTOR, spline(DensityFunctions.spline(TerrainProvider.overworldFactor(continentCoord, erosionCoord, ridgesCoord, ridgesFoldedCoord, false))));
        DensityFunction depth = registerAndWrap(ctx, DEPTH, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5, -1.5), offset));
        ctx.register(SLOPED_CHEESE, noiseGradientDensity(factor, depth));
    }

    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> ctx, ResourceKey<DensityFunction> resourceKey, DensityFunction densityFunction) {
        return new DensityFunctions.HolderHolder(ctx.register(resourceKey, densityFunction));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> resourceKey) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(resourceKey));
    }

    private static DensityFunction peaksAndValleys(DensityFunction densityFunction) {
        return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(densityFunction.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant(-3.0));
    }

    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghettiRoughnessNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction spaghettiRoughnessModulator = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(spaghettiRoughnessModulator, DensityFunctions.add(spaghettiRoughnessNoise.abs(), DensityFunctions.constant(-0.4))));
    }

    private static DensityFunction noodle(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction y = getFunction(densityFunctions, Y);
        DensityFunction noodleNoise = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
        DensityFunction noodleThicknessNoise = yLimitedInterpolatable(y, DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
        DensityFunction noodleRidgeANoise = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction noodleRidgeBNoise = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction noodleRidge = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(noodleRidgeANoise.abs(), noodleRidgeBNoise.abs()));
        return DensityFunctions.rangeChoice(noodleNoise, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(noodleThicknessNoise, noodleRidge));
    }

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction pillarNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.PILLAR), 25.0, 0.3);
        DensityFunction pillarRarenessNoise = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction pillarThicknessNoise = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction pillar = DensityFunctions.add(DensityFunctions.mul(pillarNoise, DensityFunctions.constant(2.0)), pillarRarenessNoise);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(pillar, pillarThicknessNoise.cube()));
    }

    private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghetti2dModulatorNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction spaghetti2dModulatorSampler = DensityFunctions.weirdScaledSampler(spaghetti2dModulatorNoise, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction spaghetti2dElevationNoise = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(-64, 8), 8.0);
        DensityFunction spaghetti2dThicknessModulator = getFunction(densityFunctions, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction spaghetti2dElevationGradient = DensityFunctions.add(spaghetti2dElevationNoise, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction spaghetti2dModulatorGradient = DensityFunctions.add(spaghetti2dElevationGradient, spaghetti2dThicknessModulator).cube();
        DensityFunction spaghetti2d = DensityFunctions.add(spaghetti2dModulatorSampler, DensityFunctions.mul(DensityFunctions.constant(0.083), spaghetti2dThicknessModulator));
        return DensityFunctions.max(spaghetti2d, spaghetti2dModulatorGradient).clamp(-1.0, 1.0);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction densityFunction) {
        DensityFunction spaghetti2d = getFunction(densityFunctions, SPAGHETTI_2D);
        DensityFunction spaghettiRoughnessFunction = getFunction(densityFunctions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveLayerNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction caveLayerSquared = DensityFunctions.mul(DensityFunctions.constant(4.0), caveLayerNoise.square());
        DensityFunction caveCheeseNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction caveMix = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), caveCheeseNoise).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), densityFunction)).clamp(0.0, 0.5));
        DensityFunction caveLayerBlend = DensityFunctions.add(caveLayerSquared, caveMix);
        DensityFunction cheeseSpaghettiMix = DensityFunctions.min(caveLayerBlend, DensityFunctions.add(spaghetti2d, spaghettiRoughnessFunction));
        DensityFunction pillars = getFunction(densityFunctions, PILLARS);
        DensityFunction pillarChoice = DensityFunctions.rangeChoice(pillars, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), pillars);
        return DensityFunctions.max(cheeseSpaghettiMix, pillarChoice);
    }

    private static DensityFunction postProcess(DensityFunction densityFunction) {
        DensityFunction blended = DensityFunctions.blendDensity(densityFunction);
        return DensityFunctions.mul(DensityFunctions.interpolated(blended), DensityFunctions.constant(0.64)).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> Params, HolderGetter<Preset> presets, Preset preset) {
        DensityFunction aquiferBarrier = DensityFunctions.noise(Params.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction aquiferFluidLevelFloodedness = DensityFunctions.noise(Params.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction aquiferFluidLevelSpread = DensityFunctions.noise(Params.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction aquiferLava = DensityFunctions.noise(Params.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction temperature = wrapCell(presets, CellSampler.Channel.TEMPERATURE);
        DensityFunction moisture = wrapCell(presets, CellSampler.Channel.MOISTURE);
        DensityFunction factor = getFunction(densityFunctions, FACTOR);
        DensityFunction depth = getFunction(densityFunctions, DEPTH);
        DensityFunction initialDensity = noiseGradientDensity(DensityFunctions.cache2d(factor), depth);
        DensityFunction slopedCheese = getFunction(densityFunctions, SLOPED_CHEESE);
        DensityFunction caveSurfaceBlend = DensityFunctions.rangeChoice(slopedCheese, -1000000.0, 1.5625, slopedCheese, postProcess(underground(densityFunctions, Params, slopedCheese)));
        DensityFunction finalDensity = DensityFunctions.min(slideOverworld(caveSurfaceBlend, preset), getFunction(densityFunctions, NOODLE));
        DensityFunction y = getFunction(densityFunctions, Y);
        int minVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int maxVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction oreVeinness = yLimitedInterpolatable(y, DensityFunctions.noise(Params.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), minVeinY, maxVeinY, 0);
        DensityFunction oreVeinA = yLimitedInterpolatable(y, DensityFunctions.noise(Params.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), minVeinY, maxVeinY, 0).abs();
        DensityFunction oreVeinB = yLimitedInterpolatable(y, DensityFunctions.noise(Params.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), minVeinY, maxVeinY, 0).abs();
        DensityFunction oreVeinMix = DensityFunctions.add(DensityFunctions.constant(-0.08f), DensityFunctions.max(oreVeinA, oreVeinB));
        DensityFunction oreGap = DensityFunctions.noise(Params.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(aquiferBarrier, aquiferFluidLevelFloodedness, aquiferFluidLevelSpread, aquiferLava, temperature, moisture, getFunction(densityFunctions, CONTINENTS), getFunction(densityFunctions, EROSION), depth, getFunction(densityFunctions, RIDGES), slideOverworld(DensityFunctions.add(initialDensity, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0), preset), finalDensity, oreVeinness, oreVeinMix, oreGap);
    }
    
    private static DensityFunction slideOverworld(DensityFunction densityFunction, Preset preset) {
        return slide(densityFunction, -preset.world().properties.worldDepth, 384, 80, 64, -0.078125, 0, 24, 0.1171875);
    }

    private static DensityFunction spline(DensityFunction densityFunction) {
        return DensityFunctions.flatCache(DensityFunctions.cache2d(densityFunction));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction factor, DensityFunction depth) {
        DensityFunction scaled = DensityFunctions.mul(depth, factor);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), scaled.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction y, DensityFunction height, int minY, int maxY, int unit) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(y, minY, maxY + 1, height, DensityFunctions.constant(unit)));
    }

    private static DensityFunction slide(DensityFunction densityFunction, int minY, int yRange, int surfaceLerpStart, int surfaceLerpEnd, double surfaceLerpTarget, int depthLerpStart, int depthLerpEnd, double depthLerpTarget) {
        DensityFunction slide = densityFunction;
        DensityFunction surfaceGradient = DensityFunctions.yClampedGradient(minY + yRange - surfaceLerpStart, minY + yRange - surfaceLerpEnd, 1.0, 0.0);
        slide = DensityFunctions.lerp(surfaceGradient, surfaceLerpTarget, slide);
        DensityFunction deptohGradient = DensityFunctions.yClampedGradient(minY + depthLerpStart, minY + depthLerpEnd, 0.0, 1.0);
        slide = DensityFunctions.lerp(deptohGradient, depthLerpTarget, slide);
        return slide;
    }
}

