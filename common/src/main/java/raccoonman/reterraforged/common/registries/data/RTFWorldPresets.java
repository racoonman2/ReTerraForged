package raccoonman.reterraforged.common.registries.data;

import com.google.common.collect.ImmutableMap;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterList;
import net.minecraft.world.level.biome.MultiNoiseBiomeSourceParameterLists;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateGroup;
import raccoonman.reterraforged.common.level.levelgen.generator.RTFChunkGenerator;
import raccoonman.reterraforged.common.level.levelgen.settings.Settings;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainBlender;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainLevels;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.registries.data.tags.RTFClimateGroupTags;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public final class RTFWorldPresets {
	public static final ResourceKey<WorldPreset> RETERRAFORGED = resolve("reterraforged");

	public static void register(BootstapContext<WorldPreset> ctx) {
		ctx.register(RETERRAFORGED, createDefaultPreset(ctx));
    }
	
	private static WorldPreset createDefaultPreset(BootstapContext<WorldPreset> ctx) {
		HolderGetter<DimensionType> dimensions = ctx.lookup(Registries.DIMENSION_TYPE);
		HolderGetter<Noise> modules = ctx.lookup(RTFRegistries.NOISE);
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
		HolderGetter<NoiseGeneratorSettings> noiseSettings = ctx.lookup(Registries.NOISE_SETTINGS);
		HolderGetter<MultiNoiseBiomeSourceParameterList> paramLists = ctx.lookup(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
		HolderGetter<ClimateGroup> climateGroups = ctx.lookup(RTFRegistries.CLIMATE_GROUP);
		return new WorldPreset(
			ImmutableMap.<ResourceKey<LevelStem>, LevelStem>builder()
			.put(LevelStem.NETHER, 
				new LevelStem(
					dimensions.getOrThrow(BuiltinDimensionTypes.NETHER), 
					new NoiseBasedChunkGenerator(
						MultiNoiseBiomeSource.createFromPreset(paramLists.getOrThrow(MultiNoiseBiomeSourceParameterLists.NETHER)),
						noiseSettings.getOrThrow(NoiseGeneratorSettings.NETHER)
					)
				)
			)
			.put(LevelStem.OVERWORLD, 
				new LevelStem(
					dimensions.getOrThrow(BuiltinDimensionTypes.OVERWORLD),
					RTFChunkGenerator.create(
						Settings.DEFAULT,
						TerrainLevels.DEFAULT,
						new TerrainBlender(800, 0.8F, 0.4F, 
							new WeightMap.Builder<>()
								.entry(0.55F, modules.getOrThrow(RTFNoise.STEPPE))
								.entry(0.6F, modules.getOrThrow(RTFNoise.PLAINS))
								.entry(0.55F, modules.getOrThrow(RTFNoise.HILLS_1))
								.entry(0.55F, modules.getOrThrow(RTFNoise.HILLS_2))
								.entry(0.45F, modules.getOrThrow(RTFNoise.DALES))
								.entry(0.45F, modules.getOrThrow(RTFNoise.PLATEAU))
								.entry(0.65F, modules.getOrThrow(RTFNoise.BADLANDS))
								.entry(0.65F, modules.getOrThrow(RTFNoise.TORRIDONIAN))
								.entry(0.55F, modules.getOrThrow(RTFNoise.MOUNTAINS_1))
								.entry(0.45F, modules.getOrThrow(RTFNoise.MOUNTAINS_2))
								.entry(0.45F, modules.getOrThrow(RTFNoise.MOUNTAINS_3))
								.entry(0.45F, modules.getOrThrow(RTFNoise.DOLOMITES))
								.entry(0.45F, modules.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_1))
								.entry(0.45F, modules.getOrThrow(RTFNoise.MOUNTAINS_RIDGE_2))
								.build()
						),
						noiseSettings.getOrThrow(RTFNoiseGeneratorSettings.DEFAULT),
						climateGroups.getOrThrow(RTFClimateGroupTags.DEFAULT)
					)
				)
			)
			.put(LevelStem.END,
				new LevelStem(
					dimensions.getOrThrow(BuiltinDimensionTypes.END), 
					new NoiseBasedChunkGenerator(
						TheEndBiomeSource.create(biomes),
						noiseSettings.getOrThrow(NoiseGeneratorSettings.END)
					)
				)
			)
			.build());
	}
	
	private static ResourceKey<WorldPreset> resolve(String path) {
		return ReTerraForged.resolve(Registries.WORLD_PRESET, path);
	}
}
