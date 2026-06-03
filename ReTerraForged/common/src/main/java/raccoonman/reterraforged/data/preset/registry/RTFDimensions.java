package raccoonman.reterraforged.data.preset.registry;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import raccoonman.reterraforged.preset.Preset;

public class RTFDimensions {
    
	public static void bootstrap(Preset preset, BootstrapContext<LevelStem> ctx) {
//		if(preset.getOption(ClimateOptions.CUSTOM_BIOME_GENERATION)) {
//			HolderGetter<DimensionType> dimensionTypes = ctx.lookup(Registries.DIMENSION_TYPE);			
////			HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
//			HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists = ctx.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
//			HolderGetter<NoiseGeneratorSettings> generatorSettings = ctx.lookup(Registries.NOISE_SETTINGS);

//			Holder<DimensionType> dimensionType = dimensionTypes.getOrThrow(BuiltinDimensionTypes.OVERWORLD);
//			ctx.register(LevelStem.OVERWORLD, new LevelStem(dimensionType, chunkGenerator(multiNoiseBiomeSourceParameterLists, generatorSettings)));
//		}
	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private static ChunkGenerator chunkGenerator(/*HolderGetter<Biome> biomes,*/ HolderGetter<MultiNoiseBiomeSourceParameterList> multiNoiseBiomeSourceParameterLists,  HolderGetter<NoiseGeneratorSettings> generatorSettings) {
////		ImmutableList.Builder builder = ImmutableList.builder();
////        new RTFOverworldBiomeBuilder().addBiomes((pair) -> builder.add(pair.mapSecond(biomes::getOrThrow)));
////		BiomeSource biomeSource = MultiNoiseBiomeSource.createFromList(new Climate.ParameterList(builder.build()));
//
//		Holder.Reference<MultiNoiseBiomeSourceParameterList> reference = multiNoiseBiomeSourceParameterLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.OVERWORLD);
//		BiomeSource biomeSource = MultiNoiseBiomeSource.createFromPreset(reference);
//		return new NoiseBasedChunkGenerator(biomeSource, generatorSettings.getOrThrow(NoiseSettings.OVERWORLD_NOISE_SETTINGS));
//	}
}
