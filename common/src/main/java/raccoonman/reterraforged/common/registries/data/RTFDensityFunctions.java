package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.density.FlatCache;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.density.YGradient;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.data.noise.RTFTerrainNoise;

public final class RTFDensityFunctions {
	public static final ResourceKey<DensityFunction> TEMPERATURE = resolve("overworld/temperature");
	public static final ResourceKey<DensityFunction> HUMIDITY = resolve("overworld/humidity");
	public static final ResourceKey<DensityFunction> CONTINENT = resolve("overworld/continent");
	public static final ResourceKey<DensityFunction> INITIAL_DENSITY = resolve("overworld/initial_density");
	public static final ResourceKey<DensityFunction> FINAL_DENSITY = resolve("overworld/final_density");
	
	public static void register(BootstapContext<DensityFunction> ctx) {                                                                                 
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);

		ctx.register(CONTINENT, DensityFunctions.flatCache(wrapNoise(noise, RTFTerrainNoise.CONTINENT)));
		DensityFunction finalDensity = register(ctx, FINAL_DENSITY, new YGradient(new FlatCache.Marker(wrapNoise(noise, RTFTerrainNoise.HEIGHT))));
		ctx.register(INITIAL_DENSITY, slideOverworld(false, DensityFunctions.add(finalDensity, DensityFunctions.constant(-0.703125)).clamp(-64.0, 64.0)));

//		ctx.register(TEMPERATURE, wrapNoise(noise, RTFNoise.TEMPERATURE));
//		ctx.register(HUMIDITY, wrapNoise(noise, RTFNoise.HUMIDITY));
	}

	private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
		return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
	}
	
	private static DensityFunction slideOverworld(boolean bl, DensityFunction densityFunction) {
        return slide(densityFunction, -64, 256, bl ? 16 : 80, bl ? 0 : 64, -0.078125, 0, 24, bl ? 0.4 : 0.1171875);
    }

	private static DensityFunction slide(DensityFunction densityFunction, int i, int j, int k, int l, double d, int m, int n, double e) {
        DensityFunction densityFunction2 = densityFunction;
        DensityFunction densityFunction3 = DensityFunctions.yClampedGradient(i + j - k, i + j - l, 1.0, 0.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction3, d, densityFunction2);
        DensityFunction densityFunction4 = DensityFunctions.yClampedGradient(i + m, i + n, 0.0, 1.0);
        densityFunction2 = DensityFunctions.lerp(densityFunction4, e, densityFunction2);
        return densityFunction2;
    }	
	
	private static DensityFunction wrapNoise(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
		return new NoiseWrapper.Marker(noise.getOrThrow(key));
	}
	
	private static ResourceKey<DensityFunction> resolve(String key) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION, key);
	}
	
	private static DensityFunction register(BootstapContext<DensityFunction> ctx, ResourceKey<DensityFunction> key, DensityFunction value) {
    	return new DensityFunctions.HolderHolder(ctx.register(key, value));
    }
}