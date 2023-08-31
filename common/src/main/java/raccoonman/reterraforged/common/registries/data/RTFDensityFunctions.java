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

// can we use mountain noise as the ridges??
public final class RTFDensityFunctions {
	// if this is off performance goes to shit
	public static final ResourceKey<DensityFunction> INITIAL_DENSITY = resolve("initial_density");
	public static final ResourceKey<DensityFunction> FINAL_DENSITY = resolve("final_density");
    public static final ResourceKey<DensityFunction> DEPTH = resolve("depth");
    public static final ResourceKey<DensityFunction> WEIRDNESS = resolve("weirdness");

    // access wideners dont work with these fields for some reason so we have to copy them manually
    // TODO access the actual fields from NoiseRouterData
    private static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = resolve("minecraft:overworld/caves/spaghetti_roughness_function");
    private static final ResourceKey<DensityFunction> NOODLE = resolve("minecraft:overworld/caves/noodle");
    private static final ResourceKey<DensityFunction> PILLARS = resolve("minecraft:overworld/caves/pillars");
    private static final ResourceKey<DensityFunction> SPAGHETTI_2D = resolve("minecraft:overworld/caves/spaghetti_2d");
    
	public static void register(BootstapContext<DensityFunction> ctx) {                                                                                 
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);

		ctx.register(NoiseRouterData.CONTINENTS, DensityFunctions.flatCache(wrapNoise(noise, RTFNoise.CONTINENT)));
    	ctx.register(NoiseRouterData.RIDGES, new YGradient(new FlatCache.Marker(wrapNoise(noise, RTFNoise.TERRAIN_HEIGHT))));
//		ctx.register(FINAL_DENSITY, getFunction(densityFunctions, INITIAL_DENSITY));
//    	ctx.register(NoiseRouterData.RIDGES, DensityFunctions.cache2d(wrapNoise(noise, RTFNoise.RIDGES)));
	}

	private static DensityFunction getFunction(HolderGetter<DensityFunction> densityFunctions, ResourceKey<DensityFunction> key) {
		return new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(key));
	}

	private static DensityFunction wrapNoise(HolderGetter<Noise> noise, ResourceKey<Noise> key) {
		return new NoiseWrapper.Marker(noise.getOrThrow(key));
	}
	
	private static ResourceKey<DensityFunction> resolve(String key) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION, key);
	}
}