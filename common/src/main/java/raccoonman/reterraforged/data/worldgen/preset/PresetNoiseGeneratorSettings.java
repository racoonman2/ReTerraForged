package raccoonman.reterraforged.data.worldgen.preset;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.data.worldgen.preset.settings.CaveSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldSettings;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;

public final class PresetNoiseGeneratorSettings {
	
	public static void bootstrap(Preset preset, BootstapContext<NoiseGeneratorSettings> ctx) {
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<NormalNoise.NoiseParameters> noiseParams = ctx.lookup(Registries.NOISE);
		HolderGetter<Noise> noises = ctx.lookup(RTFRegistries.NOISE);
		
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		int worldHeight = properties.worldHeight;
		int worldDepth = properties.worldDepth;
		
		CaveSettings caveSettings = preset.caves();

		ctx.register(NoiseGeneratorSettings.OVERWORLD, new NoiseGeneratorSettings(
			NoiseSettings.create(-worldDepth, worldDepth + worldHeight, 1, 2), 
			Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), 
			PresetNoiseRouterData.overworld(preset, densityFunctions, noiseParams, noises),
			PresetSurfaceRuleData.overworld(preset, densityFunctions, noises),
//			i want to be able to do it with parameter points :(
//			properties.spawnType.getParameterPoints(), 
			ImmutableList.of(),
			properties.seaLevel, 
			false, 
			true, 
			caveSettings.largeOreVeins, 
			true
		));
    }
}
