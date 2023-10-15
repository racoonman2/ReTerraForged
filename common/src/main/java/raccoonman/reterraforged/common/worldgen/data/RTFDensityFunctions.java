package raccoonman.reterraforged.common.worldgen.data;

import java.util.stream.Stream;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
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
import raccoonman.reterraforged.common.level.levelgen.density.YScale;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public final class RTFDensityFunctions {
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    public static final ResourceKey<DensityFunction> HEIGHT = createKey("height");
    private static final ResourceKey<DensityFunction> TEMPERATURE = createKey("temperature");
    private static final ResourceKey<DensityFunction> MOISTURE = createKey("moisture");
    public static final ResourceKey<DensityFunction> CONTINENTS = createKey("continents");
    public static final ResourceKey<DensityFunction> EROSION = createKey("erosion");
    public static final ResourceKey<DensityFunction> RIDGES = createKey("ridges");
    public static final ResourceKey<DensityFunction> DEPTH = createKey("depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("caves/spaghetti_2d");

    private static ResourceKey<DensityFunction> createKey(String string) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, ReTerraForged.resolve(string));
    }

    public static void bootstrap(BootstapContext<DensityFunction> ctx, Preset preset) {
        HolderGetter<NormalNoise.NoiseParameters> holderGetter = ctx.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> holderGetter2 = ctx.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
        
        int minY = DimensionType.MIN_Y * 2;
        int maxY = DimensionType.MAX_Y * 2;
        ctx.register(Y, DensityFunctions.yClampedGradient(minY, maxY, minY, maxY));
        DensityFunction height = registerAndWrap(ctx, HEIGHT, new FlatCache.Marker(new NoiseWrapper.Marker(getNoise(noise, RTFTerrainNoise.VARIANCE)), 1));
        ctx.register(TEMPERATURE, new NoiseWrapper.Marker(getNoise(noise, RTFClimateNoise.TEMPERATURE).map(-1.0D, 1.0D)));
        ctx.register(MOISTURE, new NoiseWrapper.Marker(getNoise(noise, RTFClimateNoise.MOISTURE).map(-1.0D, 1.0D)));
        ctx.register(CONTINENTS, DensityFunctions.flatCache(new NoiseWrapper.Marker(getNoise(noise, RTFTerrainNoise.CONTINENT).map(-1.0D, 1.0D))));
        ctx.register(EROSION, DensityFunctions.cache2d(new NoiseWrapper.Marker(getNoise(noise, RTFTerrainNoise.EROSION))));
        ctx.register(RIDGES, DensityFunctions.cache2d(new NoiseWrapper.Marker(getNoise(noise, RTFTerrainNoise.RIDGE))));
        DensityFunction depth = registerAndWrap(ctx, DEPTH, DensityFunctions.add(scale(height), DensityFunctions.add(DensityFunctions.constant(-0.50375F), DensityFunctions.yClampedGradient(-64, 320, 1.5, -1.5))));
        ctx.register(SLOPED_CHEESE, noiseGradientDensity(DensityFunctions.constant(1.0D), depth));
        ctx.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(holderGetter));
        ctx.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
        ctx.register(SPAGHETTI_2D, spaghetti2D(holderGetter2, holderGetter));
        ctx.register(NOODLE, noodle(holderGetter2, holderGetter));
        ctx.register(PILLARS, pillars(holderGetter));
    }
    
    //for some reason doing this through density functions messes up caves
    //so this is our solution for now
    //TODO remove YScale and do this through density functions
    private static DensityFunction scale(DensityFunction height) {
    	return new YScale(height, -63, 256, 0.0078125D);// DensityFunctions.mul(DensityFunctions.mul(DensityFunctions.add(height, DensityFunctions.constant(-63.0D / 256.0D)), DensityFunctions.constant(256.0D)), DensityFunctions.constant(0.0078125D));
    }

    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction function) {
        return new DensityFunctions.HolderHolder(ctx.register(key, function));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
    }

    private static Noise getNoise(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
    	return new HolderNoise(noise.getOrThrow(key));
    }
    
    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghettiRoughnessNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction spaghettiRoughnessModulatorNoise = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(spaghettiRoughnessModulatorNoise, DensityFunctions.add(spaghettiRoughnessNoise.abs(), DensityFunctions.constant(-0.4))));
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

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> holderGetter) {
        DensityFunction pillarNoise = DensityFunctions.noise(holderGetter.getOrThrow(Noises.PILLAR), 25.0, 0.3);
        DensityFunction pillarRarenessNoise = DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction pillarThicknessNoise = DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction pillarSelector = DensityFunctions.add(DensityFunctions.mul(pillarNoise, DensityFunctions.constant(2.0)), pillarRarenessNoise);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(pillarSelector, pillarThicknessNoise.cube()));
    }

    private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghetti2dModulatorNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction spaghetti2dNoise = DensityFunctions.weirdScaledSampler(spaghetti2dModulatorNoise, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction spaghetti2dNoiseElevation = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(-64, 8), 8.0);
        DensityFunction spaghetti2dThicknessModulator = getFunction(densityFunctions, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction spaghettiGradient = DensityFunctions.add(spaghetti2dNoiseElevation, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction spaghetti1 = DensityFunctions.add(spaghettiGradient, spaghetti2dThicknessModulator).cube();
        DensityFunction spaghetti2 = DensityFunctions.add(spaghetti2dNoise, DensityFunctions.mul(DensityFunctions.constant(0.083), spaghetti2dThicknessModulator));
        return DensityFunctions.max(spaghetti2, spaghetti1).clamp(-1.0, 1.0);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction height) {
        DensityFunction spaghetti2d = getFunction(densityFunctions, SPAGHETTI_2D);
        DensityFunction spaghettiRoughnessFunction = getFunction(densityFunctions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveLayerNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction cave1 = DensityFunctions.mul(DensityFunctions.constant(4.0), caveLayerNoise.square());
        DensityFunction caveCheeseNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction cave2 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), caveCheeseNoise).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), height)).clamp(0.0, 0.5));
        DensityFunction caveLayerMix = DensityFunctions.add(cave1, cave2);
        DensityFunction caveSpaghetti = DensityFunctions.min(caveLayerMix, DensityFunctions.add(spaghetti2d, spaghettiRoughnessFunction));
        DensityFunction pillars = getFunction(densityFunctions, PILLARS);
        DensityFunction spaghettiChoice = DensityFunctions.rangeChoice(pillars, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), pillars);
        return DensityFunctions.max(caveSpaghetti, spaghettiChoice);
    }

    private static DensityFunction postProcess(DensityFunction function) {
        DensityFunction blended = DensityFunctions.blendDensity(function);
        return DensityFunctions.mul(DensityFunctions.interpolated(blended), DensityFunctions.constant(0.64)).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, Preset preset) {
    	WorldSettings world = preset.world();
    	int worldHeight = world.properties.worldHeight;
    	
    	DensityFunction aquiferBarrierNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction aquiferFluidLevelFloodednessNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction aquiferFluidLevelSpreadNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction aquiferLavaNoise = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_LAVA));
        //TODO maybe add terraforged's height temperature offset?
        DensityFunction temperature = getFunction(densityFunctions, TEMPERATURE);
        DensityFunction vegetation = getFunction(densityFunctions, MOISTURE);
        DensityFunction depth = getFunction(densityFunctions, DEPTH);
        DensityFunction initialDensity = noiseGradientDensity(DensityFunctions.constant(2.0D), depth);
        DensityFunction slopedCheese = getFunction(densityFunctions, SLOPED_CHEESE);
        DensityFunction slopedCheeseWithCaves = DensityFunctions.rangeChoice(slopedCheese, -1000000.0, 1.5625, slopedCheese, postProcess(underground(densityFunctions, noiseParams, slopedCheese)));
        DensityFunction slopedCheeseWithNoodle = DensityFunctions.min(slideOverworld(slopedCheeseWithCaves, worldHeight), getFunction(densityFunctions, NOODLE));
        DensityFunction y = getFunction(densityFunctions, Y);
        int minOreVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int maxOreVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction oreVeininessNoise = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), minOreVeinY, maxOreVeinY, 0);
        DensityFunction oreVeinANoise = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), minOreVeinY, maxOreVeinY, 0).abs();
        DensityFunction oreVeinBNoise = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), minOreVeinY, maxOreVeinY, 0).abs();
        DensityFunction oreVein = DensityFunctions.add(DensityFunctions.constant(-0.08F), DensityFunctions.max(oreVeinANoise, oreVeinBNoise));
        DensityFunction oreGap = DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(aquiferBarrierNoise, aquiferFluidLevelFloodednessNoise, aquiferFluidLevelSpreadNoise, aquiferLavaNoise, temperature, vegetation, DensityFunctions.add(getFunction(densityFunctions, CONTINENTS), DensityFunctions.constant(0.22D)), getFunction(densityFunctions, EROSION), DensityFunctions.add(depth, DensityFunctions.constant(-0.125D)), getFunction(densityFunctions, RIDGES), slideOverworld(initialDensity.clamp(-64.0, 64.0), worldHeight), slopedCheeseWithNoodle, oreVeininessNoise, oreVein, oreGap);
    }

    private static DensityFunction slideOverworld(DensityFunction function, int worldHeight) {
        return slide(function, -64, worldHeight + 64, 80, 64, -0.078125, 0, 24, 0.1171875);
    }

    private static DensityFunction noiseGradientDensity(DensityFunction factor, DensityFunction height) {
        DensityFunction scaled = DensityFunctions.mul(height, factor);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), scaled.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction input, DensityFunction inRange, int min, int max, int outOfRange) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(input, min, max + 1, inRange, DensityFunctions.constant(outOfRange)));
    }

    private static DensityFunction slide(DensityFunction densityFunction, int minY, int maxY, int topSlideMinY, int topSlideMaxY, double aboveGradientValue, int bottomSlideMinY, int bottomSlideMaxY, double bottomGradientValue) {
        DensityFunction slide = densityFunction;
        DensityFunction aboveGradient = DensityFunctions.yClampedGradient(minY + maxY - topSlideMinY, minY + maxY - topSlideMaxY, 1.0, 0.0);
        slide = DensityFunctions.lerp(aboveGradient, aboveGradientValue, slide);
        DensityFunction bottomGradient = DensityFunctions.yClampedGradient(minY + bottomSlideMinY, minY + bottomSlideMaxY, 0.0, 1.0);
        slide = DensityFunctions.lerp(bottomGradient, bottomGradientValue, slide);
        return slide;
    }
}

