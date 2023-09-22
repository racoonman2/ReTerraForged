package raccoonman.reterraforged.common.worldgen.data;

import java.util.stream.Stream;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CubicSpline;
import net.minecraft.util.ToFloatFunction;
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
import raccoonman.reterraforged.common.level.levelgen.density.YGradient;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.noise.RTFClimateNoise;
import raccoonman.reterraforged.common.worldgen.data.noise.RTFTerrainNoise;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public class RTFNoiseRouterData {
    private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0);
    private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
    private static final ResourceKey<DensityFunction> ZERO = createKey("zero");
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    public static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
    public static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
    public static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
    public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("overworld/ridges_folded");
    public static final ResourceKey<DensityFunction> OFFSET = createKey("overworld/offset");
    public static final ResourceKey<DensityFunction> FACTOR = createKey("overworld/factor");
    public static final ResourceKey<DensityFunction> JAGGEDNESS = createKey("overworld/jaggedness");
    public static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
    public static final ResourceKey<DensityFunction> HEIGHT = createKey("overworld/height");
    public static final ResourceKey<DensityFunction> GRADIENT = createKey("overworld/gradient");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> ENTRANCES = createKey("overworld/caves/entrances");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");

    private static ResourceKey<DensityFunction> createKey(String string) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, ReTerraForged.resolve(string));
    }

    // can we use the height gradient for the ridge noise?
    public static void bootstrap(BootstapContext<DensityFunction> ctx, Preset preset) {
        HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> functions = ctx.lookup(Registries.DENSITY_FUNCTION);
        HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
        
        ctx.register(ZERO, DensityFunctions.zero());
        int minY = DimensionType.MIN_Y * 2;
        int maxY = DimensionType.MAX_Y * 2;
        ctx.register(Y, DensityFunctions.yClampedGradient(minY, maxY, minY, maxY));
        Holder.Reference<DensityFunction> continents = ctx.register(CONTINENTS, DensityFunctions.flatCache(new NoiseWrapper.Marker(new HolderNoise(noise.getOrThrow(preset.world().continent.continentType)).map(-1.0D, 1.0D))));
        Holder.Reference<DensityFunction> erosion = ctx.register(EROSION, DensityFunctions.constant(0.0D));
        DensityFunction ridges = registerAndWrap(ctx, RIDGES, DensityFunctions.constant(-0.25D));
        ctx.register(RIDGES_FOLDED, DensityFunctions.constant(0.0D));
        registerTerrainNoises(ctx, functions, DensityFunctions.constant(0.0D), continents, erosion, OFFSET, FACTOR, JAGGEDNESS, DEPTH, SLOPED_CHEESE);
        ctx.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noiseParams));
        ctx.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
        ctx.register(SPAGHETTI_2D, spaghetti2D(functions, noiseParams));
        ctx.register(ENTRANCES, entrances(functions, noiseParams));
        ctx.register(NOODLE, noodle(functions, noiseParams));
        ctx.register(PILLARS, pillars(noiseParams));
        ctx.register(HEIGHT, new FlatCache.Marker(new NoiseWrapper.Marker(new HolderNoise(noise.getOrThrow(RTFTerrainNoise.ROOT))), 1));
    }

    private static <C, I extends ToFloatFunction<C>> CubicSpline<C, I> overworldOffset(I toFloatFunction) {
        return CubicSpline.builder(toFloatFunction).addPoint(-0.235F, 0.044F).build();//.addPoint(-1.1f, 0.044f).addPoint(-1.02f, -0.2222f).addPoint(-0.51f, -0.2222f).addPoint(-0.44f, -0.12f).addPoint(-0.18f, -0.12f).build();
    }
    
    private static void registerTerrainNoises(BootstapContext<DensityFunction> ctx, HolderGetter<DensityFunction> functions, DensityFunction jagged, Holder<DensityFunction> continent, Holder<DensityFunction> erosion, ResourceKey<DensityFunction> offsetKey, ResourceKey<DensityFunction> factorKey, ResourceKey<DensityFunction> jaggednessKey, ResourceKey<DensityFunction> depthKey, ResourceKey<DensityFunction> slopedCheeseKey) {
        DensityFunctions.Spline.Coordinate continentCoord = new DensityFunctions.Spline.Coordinate(continent);
        DensityFunctions.Spline.Coordinate erosionCoord = new DensityFunctions.Spline.Coordinate(erosion);
        DensityFunctions.Spline.Coordinate ridgesCoord = new DensityFunctions.Spline.Coordinate(functions.getOrThrow(RIDGES));
        DensityFunctions.Spline.Coordinate ridgesFoldedCoord = new DensityFunctions.Spline.Coordinate(functions.getOrThrow(RIDGES_FOLDED));
        DensityFunction offsetBlend = registerAndWrap(ctx, offsetKey, splineWithBlending(DensityFunctions.spline(overworldOffset(continentCoord)), DensityFunctions.blendOffset()));
        ctx.register(factorKey, splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldFactor(continentCoord, erosionCoord, ridgesCoord, ridgesFoldedCoord, false)), BLENDING_FACTOR));
        ctx.register(depthKey, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5, -1.5), offsetBlend));
    }
    
    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction function) {
        return new DensityFunctions.HolderHolder(ctx.register(key, function));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> functions, ResourceKey<DensityFunction> key) {
        return new DensityFunctions.HolderHolder(functions.getOrThrow(key));
    }

    private static DensityFunction peaksAndValleys(DensityFunction function) {
        return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(function.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant(-3.0));
    }

    public static float peaksAndValleys(float f) {
        return -(Math.abs(Math.abs(f) - 0.6666667f) - 0.33333334f) * 3.0f;
    }

    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghettiRoughness = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction spaghettiRoughnessModulator = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(spaghettiRoughnessModulator, DensityFunctions.add(spaghettiRoughness.abs(), DensityFunctions.constant(-0.4))));
    }

    private static DensityFunction entrances(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghettiRarity = DensityFunctions.cacheOnce(DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
        DensityFunction spaghettiThickness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
        DensityFunction spaghetti1 = DensityFunctions.weirdScaledSampler(spaghettiRarity, noiseParams.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction spaghetti2 = DensityFunctions.weirdScaledSampler(spaghettiRarity, noiseParams.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction spaghettiBlend = DensityFunctions.add(DensityFunctions.max(spaghetti1, spaghetti2), spaghettiThickness).clamp(-1.0, 1.0);
        DensityFunction spaghettiRoughness = getFunction(functions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveEntrance = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
        DensityFunction entranceGradient = DensityFunctions.add(DensityFunctions.add(caveEntrance, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0));
        return DensityFunctions.cacheOnce(DensityFunctions.min(entranceGradient, DensityFunctions.add(spaghettiRoughness, spaghettiBlend)));
    }

    private static DensityFunction noodle(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction y = getFunction(functions, Y);
        DensityFunction noodle = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
        DensityFunction noodleThickness = yLimitedInterpolatable(y, DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
        DensityFunction noodleRidgeA = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction noodleRidgeB = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction noodleRidgeSelector = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(noodleRidgeA.abs(), noodleRidgeB.abs()));
        return DensityFunctions.rangeChoice(noodle, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(noodleThickness, noodleRidgeSelector));
    }

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction pillar = DensityFunctions.noise(noiseParams.getOrThrow(Noises.PILLAR), 25.0, 0.3);
        DensityFunction pillarRareness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction pillarThickness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction pillarSelector = DensityFunctions.add(DensityFunctions.mul(pillar, DensityFunctions.constant(2.0)), pillarRareness);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(pillarSelector, pillarThickness.cube()));
    }

    private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
        DensityFunction spaghettiModulator = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction spaghetti2D = DensityFunctions.weirdScaledSampler(spaghettiModulator, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction spaghettiElevation = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(-64, 8), 8.0);
        DensityFunction spaghettiThicknessModulator = getFunction(functions, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction spaghettiGradient = DensityFunctions.add(spaghettiElevation, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction spaghettiBlended = DensityFunctions.add(spaghettiGradient, spaghettiThicknessModulator).cube();
        DensityFunction spaghettiSelector = DensityFunctions.add(spaghetti2D, DensityFunctions.mul(DensityFunctions.constant(0.083), spaghettiThicknessModulator));
        return DensityFunctions.max(spaghettiSelector, spaghettiBlended).clamp(-1.0, 1.0);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction densityFunction) {
        DensityFunction spaghetti = getFunction(functions, SPAGHETTI_2D);
        DensityFunction spaghettiRoughness = getFunction(functions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveLayer = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction scaledCaveLayer = DensityFunctions.mul(DensityFunctions.constant(4.0), caveLayer.square());
        DensityFunction caveCheese = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction scaledCaveCheese = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), caveCheese).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), densityFunction)).clamp(0.0, 0.5));
        DensityFunction caveSum = DensityFunctions.add(scaledCaveLayer, scaledCaveCheese);
        DensityFunction caveMix = DensityFunctions.min(caveSum, DensityFunctions.add(spaghetti, spaghettiRoughness));
        DensityFunction pillars = getFunction(functions, PILLARS);
        DensityFunction pillarChoice = DensityFunctions.rangeChoice(pillars, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), pillars);
        return DensityFunctions.max(caveMix, pillarChoice);
    }

    private static DensityFunction postProcess(DensityFunction function) {
        DensityFunction blend = DensityFunctions.blendDensity(function);
        return DensityFunctions.mul(DensityFunctions.interpolated(blend), DensityFunctions.constant(0.64)).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> functions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, HolderGetter<Noise> noise, Preset preset) {
        DensityFunction aquiferBarrier = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction aquiferFluidLevelFloodedness = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction aquiferFluidLevelSpread = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction aquiferLava = DensityFunctions.noise(noiseParams.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction temperature = DensityFunctions.flatCache(new NoiseWrapper.Marker(new HolderNoise(noise.getOrThrow(RTFClimateNoise.TEMPERATURE)).map(-1.0D, 1.0D)));// DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseParams.getOrThrow(Noises.TEMPERATURE));
        DensityFunction moisture = DensityFunctions.flatCache(new NoiseWrapper.Marker(new HolderNoise(noise.getOrThrow(RTFClimateNoise.MOISTURE)).map(-1.0D, 1.0D)));// DensityFunctions.shiftedNoise2d(shiftX, shiftZ, 0.25, noiseParams.getOrThrow(Noises.VEGETATION));
        DensityFunction depth = new YGradient(DensityFunctions.constant(0.0D / 256.0D), DensityFunctions.constant(preset.terrain().general.yScale), 1);
        DensityFunction slopedCheese = new YGradient(getFunction(functions, HEIGHT), DensityFunctions.constant(preset.terrain().general.yScale), 1);
        DensityFunction slopedCaveSelector = DensityFunctions.rangeChoice(slideOverworld(slopedCheese), -1000000.0, 1.5625, slideOverworld(slopedCheese), postProcess(slideOverworld(underground(functions, noiseParams, slopedCheese))));
        slopedCaveSelector = DensityFunctions.min(slopedCaveSelector, getFunction(functions, NOODLE));
        DensityFunction y = getFunction(functions, Y);
        int veinMin = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int veinMax = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction oreVeinness = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), veinMin, veinMax, 0);
        DensityFunction oreVeinA = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), veinMin, veinMax, 0).abs();
        DensityFunction oreVeinB = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), veinMin, veinMax, 0).abs();
        DensityFunction oreVeinSelector = DensityFunctions.add(DensityFunctions.constant(-0.08f), DensityFunctions.max(oreVeinA, oreVeinB));
        DensityFunction oreGap = DensityFunctions.noise(noiseParams.getOrThrow(Noises.ORE_GAP));
        DensityFunction densityFunction9 = getFunction(functions, FACTOR);
        DensityFunction densityFunction10 = getFunction(functions, DEPTH);
        DensityFunction densityFunction11 = noiseGradientDensity(DensityFunctions.cache2d(densityFunction9), densityFunction10);
        DensityFunction initialDensity = new YGradient(DensityFunctions.constant(100.0D / 256.0D), DensityFunctions.constant(preset.terrain().general.yScale), 1);// slideOverworld(DensityFunctions.add(densityFunction11, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0));
        return new NoiseRouter(aquiferBarrier, aquiferFluidLevelFloodedness, aquiferFluidLevelSpread, aquiferLava, temperature, moisture, DensityFunctions.add(getFunction(functions, CONTINENTS), DensityFunctions.constant(0.28D)), getFunction(functions, EROSION), depth, getFunction(functions, RIDGES), initialDensity, slopedCaveSelector, oreVeinness, oreVeinSelector, oreGap);
    }
    
	private static DensityFunction finalDensity(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction terrain) {
		DensityFunction heightGradient = heightGradient(terrain);
		DensityFunction heightCaveChoice = DensityFunctions.rangeChoice(terrain, -1000000.0D, 1.5625D, heightGradient, postProcess(underground(densityFunctions, noiseParams, heightGradient)));
        return DensityFunctions.min(slideOverworld(heightCaveChoice), getFunction(densityFunctions, NOODLE));
	}
	
    private static DensityFunction heightGradient(DensityFunction base) {
    	DensityFunction heightGradient = DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 384, 1.5D, -1.5D), base);
    	DensityFunction densityGradient = noiseGradientDensity(base, heightGradient);
    	return DensityFunctions.add(densityGradient, base);
    }

    private static DensityFunction slideOverworld(DensityFunction density) {
        return slide(density, -64, 384, 80, 64, -0.078125, 0, 24, 0.1171875);
    }

    private static DensityFunction splineWithBlending(DensityFunction densityFunction, DensityFunction densityFunction2) {
        DensityFunction densityFunction3 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), densityFunction2, densityFunction);
        return DensityFunctions.flatCache(DensityFunctions.cache2d(densityFunction3));
    }

    private static DensityFunction noiseGradientDensity(DensityFunction densityFunction, DensityFunction densityFunction2) {
        DensityFunction densityFunction3 = DensityFunctions.mul(densityFunction2, densityFunction);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), densityFunction3.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction y, DensityFunction noise, int i, int j, int k) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(y, i, j + 1, noise, DensityFunctions.constant(k)));
    }

    private static DensityFunction slide(DensityFunction density, int i, int j, int k, int l, double d, int m, int n, double e) {
        DensityFunction densityFunction2 = density;
        DensityFunction densityFunction3 = DensityFunctions.yClampedGradient(i + j - k, i + j - l, 1.0, 0.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction3, d, densityFunction2);
        DensityFunction densityFunction4 = DensityFunctions.yClampedGradient(i + m, i + n, 0.0, 1.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction4, e, densityFunction2);
        return densityFunction2;
    }

    protected static final class QuantizedSpaghettiRarity {
        protected QuantizedSpaghettiRarity() {
        }

        protected static double getSphaghettiRarity2D(double d) {
            if (d < -0.75) {
                return 0.5;
            }
            if (d < -0.5) {
                return 0.75;
            }
            if (d < 0.5) {
                return 1.0;
            }
            if (d < 0.75) {
                return 2.0;
            }
            return 3.0;
        }

        protected static double getSpaghettiRarity3D(double d) {
            if (d < -0.5) {
                return 0.75;
            }
            if (d < 0.0) {
                return 1.0;
            }
            if (d < 0.5) {
                return 1.5;
            }
            return 2.0;
        }
    }
}

