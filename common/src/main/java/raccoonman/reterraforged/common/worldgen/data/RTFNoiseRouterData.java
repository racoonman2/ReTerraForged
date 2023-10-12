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

public final class RTFNoiseRouterData {
    private static final ResourceKey<DensityFunction> Y = createKey("y");
    @Deprecated
    private static final ResourceKey<DensityFunction> SHIFT_X = createKey("shift_x");
    @Deprecated
    private static final ResourceKey<DensityFunction> SHIFT_Z = createKey("shift_z");
    public static final ResourceKey<DensityFunction> CONTINENTS = createKey("overworld/continents");
    public static final ResourceKey<DensityFunction> EROSION = createKey("overworld/erosion");
    public static final ResourceKey<DensityFunction> RIDGES = createKey("overworld/ridges");
    public static final ResourceKey<DensityFunction> DEPTH = createKey("overworld/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("overworld/sloped_cheese");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> NOODLE = createKey("overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = createKey("overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("overworld/caves/spaghetti_2d_thickness_modulator");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("overworld/caves/spaghetti_2d");

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
        DensityFunction densityFunction = registerAndWrap(ctx, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(holderGetter.getOrThrow(Noises.SHIFT)))));
        DensityFunction densityFunction2 = registerAndWrap(ctx, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(holderGetter.getOrThrow(Noises.SHIFT)))));
        ctx.register(CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.CONTINENTALNESS))));
        ctx.register(EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.EROSION))));
        ctx.register(RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.RIDGE))));
        DensityFunction height = new FlatCache.Marker(new NoiseWrapper.Marker(new HolderNoise(noise.getOrThrow(RTFNoiseData.HEIGHT))), 1);
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
    	return new YScale(height, -63, 256, 0.0078125D);// DensityFunctions.mul(DensityFunctions.mul(DensityFunctions.add(height, DensityFunctions.constant(-63.0D / 256.0D)), DensityFunctions.constant(256.0D)), DensityFunctions.constant(blockUnit));
    }

    private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> bootstapContext, ResourceKey<DensityFunction> resourceKey, DensityFunction densityFunction) {
        return new DensityFunctions.HolderHolder(bootstapContext.register(resourceKey, densityFunction));
    }

    private static DensityFunction getFunction(HolderGetter<DensityFunction> holderGetter, ResourceKey<DensityFunction> resourceKey) {
        return new DensityFunctions.HolderHolder(holderGetter.getOrThrow(resourceKey));
    }

    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> holderGetter) {
        DensityFunction densityFunction = DensityFunctions.noise(holderGetter.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction densityFunction2 = DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityFunction2, DensityFunctions.add(densityFunction.abs(), DensityFunctions.constant(-0.4))));
    }

    private static DensityFunction noodle(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        DensityFunction densityFunction = getFunction(holderGetter, Y);
        DensityFunction densityFunction2 = yLimitedInterpolatable(densityFunction, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
        DensityFunction densityFunction3 = yLimitedInterpolatable(densityFunction, DensityFunctions.mappedNoise(holderGetter2.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
        DensityFunction densityFunction4 = yLimitedInterpolatable(densityFunction, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction densityFunction5 = yLimitedInterpolatable(densityFunction, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction densityFunction6 = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(densityFunction4.abs(), densityFunction5.abs()));
        return DensityFunctions.rangeChoice(densityFunction2, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(densityFunction3, densityFunction6));
    }

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> holderGetter) {
        DensityFunction densityFunction = DensityFunctions.noise(holderGetter.getOrThrow(Noises.PILLAR), 25.0, 0.3);
        DensityFunction densityFunction2 = DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.PILLAR_RARENESS), 0.0, -2.0);
        DensityFunction densityFunction3 = DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.PILLAR_THICKNESS), 0.0, 1.1);
        DensityFunction densityFunction4 = DensityFunctions.add(DensityFunctions.mul(densityFunction, DensityFunctions.constant(2.0)), densityFunction2);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityFunction4, densityFunction3.cube()));
    }

    private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        DensityFunction densityFunction = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0, 1.0);
        DensityFunction densityFunction2 = DensityFunctions.weirdScaledSampler(densityFunction, holderGetter2.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
        DensityFunction densityFunction3 = DensityFunctions.mappedNoise(holderGetter2.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0, Math.floorDiv(-64, 8), 8.0);
        DensityFunction densityFunction4 = getFunction(holderGetter, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction densityFunction5 = DensityFunctions.add(densityFunction3, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction densityFunction6 = DensityFunctions.add(densityFunction5, densityFunction4).cube();
        DensityFunction densityFunction7 = DensityFunctions.add(densityFunction2, DensityFunctions.mul(DensityFunctions.constant(0.083), densityFunction4));
        return DensityFunctions.max(densityFunction7, densityFunction6).clamp(-1.0, 1.0);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2, DensityFunction densityFunction) {
        DensityFunction densityFunction2 = getFunction(holderGetter, SPAGHETTI_2D);
        DensityFunction densityFunction3 = getFunction(holderGetter, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityFunction4 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction densityFunction5 = DensityFunctions.mul(DensityFunctions.constant(4.0), densityFunction4.square());
        DensityFunction densityFunction6 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction densityFunction7 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), densityFunction6).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), densityFunction)).clamp(0.0, 0.5));
        DensityFunction densityFunction8 = DensityFunctions.add(densityFunction5, densityFunction7);
        DensityFunction densityFunction9 = DensityFunctions.min(densityFunction8, DensityFunctions.add(densityFunction2, densityFunction3));
        DensityFunction densityFunction10 = getFunction(holderGetter, PILLARS);
        DensityFunction densityFunction11 = DensityFunctions.rangeChoice(densityFunction10, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), densityFunction10);
        return DensityFunctions.max(densityFunction9, densityFunction11);
    }

    private static DensityFunction postProcess(DensityFunction densityFunction) {
        DensityFunction densityFunction2 = DensityFunctions.blendDensity(densityFunction);
        return DensityFunctions.mul(DensityFunctions.interpolated(densityFunction2), DensityFunctions.constant(0.64)).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2, Preset preset) {
        DensityFunction densityFunction = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction densityFunction2 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction densityFunction3 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction densityFunction4 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction densityFunction5 = getFunction(holderGetter, SHIFT_X);
        DensityFunction densityFunction6 = getFunction(holderGetter, SHIFT_Z);
        DensityFunction densityFunction7 = DensityFunctions.shiftedNoise2d(densityFunction5, densityFunction6, 0.25, holderGetter2.getOrThrow(Noises.TEMPERATURE));
        DensityFunction densityFunction8 = DensityFunctions.shiftedNoise2d(densityFunction5, densityFunction6, 0.25, holderGetter2.getOrThrow(Noises.VEGETATION));
        DensityFunction depth = getFunction(holderGetter, DEPTH);
        DensityFunction initialDensity = noiseGradientDensity(DensityFunctions.constant(1.0D), depth);
        DensityFunction densityFunction12 = getFunction(holderGetter, SLOPED_CHEESE);
        DensityFunction densityFunction14 = DensityFunctions.rangeChoice(densityFunction12, -1000000.0, 1.5625, densityFunction12, postProcess(underground(holderGetter, holderGetter2, densityFunction12)));
        DensityFunction densityFunction15 = DensityFunctions.min(slideOverworld(densityFunction14), getFunction(holderGetter, NOODLE));
        DensityFunction densityFunction16 = getFunction(holderGetter, Y);
        int minOreVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int maxOreVeinY = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction densityFunction17 = yLimitedInterpolatable(densityFunction16, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), minOreVeinY, maxOreVeinY, 0);
        DensityFunction densityFunction18 = yLimitedInterpolatable(densityFunction16, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), minOreVeinY, maxOreVeinY, 0).abs();
        DensityFunction densityFunction19 = yLimitedInterpolatable(densityFunction16, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), minOreVeinY, maxOreVeinY, 0).abs();
        DensityFunction densityFunction20 = DensityFunctions.add(DensityFunctions.constant(-0.08f), DensityFunctions.max(densityFunction18, densityFunction19));
        DensityFunction densityFunction21 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(densityFunction, densityFunction2, densityFunction3, densityFunction4, densityFunction7, densityFunction8, getFunction(holderGetter, CONTINENTS), getFunction(holderGetter, EROSION), depth, getFunction(holderGetter, RIDGES), slideOverworld(initialDensity.clamp(-64.0, 64.0)), densityFunction15, densityFunction17, densityFunction20, densityFunction21);
    }

    //TODO let the base gradient lerp to infinity so we don't cut off high terrain
    private static DensityFunction slideOverworld(DensityFunction densityFunction) {
        return slide(densityFunction, -64, DimensionType.MAX_Y, 80, 64, -0.078125, 0, 24, 0.1171875);
    }

    private static DensityFunction noiseGradientDensity(DensityFunction densityFunction, DensityFunction densityFunction2) {
        DensityFunction densityFunction3 = DensityFunctions.mul(densityFunction2, densityFunction);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), densityFunction3.quarterNegative());
    }

    private static DensityFunction yLimitedInterpolatable(DensityFunction densityFunction, DensityFunction densityFunction2, int i, int j, int k) {
        return DensityFunctions.interpolated(DensityFunctions.rangeChoice(densityFunction, i, j + 1, densityFunction2, DensityFunctions.constant(k)));
    }

    private static DensityFunction slide(DensityFunction densityFunction, int i, int j, int k, int l, double d, int m, int n, double e) {
        DensityFunction densityFunction2 = densityFunction;
        DensityFunction densityFunction3 = DensityFunctions.yClampedGradient(i + j - k, i + j - l, 1.0, 0.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction3, d, densityFunction2);
        DensityFunction densityFunction4 = DensityFunctions.yClampedGradient(i + m, i + n, 0.0, 1.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction4, e, densityFunction2);
        return densityFunction2;
    }
}

