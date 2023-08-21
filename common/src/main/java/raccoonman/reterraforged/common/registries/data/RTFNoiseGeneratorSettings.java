package raccoonman.reterraforged.common.registries.data;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.ReTerraForged;

public final class RTFNoiseGeneratorSettings {
    public static final ResourceKey<NoiseGeneratorSettings> DEFAULT = resolve("default");
	
	public static void register(BootstapContext<NoiseGeneratorSettings> ctx) {
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		ctx.register(DEFAULT, createDefault(ctx, densityFunctions, noiseParams));
    }
    
    private static ResourceKey<NoiseGeneratorSettings> resolve(String name) {
    	return ResourceKey.create(Registries.NOISE_SETTINGS, ReTerraForged.resolve(name));
    }

    private static NoiseGeneratorSettings createDefault(BootstapContext<NoiseGeneratorSettings> ctx, HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
    	NoiseGeneratorSettings overworld = NoiseGeneratorSettings.overworld(ctx, false, false);
    	NoiseRouter overworldRouter = overworld.noiseRouter();
	    return new NoiseGeneratorSettings(
	  		new NoiseSettings(-64, 1024, 1, 1), 
	  		Blocks.STONE.defaultBlockState(), 
	  		Blocks.WATER.defaultBlockState(), 
	  		new NoiseRouter(
	  			overworldRouter.barrierNoise(),
	  			overworldRouter.fluidLevelFloodednessNoise(),
	  			overworldRouter.fluidLevelSpreadNoise(),
	  			overworldRouter.lavaNoise(),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.TEMPERATURE)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.HUMIDITY)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.CONTINENT)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.RIVER)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.DEPTH)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.WEIRDNESS)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.INITIAL_DENSITY)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.FINAL_DENSITY)),
	  			overworldRouter.veinToggle(),
	  			overworldRouter.veinRidged(),
	  			overworldRouter.veinGap()
	    	), 
	  		SurfaceRuleData.overworld(), 
	  		ImmutableList.of(
	  			Climate.parameters(
	  				Climate.Parameter.span(0.0F, 1.0F),
	  				Climate.Parameter.span(0.0F, 1.0F),
	  				Climate.Parameter.point(1.0F),
	  				Climate.Parameter.span(0.0F, 1.0F),
	  				Climate.Parameter.point(0.0F),
	  				Climate.Parameter.span(0.0F, 1.0F),
	  				0.0F
	  			)
	  		),
	  		62,
	  		false, 
	  		true, 
	  		true, 
	  		false
    	);
    }
}
