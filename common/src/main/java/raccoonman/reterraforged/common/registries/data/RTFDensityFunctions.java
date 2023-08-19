package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.density.Cache2DFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.density.NoiseDensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.density.YGradientFunction;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;

public final class RTFDensityFunctions {
	public static final ResourceKey<DensityFunction> TEMPERATURE = resolve("overworld/temperature");
	public static final ResourceKey<DensityFunction> HUMIDITY = resolve("overworld/humidity");
	public static final ResourceKey<DensityFunction> CONTINENT = resolve("overworld/continent");
	public static final ResourceKey<DensityFunction> RIVER = resolve("overworld/river");
	public static final ResourceKey<DensityFunction> WEIRDNESS = resolve("overworld/weirdness");
    public static final ResourceKey<DensityFunction> OCEAN = resolve("overworld/ocean");
	public static final ResourceKey<DensityFunction> TERRAIN = resolve("overworld/terrain");
	public static final ResourceKey<DensityFunction> FINAL_DENSITY = resolve("overworld/final_density");
    public static final ResourceKey<DensityFunction> DEPTH = resolve("overworld/depth");
    private static final ResourceKey<DensityFunction> NOODLE = resolve("minecraft:overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = resolve("minecraft:overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = resolve("minecraft:overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = resolve("minecraft:overworld/caves/spaghetti_2d");

	public static void register(BootstapContext<DensityFunction> ctx) {
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		
        ctx.register(TERRAIN, terrain(noise));
        ctx.register(TEMPERATURE, new NoiseDensityFunction.Marker(noise.getOrThrow(RTFNoise.TEMPERATURE)));
        ctx.register(HUMIDITY, new NoiseDensityFunction.Marker(noise.getOrThrow(RTFNoise.HUMIDITY)));
        ctx.register(CONTINENT, new NoiseDensityFunction.Marker(noise.getOrThrow(RTFNoise.CONTINENT)));
        ctx.register(RIVER, new NoiseDensityFunction.Marker(noise.getOrThrow(RTFNoise.RIVER)));
        ctx.register(WEIRDNESS, new NoiseDensityFunction.Marker(noise.getOrThrow(RTFNoise.WEIRDNESS)));
	    ctx.register(OCEAN, new NoiseDensityFunction.Marker(noise.getOrThrow(RTFNoise.OCEAN)));
		ctx.register(FINAL_DENSITY, finalDensity(densityFunctions, noiseParams));
	    ctx.register(DEPTH, DensityFunctions.yClampedGradient(-64, 1024, 1.0F, 0.0F));
   }
	
	private static DensityFunction terrain(HolderGetter<Noise> noise) {
		return new YGradientFunction(new Cache2DFunction.Marker(new NoiseDensityFunction.Marker(noise.getOrThrow(RTFNoise.TERRAIN))));
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

	private static DensityFunction postProcess(DensityFunction densityFunction) {
		DensityFunction blended = DensityFunctions.blendDensity(densityFunction);
		return DensityFunctions.mul(DensityFunctions.interpolated(blended), DensityFunctions.constant(0.64)).squeeze();
	}
	
	private static DensityFunction noiseGradientDensity(DensityFunction base, DensityFunction gradient) {
		return DensityFunctions.mul(DensityFunctions.constant(4.0), DensityFunctions.mul(gradient, base).quarterNegative());
	}

	private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
		return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
	}
	
	private static ResourceKey<DensityFunction> resolve(String key) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION, key);
	}
}
