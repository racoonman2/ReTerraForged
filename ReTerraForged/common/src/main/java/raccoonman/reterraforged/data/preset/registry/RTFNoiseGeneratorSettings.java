package raccoonman.reterraforged.data.preset.registry;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import raccoonman.reterraforged.preset.Preset;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.pages.CaveOptions;
import raccoonman.reterraforged.preset.pages.WorldOptions;
import raccoonman.reterraforged.registry.RTFRegistries;
import raccoonman.reterraforged.world.ParameterSlice;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public class RTFNoiseGeneratorSettings {
	
	public static void bootstrap(Preset preset, BootstrapContext<NoiseGeneratorSettings> ctx, HolderLookup.Provider lookupProvider) {
		HolderGetter<DensityFunction> densityFunctions = ctx.lookup(Registries.DENSITY_FUNCTION);
		HolderGetter<Block> blocks = ctx.lookup(Registries.BLOCK);
		HolderGetter<ParameterSlice> parameterSlices = lookupProvider.lookupOrThrow(RTFRegistries.PARAMETER_SLICE);
		ctx.register(NoiseGeneratorSettings.OVERWORLD, overworld(preset, densityFunctions, blocks, parameterSlices));
    }
	
	private static NoiseGeneratorSettings overworld(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Block> blocks, HolderGetter<ParameterSlice> parameterSlices) {
		int minY = preset.getOption(WorldOptions.MIN_Y);
		int maxY = preset.getOption(WorldOptions.MAX_Y);
		int waterLevel = preset.getOption(WorldOptions.SEA_LEVEL);
		boolean largeOreVeins = preset.getOption(CaveOptions.LARGE_ORE_VEINS);
		return new NoiseGeneratorSettings(
			NoiseSettings.create(minY, -minY + maxY, 1, 2), 
			Blocks.STONE.defaultBlockState(), 
			Blocks.WATER.defaultBlockState(), 
			RTFNoiseRouterData.overworld(densityFunctions),
			SurfaceRuleData.overworld(),
			List.of(),// getParameterPoints(preset, parameterSlices), 
			waterLevel,
			false, 
			true, 
			largeOreVeins, 
			false
		);
	}
	
//	private static List<Climate.ParameterPoint> getParameterPoints(Preset preset, HolderGetter<ParameterSlice> parameterSlices) {
////		ResourceKey<ParameterSlice> spawnTerrain = preset.getOption(WorldOptions.SPAWN_TERRAIN);
//	    Climate.Parameter temperature = parameter(preset, TemperaturePoints.FROZEN, TemperaturePoints.MAX, WorldOptions.MIN_SPAWN_TEMPERATURE, WorldOptions.MAX_SPAWN_TEMPERATURE);
//	    Climate.Parameter humidity = parameter(preset, HumidityPoints.ARID, HumidityPoints.MAX, WorldOptions.MIN_SPAWN_HUMIDITY, WorldOptions.MAX_SPAWN_HUMIDITY);
//	    Climate.Parameter continentalness = parameter(preset, ContinentPoints.NEAR_INLAND, ContinentPoints.MAX, WorldOptions.MIN_SPAWN_CONTINENTALNESS, WorldOptions.MAX_SPAWN_CONTINENTALNESS);
////	    List<Climate.ParameterPoint> points = new OverworldBiomeBuilder().spawnTarget();// parameterSlices.getOrThrow(spawnTerrain).value().points();
////	    return points.stream().map((point) -> {
//	    return ImmutableList.of(
//	    	new Climate.ParameterPoint(
//	    		temperature, 
//	    		humidity,
//	    		continentalness, 
//	    		Climate.Parameter.span(-1.0F, 1.0F), 
//	    		Climate.Parameter.point(0.0F),
//	    		Climate.Parameter.span(-1.0F, 1.0F),
//	    		0L
//	    	)
//	    );
////	    }).toList();
//	}
	
	private static Climate.Parameter parameter(Preset preset, float from, float to, Option<Float> minOption, Option<Float> maxOption) {
		return Climate.Parameter.span(
			NoiseUtil.lerp(from, to, preset.getOption(minOption)), 
			NoiseUtil.lerp(from, to, preset.getOption(maxOption))
		);
	}
	
	private static ResourceKey<NoiseGeneratorSettings> createKey(ResourceLocation location) {
		return ResourceKey.create(Registries.NOISE_SETTINGS, location);
	}
}
