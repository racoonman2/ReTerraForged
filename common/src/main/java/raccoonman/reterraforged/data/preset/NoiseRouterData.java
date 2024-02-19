package raccoonman.reterraforged.data.preset;

import java.util.stream.Stream;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.TerrainProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.OreVeinifier;
import net.minecraft.world.level.levelgen.synth.BlendedNoise;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public class NoiseRouterData {
    public static final float GLOBAL_OFFSET = -0.50375f;
    private static final float ORE_THICKNESS = 0.08f;
    private static final double VEININESS_FREQUENCY = 1.5;
    private static final double NOODLE_SPACING_AND_STRAIGHTNESS = 1.5;
    private static final double SURFACE_DENSITY_THRESHOLD = 1.5625;
    private static final double CHEESE_NOISE_TARGET = -0.703125;
    public static final int ISLAND_CHUNK_DISTANCE = 64;
    public static final long ISLAND_CHUNK_DISTANCE_SQR = 4096L;
    private static final DensityFunction BLENDING_FACTOR = DensityFunctions.constant(10.0);
    private static final DensityFunction BLENDING_JAGGEDNESS = DensityFunctions.zero();
    private static final ResourceKey<DensityFunction> ZERO = NoiseRouterData.createKey("zero");
    public static final ResourceKey<DensityFunction> Y = NoiseRouterData.createKey("y");
    private static final ResourceKey<DensityFunction> SHIFT_X = NoiseRouterData.createKey("shift_x");
    private static final ResourceKey<DensityFunction> SHIFT_Z = NoiseRouterData.createKey("shift_z");
    public static final ResourceKey<DensityFunction> BASE_3D_NOISE_OVERWORLD = NoiseRouterData.createKey("overworld/base_3d_noise");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_NETHER = NoiseRouterData.createKey("nether/base_3d_noise");
    private static final ResourceKey<DensityFunction> BASE_3D_NOISE_END = NoiseRouterData.createKey("end/base_3d_noise");
    public static final ResourceKey<DensityFunction> CONTINENTS = NoiseRouterData.createKey("overworld/continents");
    public static final ResourceKey<DensityFunction> EROSION = NoiseRouterData.createKey("overworld/erosion");
    public static final ResourceKey<DensityFunction> RIDGES = NoiseRouterData.createKey("overworld/ridges");
    public static final ResourceKey<DensityFunction> RIDGES_FOLDED = NoiseRouterData.createKey("overworld/ridges_folded");
    public static final ResourceKey<DensityFunction> OFFSET = NoiseRouterData.createKey("overworld/offset");
    public static final ResourceKey<DensityFunction> FACTOR = NoiseRouterData.createKey("overworld/factor");
    public static final ResourceKey<DensityFunction> JAGGEDNESS = NoiseRouterData.createKey("overworld/jaggedness");
    public static final ResourceKey<DensityFunction> DEPTH = NoiseRouterData.createKey("overworld/depth");
    public static final ResourceKey<DensityFunction> SLOPED_CHEESE = NoiseRouterData.createKey("overworld/sloped_cheese");
    public static final ResourceKey<DensityFunction> CONTINENTS_LARGE = NoiseRouterData.createKey("overworld_large_biomes/continents");
    public static final ResourceKey<DensityFunction> EROSION_LARGE = NoiseRouterData.createKey("overworld_large_biomes/erosion");
    private static final ResourceKey<DensityFunction> OFFSET_LARGE = NoiseRouterData.createKey("overworld_large_biomes/offset");
    private static final ResourceKey<DensityFunction> FACTOR_LARGE = NoiseRouterData.createKey("overworld_large_biomes/factor");
    private static final ResourceKey<DensityFunction> JAGGEDNESS_LARGE = NoiseRouterData.createKey("overworld_large_biomes/jaggedness");
    private static final ResourceKey<DensityFunction> DEPTH_LARGE = NoiseRouterData.createKey("overworld_large_biomes/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_LARGE = NoiseRouterData.createKey("overworld_large_biomes/sloped_cheese");
    private static final ResourceKey<DensityFunction> OFFSET_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/offset");
    private static final ResourceKey<DensityFunction> FACTOR_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/factor");
    private static final ResourceKey<DensityFunction> JAGGEDNESS_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/jaggedness");
    private static final ResourceKey<DensityFunction> DEPTH_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/depth");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_AMPLIFIED = NoiseRouterData.createKey("overworld_amplified/sloped_cheese");
    private static final ResourceKey<DensityFunction> SLOPED_CHEESE_END = NoiseRouterData.createKey("end/sloped_cheese");
    public static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = NoiseRouterData.createKey("overworld/caves/spaghetti_roughness_function");
    public static final ResourceKey<DensityFunction> ENTRANCES = NoiseRouterData.createKey("overworld/caves/entrances");
    public static final ResourceKey<DensityFunction> NOODLE = NoiseRouterData.createKey("overworld/caves/noodle");
    public static final ResourceKey<DensityFunction> PILLARS = NoiseRouterData.createKey("overworld/caves/pillars");
    public static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = NoiseRouterData.createKey("overworld/caves/spaghetti_2d_thickness_modulator");
    public static final ResourceKey<DensityFunction> SPAGHETTI_2D = NoiseRouterData.createKey("overworld/caves/spaghetti_2d");

    private static ResourceKey<DensityFunction> createKey(String string) {
        return ResourceKey.create(Registries.DENSITY_FUNCTION, new ResourceLocation(string));
    }

    public static Holder<? extends DensityFunction> bootstrap(BootstapContext<DensityFunction> bootstapContext) {
        HolderGetter<NormalNoise.NoiseParameters> holderGetter = bootstapContext.lookup(Registries.NOISE);
        HolderGetter<DensityFunction> holderGetter2 = bootstapContext.lookup(Registries.DENSITY_FUNCTION);
        bootstapContext.register(ZERO, DensityFunctions.zero());
        int i = DimensionType.MIN_Y * 2;
        int j = DimensionType.MAX_Y * 2;
        bootstapContext.register(Y, DensityFunctions.yClampedGradient(i, j, i, j));
        DensityFunction densityFunction = NoiseRouterData.registerAndWrap(bootstapContext, SHIFT_X, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftA(holderGetter.getOrThrow(Noises.SHIFT)))));
        DensityFunction densityFunction2 = NoiseRouterData.registerAndWrap(bootstapContext, SHIFT_Z, DensityFunctions.flatCache(DensityFunctions.cache2d(DensityFunctions.shiftB(holderGetter.getOrThrow(Noises.SHIFT)))));
        bootstapContext.register(BASE_3D_NOISE_OVERWORLD, BlendedNoise.createUnseeded(0.25, 0.125, 80.0, 160.0, 8.0));
        bootstapContext.register(BASE_3D_NOISE_NETHER, BlendedNoise.createUnseeded(0.25, 0.375, 80.0, 60.0, 8.0));
        bootstapContext.register(BASE_3D_NOISE_END, BlendedNoise.createUnseeded(0.25, 0.25, 80.0, 160.0, 4.0));
        Holder.Reference<DensityFunction> holder = bootstapContext.register(CONTINENTS, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.CONTINENTALNESS))));
        Holder.Reference<DensityFunction> holder2 = bootstapContext.register(EROSION, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.EROSION))));
        DensityFunction densityFunction3 = NoiseRouterData.registerAndWrap(bootstapContext, RIDGES, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.RIDGE))));
        bootstapContext.register(RIDGES_FOLDED, NoiseRouterData.peaksAndValleys(densityFunction3));
        DensityFunction densityFunction4 = DensityFunctions.noise(holderGetter.getOrThrow(Noises.JAGGED), 1500.0, 0.0);
        NoiseRouterData.registerTerrainNoises(bootstapContext, holderGetter2, densityFunction4, holder, holder2, OFFSET, FACTOR, JAGGEDNESS, DEPTH, SLOPED_CHEESE, false);
        Holder.Reference<DensityFunction> holder3 = bootstapContext.register(CONTINENTS_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.CONTINENTALNESS_LARGE))));
        Holder.Reference<DensityFunction> holder4 = bootstapContext.register(EROSION_LARGE, DensityFunctions.flatCache(DensityFunctions.shiftedNoise2d(densityFunction, densityFunction2, 0.25, holderGetter.getOrThrow(Noises.EROSION_LARGE))));
        NoiseRouterData.registerTerrainNoises(bootstapContext, holderGetter2, densityFunction4, holder3, holder4, OFFSET_LARGE, FACTOR_LARGE, JAGGEDNESS_LARGE, DEPTH_LARGE, SLOPED_CHEESE_LARGE, false);
        NoiseRouterData.registerTerrainNoises(bootstapContext, holderGetter2, densityFunction4, holder, holder2, OFFSET_AMPLIFIED, FACTOR_AMPLIFIED, JAGGEDNESS_AMPLIFIED, DEPTH_AMPLIFIED, SLOPED_CHEESE_AMPLIFIED, true);
        bootstapContext.register(SLOPED_CHEESE_END, DensityFunctions.add(DensityFunctions.endIslands(0L), NoiseRouterData.getFunction(holderGetter2, BASE_3D_NOISE_END)));
        bootstapContext.register(SPAGHETTI_ROUGHNESS_FUNCTION, NoiseRouterData.spaghettiRoughnessFunction(holderGetter));
        bootstapContext.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
        bootstapContext.register(SPAGHETTI_2D, NoiseRouterData.spaghetti2D(holderGetter2, holderGetter));
        bootstapContext.register(ENTRANCES, NoiseRouterData.entrances(holderGetter2, holderGetter));
        bootstapContext.register(NOODLE, NoiseRouterData.noodle(holderGetter2, holderGetter));
        return bootstapContext.register(PILLARS, NoiseRouterData.pillars(holderGetter));
    }

    private static void registerTerrainNoises(BootstapContext<DensityFunction> bootstapContext, HolderGetter<DensityFunction> holderGetter, DensityFunction densityFunction, Holder<DensityFunction> holder, Holder<DensityFunction> holder2, ResourceKey<DensityFunction> resourceKey, ResourceKey<DensityFunction> resourceKey2, ResourceKey<DensityFunction> resourceKey3, ResourceKey<DensityFunction> resourceKey4, ResourceKey<DensityFunction> resourceKey5, boolean bl) {
        DensityFunctions.Spline.Coordinate coordinate = new DensityFunctions.Spline.Coordinate(holder);
        DensityFunctions.Spline.Coordinate coordinate2 = new DensityFunctions.Spline.Coordinate(holder2);
        DensityFunctions.Spline.Coordinate coordinate3 = new DensityFunctions.Spline.Coordinate(holderGetter.getOrThrow(RIDGES));
        DensityFunctions.Spline.Coordinate coordinate4 = new DensityFunctions.Spline.Coordinate(holderGetter.getOrThrow(RIDGES_FOLDED));
        DensityFunction densityFunction2 = NoiseRouterData.registerAndWrap(bootstapContext, resourceKey, NoiseRouterData.splineWithBlending(DensityFunctions.add(DensityFunctions.constant(-0.50375f), DensityFunctions.spline(TerrainProvider.overworldOffset(coordinate, coordinate2, coordinate4, bl))), DensityFunctions.blendOffset()));
        DensityFunction densityFunction3 = NoiseRouterData.registerAndWrap(bootstapContext, resourceKey2, NoiseRouterData.splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldFactor(coordinate, coordinate2, coordinate3, coordinate4, bl)), BLENDING_FACTOR));
        DensityFunction densityFunction4 = NoiseRouterData.registerAndWrap(bootstapContext, resourceKey4, DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 320, 1.5, -1.5), densityFunction2));
        DensityFunction densityFunction5 = NoiseRouterData.registerAndWrap(bootstapContext, resourceKey3, NoiseRouterData.splineWithBlending(DensityFunctions.spline(TerrainProvider.overworldJaggedness(coordinate, coordinate2, coordinate3, coordinate4, bl)), BLENDING_JAGGEDNESS));
        DensityFunction densityFunction6 = DensityFunctions.mul(densityFunction5, densityFunction.halfNegative());
        DensityFunction densityFunction7 = NoiseRouterData.noiseGradientDensity(densityFunction3, DensityFunctions.add(densityFunction4, densityFunction6));
        bootstapContext.register(resourceKey5, DensityFunctions.add(densityFunction7, NoiseRouterData.getFunction(holderGetter, BASE_3D_NOISE_OVERWORLD)));
    }

    public static DensityFunction registerAndWrap(BootstapContext<DensityFunction> bootstapContext, ResourceKey<DensityFunction> resourceKey, DensityFunction densityFunction) {
        return new DensityFunctions.HolderHolder(bootstapContext.register(resourceKey, densityFunction));
    }

    public static DensityFunction getFunction(HolderGetter<DensityFunction> holderGetter, ResourceKey<DensityFunction> resourceKey) {
        return new DensityFunctions.HolderHolder(holderGetter.getOrThrow(resourceKey));
    }

    private static DensityFunction peaksAndValleys(DensityFunction densityFunction) {
        return DensityFunctions.mul(DensityFunctions.add(DensityFunctions.add(densityFunction.abs(), DensityFunctions.constant(-0.6666666666666666)).abs(), DensityFunctions.constant(-0.3333333333333333)), DensityFunctions.constant(-3.0));
    }

    public static float peaksAndValleys(float f) {
        return -(Math.abs(Math.abs(f) - 0.6666667f) - 0.33333334f) * 3.0f;
    }

    private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> holderGetter) {
        DensityFunction densityFunction = DensityFunctions.noise(holderGetter.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
        DensityFunction densityFunction2 = DensityFunctions.mappedNoise(holderGetter.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0, -0.1);
        return DensityFunctions.cacheOnce(DensityFunctions.mul(densityFunction2, DensityFunctions.add(densityFunction.abs(), DensityFunctions.constant(-0.4))));
    }

    public static DensityFunction entrances(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        DensityFunction densityFunction = DensityFunctions.cacheOnce(DensityFunctions.noise(holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
        DensityFunction densityFunction2 = DensityFunctions.mappedNoise(holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
        DensityFunction densityFunction3 = DensityFunctions.weirdScaledSampler(densityFunction, holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction densityFunction4 = DensityFunctions.weirdScaledSampler(densityFunction, holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction densityFunction5 = DensityFunctions.add(DensityFunctions.max(densityFunction3, densityFunction4), densityFunction2).clamp(-1.0, 1.0);
        DensityFunction densityFunction6 = NoiseRouterData.getFunction(holderGetter, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityFunction7 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
        DensityFunction densityFunction8 = DensityFunctions.add(DensityFunctions.add(densityFunction7, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0));
        return DensityFunctions.cacheOnce(DensityFunctions.min(densityFunction8, DensityFunctions.add(densityFunction6, densityFunction5)));
    }

    private static DensityFunction noodle(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        DensityFunction densityFunction = NoiseRouterData.getFunction(holderGetter, Y);
        int i = -64;
        int j = -60;
        int k = 320;
        DensityFunction densityFunction2 = NoiseRouterData.yLimitedInterpolatable(densityFunction, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 320, -1);
        DensityFunction densityFunction3 = NoiseRouterData.yLimitedInterpolatable(densityFunction, DensityFunctions.mappedNoise(holderGetter2.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 320, 0);
        double d = 2.6666666666666665;
        DensityFunction densityFunction4 = NoiseRouterData.yLimitedInterpolatable(densityFunction, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction densityFunction5 = NoiseRouterData.yLimitedInterpolatable(densityFunction, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 320, 0);
        DensityFunction densityFunction6 = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(densityFunction4.abs(), densityFunction5.abs()));
        return DensityFunctions.rangeChoice(densityFunction2, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(densityFunction3, densityFunction6));
    }

    private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> holderGetter) {
        double d = 25.0;
        double e = 0.3;
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
        DensityFunction densityFunction4 = NoiseRouterData.getFunction(holderGetter, SPAGHETTI_2D_THICKNESS_MODULATOR);
        DensityFunction densityFunction5 = DensityFunctions.add(densityFunction3, DensityFunctions.yClampedGradient(-64, 320, 8.0, -40.0)).abs();
        DensityFunction densityFunction6 = DensityFunctions.add(densityFunction5, densityFunction4).cube();
        double d = 0.083;
        DensityFunction densityFunction7 = DensityFunctions.add(densityFunction2, DensityFunctions.mul(DensityFunctions.constant(0.083), densityFunction4));
        return DensityFunctions.max(densityFunction7, densityFunction6).clamp(-1.0, 1.0);
    }

    public static DensityFunction underground(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2, DensityFunction densityFunction) {
        DensityFunction densityFunction2 = NoiseRouterData.getFunction(holderGetter, SPAGHETTI_2D);
        DensityFunction densityFunction3 = NoiseRouterData.getFunction(holderGetter, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityFunction4 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction densityFunction5 = DensityFunctions.mul(DensityFunctions.constant(4.0), densityFunction4.square());
        DensityFunction densityFunction6 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction densityFunction7 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), densityFunction6).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), densityFunction)).clamp(0.0, 0.5));
        DensityFunction densityFunction8 = DensityFunctions.add(densityFunction5, densityFunction7);
        DensityFunction densityFunction9 = DensityFunctions.min(DensityFunctions.min(densityFunction8, NoiseRouterData.getFunction(holderGetter, ENTRANCES)), DensityFunctions.add(densityFunction2, densityFunction3));
        DensityFunction densityFunction10 = NoiseRouterData.getFunction(holderGetter, PILLARS);
        DensityFunction densityFunction11 = DensityFunctions.rangeChoice(densityFunction10, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), densityFunction10);
        return DensityFunctions.max(densityFunction9, densityFunction11);
    }

    public static DensityFunction postProcess(DensityFunction densityFunction) {
        DensityFunction densityFunction2 = DensityFunctions.blendDensity(densityFunction);
        return DensityFunctions.mul(DensityFunctions.interpolated(densityFunction2), DensityFunctions.constant(0.64)).squeeze();
    }

    protected static NoiseRouter overworld(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2, boolean bl, boolean bl2) {
        DensityFunction densityFunction = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_BARRIER), 0.5);
        DensityFunction densityFunction2 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_FLOODEDNESS), 0.67);
        DensityFunction densityFunction3 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_FLUID_LEVEL_SPREAD), 0.7142857142857143);
        DensityFunction densityFunction4 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.AQUIFER_LAVA));
        DensityFunction densityFunction5 = NoiseRouterData.getFunction(holderGetter, SHIFT_X);
        DensityFunction densityFunction6 = NoiseRouterData.getFunction(holderGetter, SHIFT_Z);
        DensityFunction densityFunction7 = DensityFunctions.shiftedNoise2d(densityFunction5, densityFunction6, 0.25, holderGetter2.getOrThrow(bl ? Noises.TEMPERATURE_LARGE : Noises.TEMPERATURE));
        DensityFunction densityFunction8 = DensityFunctions.shiftedNoise2d(densityFunction5, densityFunction6, 0.25, holderGetter2.getOrThrow(bl ? Noises.VEGETATION_LARGE : Noises.VEGETATION));
        DensityFunction densityFunction9 = NoiseRouterData.getFunction(holderGetter, bl ? FACTOR_LARGE : (bl2 ? FACTOR_AMPLIFIED : FACTOR));
        DensityFunction densityFunction10 = NoiseRouterData.getFunction(holderGetter, bl ? DEPTH_LARGE : (bl2 ? DEPTH_AMPLIFIED : DEPTH));
        DensityFunction densityFunction11 = NoiseRouterData.noiseGradientDensity(DensityFunctions.cache2d(densityFunction9), densityFunction10);
        DensityFunction densityFunction12 = NoiseRouterData.getFunction(holderGetter, bl ? SLOPED_CHEESE_LARGE : (bl2 ? SLOPED_CHEESE_AMPLIFIED : SLOPED_CHEESE));
        DensityFunction densityFunction13 = DensityFunctions.min(densityFunction12, DensityFunctions.mul(DensityFunctions.constant(5.0), NoiseRouterData.getFunction(holderGetter, ENTRANCES)));
        DensityFunction densityFunction14 = DensityFunctions.rangeChoice(densityFunction12, -1000000.0, 1.5625, densityFunction13, NoiseRouterData.underground(holderGetter, holderGetter2, densityFunction12));
        DensityFunction densityFunction15 = DensityFunctions.min(NoiseRouterData.postProcess(NoiseRouterData.slideOverworld(bl2, densityFunction14)), NoiseRouterData.getFunction(holderGetter, NOODLE));
        DensityFunction densityFunction16 = NoiseRouterData.getFunction(holderGetter, Y);
        int i = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.minY).min().orElse(-DimensionType.MIN_Y * 2);
        int j = Stream.of(OreVeinifier.VeinType.values()).mapToInt(veinType -> veinType.maxY).max().orElse(-DimensionType.MIN_Y * 2);
        DensityFunction densityFunction17 = NoiseRouterData.yLimitedInterpolatable(densityFunction16, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_VEININESS), 1.5, 1.5), i, j, 0);
        float f = 4.0f;
        DensityFunction densityFunction18 = NoiseRouterData.yLimitedInterpolatable(densityFunction16, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_VEIN_A), 4.0, 4.0), i, j, 0).abs();
        DensityFunction densityFunction19 = NoiseRouterData.yLimitedInterpolatable(densityFunction16, DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_VEIN_B), 4.0, 4.0), i, j, 0).abs();
        DensityFunction densityFunction20 = DensityFunctions.add(DensityFunctions.constant(-0.08f), DensityFunctions.max(densityFunction18, densityFunction19));
        DensityFunction densityFunction21 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.ORE_GAP));
        return new NoiseRouter(densityFunction, densityFunction2, densityFunction3, densityFunction4, densityFunction7, densityFunction8, NoiseRouterData.getFunction(holderGetter, bl ? CONTINENTS_LARGE : CONTINENTS), NoiseRouterData.getFunction(holderGetter, bl ? EROSION_LARGE : EROSION), densityFunction10, NoiseRouterData.getFunction(holderGetter, RIDGES), NoiseRouterData.slideOverworld(bl2, DensityFunctions.add(densityFunction11, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0)), densityFunction15, densityFunction17, densityFunction20, densityFunction21);
    }

    private static NoiseRouter noNewCaves(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2, DensityFunction densityFunction) {
        DensityFunction densityFunction2 = NoiseRouterData.getFunction(holderGetter, SHIFT_X);
        DensityFunction densityFunction3 = NoiseRouterData.getFunction(holderGetter, SHIFT_Z);
        DensityFunction densityFunction4 = DensityFunctions.shiftedNoise2d(densityFunction2, densityFunction3, 0.25, holderGetter2.getOrThrow(Noises.TEMPERATURE));
        DensityFunction densityFunction5 = DensityFunctions.shiftedNoise2d(densityFunction2, densityFunction3, 0.25, holderGetter2.getOrThrow(Noises.VEGETATION));
        DensityFunction densityFunction6 = NoiseRouterData.postProcess(densityFunction);
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityFunction4, densityFunction5, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityFunction6, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    private static DensityFunction slideOverworld(boolean bl, DensityFunction densityFunction) {
        return NoiseRouterData.slide(densityFunction, -64, 384, bl ? 16 : 80, bl ? 0 : 64, -0.078125, 0, 24, bl ? 0.4 : 0.1171875);
    }

    private static DensityFunction slideNetherLike(HolderGetter<DensityFunction> holderGetter, int i, int j) {
        return NoiseRouterData.slide(NoiseRouterData.getFunction(holderGetter, BASE_3D_NOISE_NETHER), i, j, 24, 0, 0.9375, -8, 24, 2.5);
    }

    private static DensityFunction slideEndLike(DensityFunction densityFunction, int i, int j) {
        return NoiseRouterData.slide(densityFunction, i, j, 72, -184, -23.4375, 4, 32, -0.234375);
    }

    protected static NoiseRouter nether(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        return NoiseRouterData.noNewCaves(holderGetter, holderGetter2, NoiseRouterData.slideNetherLike(holderGetter, 0, 128));
    }

    protected static NoiseRouter caves(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        return NoiseRouterData.noNewCaves(holderGetter, holderGetter2, NoiseRouterData.slideNetherLike(holderGetter, -64, 192));
    }

    protected static NoiseRouter floatingIslands(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        return NoiseRouterData.noNewCaves(holderGetter, holderGetter2, NoiseRouterData.slideEndLike(NoiseRouterData.getFunction(holderGetter, BASE_3D_NOISE_END), 0, 256));
    }

    private static DensityFunction slideEnd(DensityFunction densityFunction) {
        return NoiseRouterData.slideEndLike(densityFunction, 0, 128);
    }

    protected static NoiseRouter end(HolderGetter<DensityFunction> holderGetter) {
        DensityFunction densityFunction = DensityFunctions.cache2d(DensityFunctions.endIslands(0L));
        DensityFunction densityFunction2 = NoiseRouterData.postProcess(NoiseRouterData.slideEnd(NoiseRouterData.getFunction(holderGetter, SLOPED_CHEESE_END)));
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), densityFunction, DensityFunctions.zero(), DensityFunctions.zero(), NoiseRouterData.slideEnd(DensityFunctions.add(densityFunction, DensityFunctions.constant(-0.703125))), densityFunction2, DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    protected static NoiseRouter none() {
        return new NoiseRouter(DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero(), DensityFunctions.zero());
    }

    private static DensityFunction splineWithBlending(DensityFunction densityFunction, DensityFunction densityFunction2) {
        DensityFunction densityFunction3 = DensityFunctions.lerp(DensityFunctions.blendAlpha(), densityFunction2, densityFunction);
        return DensityFunctions.flatCache(DensityFunctions.cache2d(densityFunction3));
    }

    public static DensityFunction noiseGradientDensity(DensityFunction densityFunction, DensityFunction densityFunction2) {
        DensityFunction densityFunction3 = DensityFunctions.mul(densityFunction2, densityFunction);
        return DensityFunctions.mul(DensityFunctions.constant(4.0), densityFunction3.quarterNegative());
    }

    public static DensityFunction yLimitedInterpolatable(DensityFunction densityFunction, DensityFunction densityFunction2, int i, int j, int k) {
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

