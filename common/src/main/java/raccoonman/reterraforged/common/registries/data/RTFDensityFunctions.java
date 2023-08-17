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
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainBlender;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public final class RTFDensityFunctions {
	public static final ResourceKey<DensityFunction> TERRAIN = resolve("terrain");
	public static final ResourceKey<DensityFunction> Y = resolve("y");
	public static final ResourceKey<DensityFunction> SPAGHETTI_2D = resolve("spaghetti_2d");
	public static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = resolve("spaghetti_2d_thickness_modulator");
	public static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = resolve("spaghetti_roughness_function");
	public static final ResourceKey<DensityFunction> PILLARS = resolve("pillars");
	public static final ResourceKey<DensityFunction> FINAL_DENSITY = resolve("final_density");
    public static final ResourceKey<DensityFunction> FACTOR = resolve("overworld/factor");
    public static final ResourceKey<DensityFunction> NOODLE = resolve("overworld/caves/noodle");
    public static final ResourceKey<DensityFunction> DEPTH = resolve("overworld/depth");

	public static void register(BootstapContext<DensityFunction> ctx) {
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		
		int minY = DimensionType.MIN_Y * 2;
        int maxY = DimensionType.MAX_Y * 2;
        ctx.register(TERRAIN, terrain(noise));
        ctx.register(Y, DensityFunctions.yClampedGradient(minY, maxY, minY, maxY));
		ctx.register(PILLARS, pillars(noiseParams));
		ctx.register(SPAGHETTI_2D, spaghetti2D(densityFunctions, noiseParams));
	    ctx.register(SPAGHETTI_2D_THICKNESS_MODULATOR, DensityFunctions.cacheOnce(DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_THICKNESS), 2.0, 1.0, -0.6, -1.3)));
		ctx.register(SPAGHETTI_ROUGHNESS_FUNCTION, spaghettiRoughnessFunction(noiseParams));
	    ctx.register(NOODLE, noodle(densityFunctions, noiseParams));
	    ctx.register(DEPTH, DensityFunctions.yClampedGradient(-64, 1024, 1.0F, 0.0F));

		registerAndWrap(ctx, FINAL_DENSITY, finalDensity(densityFunctions, noiseParams));
    }
	
	private static DensityFunction terrain(HolderGetter<Noise> noise) {
		return new TerrainNoise.Marker(WorldSettings.DEFAULT, new TerrainBlender(800, 0.8F, 0.4F, 
			new WeightMap.Builder<>()
				.entry(0.55F, noise.getOrThrow(RTFNoise.STEPPE))
				.entry(0.6F,  noise.getOrThrow(RTFNoise.PLAINS))
				.entry(0.55F, noise.getOrThrow(RTFNoise.HILLS_1))
				.entry(0.55F, noise.getOrThrow(RTFNoise.HILLS_2))
				.entry(0.45F, noise.getOrThrow(RTFNoise.DALES))
				.entry(0.45F, noise.getOrThrow(RTFNoise.PLATEAU))
				.entry(0.65F, noise.getOrThrow(RTFNoise.BADLANDS))
				.entry(0.65F, noise.getOrThrow(RTFNoise.TORRIDONIAN))
				.entry(0.55F, noise.getOrThrow(RTFNoise.MOUNTAINS_1))
				.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_2))
				.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_3))
				.entry(0.45F, noise.getOrThrow(RTFNoise.DOLOMITES))
				.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_1))
				.entry(0.45F, noise.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_2))
				.build()
		));
	}
	
	private static DensityFunction finalDensity(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction heightGradient = heightGradient(getFunction(densityFunctions, TERRAIN));
		DensityFunction heightCaveChoice = DensityFunctions.rangeChoice(heightGradient, -1000000.0D, 1.5625D, heightGradient, postProcess(underground(densityFunctions, noiseParams, heightGradient)));
        return DensityFunctions.min(slideOverworld(heightCaveChoice), getFunction(densityFunctions, NOODLE));
	}
	
    private static DensityFunction heightGradient(DensityFunction base) {
    	DensityFunction heightGradient = new DensityFunctions.HolderHolder(Holder.direct(DensityFunctions.add(DensityFunctions.yClampedGradient(-64, 1024, 1.5D, -1.5D), base)));
    	DensityFunction densityGradient = noiseGradientDensity(base, heightGradient);
    	return DensityFunctions.add(densityGradient, base);
    }

    private static DensityFunction underground(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, DensityFunction heightGradient) {
        DensityFunction spaghetti2d = getFunction(densityFunctions, SPAGHETTI_2D);
        DensityFunction spaghettiRoughnessFunction = getFunction(densityFunctions, SPAGHETTI_ROUGHNESS_FUNCTION);
        DensityFunction caveLayer = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_LAYER), 8.0);
        DensityFunction largeCaves = DensityFunctions.mul(DensityFunctions.constant(4.0), caveLayer.square());
        DensityFunction caveCheese = DensityFunctions.noise(noiseParams.getOrThrow(Noises.CAVE_CHEESE), 0.6666666666666666);
        DensityFunction baseCave = DensityFunctions.add(DensityFunctions.add(DensityFunctions.constant(0.27), caveCheese).clamp(-1.0, 1.0), DensityFunctions.add(DensityFunctions.constant(1.5), DensityFunctions.mul(DensityFunctions.constant(-0.64), heightGradient)).clamp(0.0, 0.5));
        DensityFunction caveGradient = DensityFunctions.add(largeCaves, baseCave);
        DensityFunction allCaves = DensityFunctions.min(caveGradient, DensityFunctions.add(spaghetti2d, spaghettiRoughnessFunction));
        DensityFunction pillars = getFunction(densityFunctions, PILLARS);
        DensityFunction pillarRange = DensityFunctions.rangeChoice(pillars, -1000000.0, 0.03, DensityFunctions.constant(-1000000.0), pillars);
        return DensityFunctions.max(allCaves, pillarRange);
    }

	private static DensityFunction spaghettiRoughnessFunction(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction spaghettiRoughness = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS));
		DensityFunction spaghettiRoughnessModulator = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_ROUGHNESS_MODULATOR), 0.0D, -0.1D);
		return DensityFunctions.cacheOnce(DensityFunctions.mul(spaghettiRoughnessModulator, DensityFunctions.add(spaghettiRoughness.abs(), DensityFunctions.constant(-0.4D))));
	}

	private static DensityFunction pillars(HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction pillar = DensityFunctions.noise(noiseParams.getOrThrow(Noises.PILLAR), 25.0D, 0.3D);
		DensityFunction pillarRareness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_RARENESS), 0.0D, -2.0D);
		DensityFunction pillarThickness = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.PILLAR_THICKNESS), 0.0D, 1.1D);
		DensityFunction pillarRare = DensityFunctions.add(DensityFunctions.mul(pillar, DensityFunctions.constant(2.0D)), pillarRareness);
		return DensityFunctions.cacheOnce(DensityFunctions.mul(pillarRare, pillarThickness.cube()));
	}

	private static DensityFunction spaghetti2D(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction spaghetti2dModulator = DensityFunctions.noise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_MODULATOR), 2.0D, 1.0D);
		DensityFunction spaghetti2d = DensityFunctions.weirdScaledSampler(spaghetti2dModulator, noiseParams.getOrThrow(Noises.SPAGHETTI_2D), DensityFunctions.WeirdScaledSampler.RarityValueMapper.TYPE2);
		DensityFunction spaghetti2dElevation = DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.SPAGHETTI_2D_ELEVATION), 0.0D, (double)Math.floorDiv(-64, 8), 8.0D);
		DensityFunction spaghetti2dThicknessModulator = getFunction(densityFunctions, SPAGHETTI_2D_THICKNESS_MODULATOR);
		DensityFunction elevation = DensityFunctions.add(spaghetti2dElevation, DensityFunctions.yClampedGradient(-64, 512, 8.0D, -40.0D)).abs();
		DensityFunction elevated = DensityFunctions.add(elevation, spaghetti2dThicknessModulator).cube();
		DensityFunction thickness = DensityFunctions.add(spaghetti2d, DensityFunctions.mul(DensityFunctions.constant(0.083D), spaghetti2dThicknessModulator));
		return DensityFunctions.max(thickness, elevated).clamp(-1.0D, 1.0D);
	}
	
	private static DensityFunction slideOverworld(DensityFunction height) {
		return slide(height, -64, 512, 16, 0, -0.078125, 0, 24, 0.4);
	}
	
	private static DensityFunction slide(DensityFunction height, int minY, int maxY, int lowMin, int lowMax, double low, int highMin, int highMax, double high) {
        DensityFunction slide = height;
        DensityFunction lowGradient = DensityFunctions.yClampedGradient(minY + maxY - lowMin, minY + maxY - lowMax, 1.0, 0.0);
        slide = DensityFunctions.lerp(lowGradient, low, slide);
        DensityFunction highGradient = DensityFunctions.yClampedGradient(minY + highMin, minY + highMax, 0.0, 1.0);
        slide = DensityFunctions.lerp(highGradient, high, slide);
        return slide;
    }
	
	private static DensityFunction yLimitedInterpolatable(DensityFunction input, DensityFunction whenInRange, int minInclusive, int maxInclusive, int whenOutOfRange) {
		return DensityFunctions.interpolated(DensityFunctions.rangeChoice(input, minInclusive, maxInclusive + 1, whenInRange, DensityFunctions.constant(whenOutOfRange)));
	}
	
	private static DensityFunction postProcess(DensityFunction densityFunction) {
		DensityFunction blended = DensityFunctions.blendDensity(densityFunction);
		return DensityFunctions.mul(DensityFunctions.interpolated(blended), DensityFunctions.constant(0.64)).squeeze();
	}
	
	private static DensityFunction noodle(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
		DensityFunction y = getFunction(densityFunctions, Y);
		DensityFunction noodle = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE), 1.0, 1.0), -60, 512, -1);
		DensityFunction noodleThickness = yLimitedInterpolatable(y, DensityFunctions.mappedNoise(noiseParams.getOrThrow(Noises.NOODLE_THICKNESS), 1.0, 1.0, -0.05, -0.1), -60, 512, 0);
		DensityFunction noodleRidgeA = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_A), 2.6666666666666665, 2.6666666666666665), -60, 512, 0);
		DensityFunction noodleRidgeB = yLimitedInterpolatable(y, DensityFunctions.noise(noiseParams.getOrThrow(Noises.NOODLE_RIDGE_B), 2.6666666666666665, 2.6666666666666665), -60, 512, 0);
		DensityFunction noodleRidge = DensityFunctions.mul(DensityFunctions.constant(1.5), DensityFunctions.max(noodleRidgeA.abs(), noodleRidgeB.abs()));
		return DensityFunctions.rangeChoice(noodle, -1000000.0, 0.0, DensityFunctions.constant(64.0), DensityFunctions.add(noodleThickness, noodleRidge));
	}
	
	private static DensityFunction noiseGradientDensity(DensityFunction base, DensityFunction gradient) {
		return DensityFunctions.mul(DensityFunctions.constant(4.0), DensityFunctions.mul(gradient, base).quarterNegative());
	}

	private static DensityFunction registerAndWrap(BootstapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction function) {
		return new DensityFunctions.HolderHolder(ctx.register(key, function));
	}
	
	private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
		return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
	}
	
	private static ResourceKey<DensityFunction> resolve(String key) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION, key);
	}
}
