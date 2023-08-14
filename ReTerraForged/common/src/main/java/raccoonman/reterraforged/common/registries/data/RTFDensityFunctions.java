package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.densityfunctions.HeightDensityFunction;

public final class RTFDensityFunctions {
	public static final ResourceKey<DensityFunction> Y = resolve("y");
	public static final ResourceKey<DensityFunction> SPAGHETTI_2D = resolve("spaghetti_2d");
	public static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = resolve("spaghetti_2d_thickness_modulator");
	public static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = resolve("spaghetti_roughness_function");
	public static final ResourceKey<DensityFunction> PILLARS = resolve("pillars");
	public static final ResourceKey<DensityFunction> INITIAL_DENSITY_WITHOUT_JAGGEDNESS = resolve("initial_density_without_jaggedness");
	public static final ResourceKey<DensityFunction> FINAL_DENSITY = resolve("final_density");
    public static final ResourceKey<DensityFunction> FACTOR = resolve("overworld/factor");
    public static final ResourceKey<DensityFunction> NOODLE = resolve("overworld/caves/noodle");
    public static final ResourceKey<DensityFunction> DEPTH = resolve("overworld/depth");
    public static final ResourceKey<DensityFunction> ENTRANCES = resolve("overworld/caves/entrances");

	public static void register(BootstapContext<DensityFunction> ctx) {
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		
        int i = DimensionType.MIN_Y * 2;
        int j = DimensionType.MAX_Y * 2;
        ctx.register(Y, DensityFunctions.yClampedGradient(i, j, i, j));
		ctx.register(PILLARS, pillars(noiseParams));
		ctx.register(SPAGHETTI_2D, spaghetti2D(densityFunctions, noiseParams));
	    ctx.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
		ctx.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noiseParams));
	    ctx.register(NOODLE, noodle(densityFunctions, noiseParams));
	    ctx.register(DEPTH, DensityFunctions.yClampedGradient(-64, 1024, 1.0F, 0.0F));
	    ctx.register(ENTRANCES, entrances(densityFunctions, noiseParams));

		registerAndWrap(ctx, INITIAL_DENSITY_WITHOUT_JAGGEDNESS, HeightDensityFunction.Marker.INSTANCE);
		registerAndWrap(ctx, FINAL_DENSITY, finalDensity(densityFunctions, noiseParams));
    }
	
	private static DensityFunction finalDensity(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction densityfunction11 = createHeightCaveGradient(HeightDensityFunction.Marker.INSTANCE);
		DensityFunction densityfunction13 = DensityFunctions.rangeChoice(densityfunction11, -1000000.0D, 1.5625D, densityfunction11, postProcess(underground(densityFunctions, noiseParams, densityfunction11)));
        return DensityFunctions.min(slideOverworld(true, densityfunction13), getFunction(densityFunctions, NOODLE));
	}
	
    private static DensityFunction createHeightCaveGradient(DensityFunction base) {
    	DensityFunction densityfunction2 = new DensityFunctions.HolderHolder(Holder.direct(DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 1024, 1.5D, -1.5D), base)));
    	DensityFunction densityfunction5 = noiseGradientDensity(base, densityfunction2);
    	return DensityFunctions.add(densityfunction5, base);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2, DensityFunction densityFunction) {
        DensityFunction densityFunction2 = getFunction(holderGetter, SPAGHETTI_2D);
        DensityFunction densityFunction3 = getFunction(holderGetter, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityFunction4 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction densityFunction5 = DensityFunctions.mul(DensityFunctions.constant(4.0), densityFunction4.square());
        DensityFunction densityFunction6 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction densityFunction7 = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), densityFunction6).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), densityFunction)).clamp(0.0, 0.5));
        DensityFunction densityFunction8 = DensityFunctions.add(densityFunction5, densityFunction7);
        DensityFunction densityFunction9 = DensityFunctions.min(DensityFunctions.min(densityFunction8, getFunction(holderGetter, ENTRANCES)), DensityFunctions.add(densityFunction2, densityFunction3));
        DensityFunction densityFunction10 = getFunction(holderGetter, PILLARS);
        DensityFunction densityFunction11 = DensityFunctions.rangeChoice(densityFunction10, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), densityFunction10);
        return DensityFunctions.max(densityFunction9, densityFunction11);
    }

	private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction densityfunction = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
		DensityFunction densityfunction1 = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0D, -0.1D);
		return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction1, DensityFunctions.add(densityfunction.abs(), DensityFunctions.constant(-0.4D))));
	}

	private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction densityfunction = DensityFunctions.noise(noiseParams.getOrThrow(Noises.PILLAR), 25.0D, 0.3D);
		DensityFunction densityfunction1 = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_RARENESS), 0.0D, -2.0D);
		DensityFunction densityfunction2 = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_THICKNESS), 0.0D, 1.1D);
		DensityFunction densityfunction3 = DensityFunctions.add(DensityFunctions.mul(densityfunction, DensityFunctions.constant(2.0D)), densityfunction1);
		return DensityFunctions.cacheOnce(DensityFunctions.mul(densityfunction3, densityfunction2.cube()));
	}

	private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction densityfunction = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0D, 1.0D);
		DensityFunction densityfunction1 = DensityFunctions.weirdScaledSampler(densityfunction, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
		DensityFunction densityfunction2 = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0D, (double)Math.floorDiv(-64, 8), 8.0D);
		DensityFunction densityfunction3 = getFunction(densityFunctions, SPAGHETTI_2D_THICKNESS_MODULATOR);
		DensityFunction densityfunction4 = DensityFunctions.add(densityfunction2, DensityFunctions.yClampedGradient(-64, 512, 8.0D, -40.0D)).abs();
		DensityFunction densityfunction5 = DensityFunctions.add(densityfunction4, densityfunction3).cube();
		DensityFunction densityfunction6 = DensityFunctions.add(densityfunction1, DensityFunctions.mul(DensityFunctions.constant(0.083D), densityfunction3));
		return DensityFunctions.max(densityfunction6, densityfunction5).clamp(-1.0D, 1.0D);
	}
	
	private static DensityFunction slideOverworld(boolean bl, DensityFunction densityFunction) {
		return slide(densityFunction, -64, 512, bl ? 16 : 80, bl ? 0 : 64, -0.078125, 0, 24, bl ? 0.4 : 0.1171875);
	}
	
	private static DensityFunction slide(DensityFunction densityFunction, int i, int j, int k, int l, double d, int m, int n, double e) {
        DensityFunction densityFunction2 = densityFunction;
        DensityFunction densityFunction3 = DensityFunctions.yClampedGradient(i + j - k, i + j - l, 1.0, 0.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction3, d, densityFunction2);
        DensityFunction densityFunction4 = DensityFunctions.yClampedGradient(i + m, i + n, 0.0, 1.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction4, e, densityFunction2);
        return densityFunction2;
    }
	
	private static DensityFunction entrances(HolderGetter<DensityFunction> holderGetter, HolderGetter<NormalNoise.NoiseParameters> holderGetter2) {
        DensityFunction densityFunction = DensityFunctions.cacheOnce(DensityFunctions.noise(holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_RARITY), 2.0, 1.0));
        DensityFunction densityFunction2 = DensityFunctions.mappedNoise(holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_THICKNESS), -0.065, -0.088);
        DensityFunction densityFunction3 = DensityFunctions.weirdScaledSampler(densityFunction, holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_1), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction densityFunction4 = DensityFunctions.weirdScaledSampler(densityFunction, holderGetter2.getOrThrow(Noises.SPAGHETTI_3D_2), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE1);
        DensityFunction densityFunction5 = DensityFunctions.add(DensityFunctions.max(densityFunction3, densityFunction4), densityFunction2).clamp(-1.0, 1.0);
        DensityFunction densityFunction6 = getFunction(holderGetter, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction densityFunction7 = DensityFunctions.noise(holderGetter2.getOrThrow(Noises.CAVE_ENTRANCE), 0.75, 0.5);
        DensityFunction densityFunction8 = DensityFunctions.add(DensityFunctions.add(densityFunction7, DensityFunctions.constant(0.37)), DensityFunctions.yClampedGradient(-10, 30, 0.3, 0.0));
        return DensityFunctions.cacheOnce(DensityFunctions.min(densityFunction8, DensityFunctions.add(densityFunction6, densityFunction5)));
    }
	
	private static DensityFunction yLimitedInterpolatable(DensityFunction f1, DensityFunction f2, int i, int j, int k) {
		return DensityFunctions.interpolated(DensityFunctions.rangeChoice(f1, i, j + 1, f2, DensityFunctions.constant((double)k)));
	}
	
	private static DensityFunction postProcess(DensityFunction densityFunction) {
		DensityFunction densityFunction2 = DensityFunctions.blendDensity(densityFunction);
		return DensityFunctions.mul(DensityFunctions.interpolated(densityFunction2), DensityFunctions.constant(0.64)).squeeze();
	}
	
	private static DensityFunction noodle(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction densityFunction = getFunction(densityFunctions, Y);
		DensityFunction densityFunction2 = yLimitedInterpolatable(densityFunction, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 512, -1);
		DensityFunction densityFunction3 = yLimitedInterpolatable(densityFunction, DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 512, 0);
		DensityFunction densityFunction4 = yLimitedInterpolatable(densityFunction, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 512, 0);
		DensityFunction densityFunction5 = yLimitedInterpolatable(densityFunction, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 512, 0);
		DensityFunction densityFunction6 = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(densityFunction4.abs(), densityFunction5.abs()));
		return DensityFunctions.rangeChoice(densityFunction2, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(densityFunction3, densityFunction6));
	}
	
	private static DensityFunction noiseGradientDensity(DensityFunction densityFunction, DensityFunction densityFunction2) {
		DensityFunction densityFunction3 = DensityFunctions.mul(densityFunction2, densityFunction);
		return DensityFunctions.mul(DensityFunctions.constant(4.0), densityFunction3.quarterNegative());
	}

	private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction function) {
		return new DensityFunctions.HolderHolder(ctx.register(key, function));
	}
	
	private static ResourceKey<DensityFunction> resolve(String key) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION, key);
	}
	
	private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
		return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
	}
}
