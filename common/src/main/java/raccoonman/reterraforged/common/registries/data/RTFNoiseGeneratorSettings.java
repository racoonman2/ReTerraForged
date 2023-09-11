package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.biome.OverworldBiomeBuilder;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.registries.data.preset.Preset;

public final class RTFNoiseGeneratorSettings {
	
	public static void bootstrap(BootstapContext<NoiseGeneratorSettings> ctx) {
		bootstrap(ctx, Preset.DEFAULT);
	}
	
	public static void bootstrap(BootstapContext<NoiseGeneratorSettings> ctx, Preset preset) {
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		ctx.register(NoiseGeneratorSettings.OVERWORLD, createOverworld(densityFunctions, noiseParams, preset));
    }

    private static NoiseGeneratorSettings createOverworld(HolderGetter<DensityFunction> densityFunctions, HolderGetter<NormalNoise.NoiseParameters> noiseParams, Preset preset) {
    	return new NoiseGeneratorSettings(
	  		new NoiseSettings(-64, 384, 1, 1),
	  		Blocks.STONE.defaultBlockState(),
	  		Blocks.WATER.defaultBlockState(),
	  		RTFNoiseRouterData.overworld(densityFunctions, noiseParams),
	  		RTFSurfaceRuleData.overworld(),
	  		new OverworldBiomeBuilder().spawnTarget(),
	  		preset.world().properties.seaLevel,
	  		false, 
	  		true, 
	  		true, 
	  		false
    	);
    }
}
