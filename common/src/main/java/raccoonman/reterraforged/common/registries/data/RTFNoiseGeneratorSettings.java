package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseRouterData;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.synth.NormalNoise;

public final class RTFNoiseGeneratorSettings {
	
	public static void register(BootstapContext<NoiseGeneratorSettings> ctx) {
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		ctx.register(NoiseGeneratorSettings.OVERWORLD, createOverworld(densityFunctions, noiseParams));
    }

    private static NoiseGeneratorSettings createOverworld(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams) {
    	NoiseRouter overworld = NoiseRouterData.overworld(densityFunctions, noiseParams, false, false);
    	return new NoiseGeneratorSettings(
	  		new NoiseSettings(-64, 256, 1, 1),
	  		Blocks.STONE.defaultBlockState(),
	  		Blocks.WATER.defaultBlockState(),
	  		new NoiseRouter(
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero(), 
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.CONTINENT)),
	  			DensityFunctions.constant(-0.65D), 
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero(), 
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.INITIAL_DENSITY)),
	  			new DensityFunctions.HolderHolder(densityFunctions.getOrThrow(RTFDensityFunctions.FINAL_DENSITY)),
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero(), 
	  			DensityFunctions.zero()
	  		),
	  		SurfaceRules.sequence(
	  		//TODO we need to replicate the Surface class here
	  		//TODO we should probably remove the height gradient rules and replace them with noise gradient ones
	  			SurfaceRuleData.overworld()
//	  			SurfaceRules.ifTrue(
//	  				new SurfaceGradientConditionSource(), 
//	  				SurfaceRules.state(Blocks.STONE.defaultBlockState())
//	  			)
	  		),
	  		new OverworldBiomeBuilder().spawnTarget(),
	  		63,
	  		false, 
	  		true, 
	  		true, 
	  		false
    	);
    }
}
