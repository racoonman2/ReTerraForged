package raccoonman.reterraforged.data.worldgen.preset;

import java.util.List;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.HeightmapPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.world.worldgen.feature.placement.RTFPlacementModifiers;

public class PresetPlacedFeatures {
	public static final ResourceKey<PlacedFeature> ERODE = createKey("erode");
	public static final ResourceKey<PlacedFeature> DECORATE_SNOW = createKey("decorate_snow");
	public static final ResourceKey<PlacedFeature> SWAMP_SURFACE = createKey("swamp_surface");

	public static final ResourceKey<PlacedFeature> OAK_SMALL = createKey("oak/small");
	public static final ResourceKey<PlacedFeature> OAK_FOREST = createKey("oak/forest");
	public static final ResourceKey<PlacedFeature> OAK_LARGE = createKey("oak/large");
	public static final ResourceKey<PlacedFeature> BIRCH_SMALL = createKey("birch/small");
	public static final ResourceKey<PlacedFeature> BIRCH_FOREST = createKey("birch/forest");
	public static final ResourceKey<PlacedFeature> BIRCH_LARGE = createKey("birch/large");
	public static final ResourceKey<PlacedFeature> ACACIA_BUSH = createKey("acacia/bush");
	public static final ResourceKey<PlacedFeature> ACACIA_SMALL = createKey("acacia/small");
	public static final ResourceKey<PlacedFeature> ACACIA_LARGE = createKey("acacia/large");
	public static final ResourceKey<PlacedFeature> DARK_OAK_SMALL = createKey("dark_oak/small");
	public static final ResourceKey<PlacedFeature> DARK_OAK_LARGE = createKey("dark_oak/large");
	public static final ResourceKey<PlacedFeature> HUGE_BROWN_MUSHROOM = createKey("mushrooms/huge_brown_mushroom");
	public static final ResourceKey<PlacedFeature> HUGE_RED_MUSHROOM = createKey("mushrooms/huge_red_mushroom");
	public static final ResourceKey<PlacedFeature> WILLOW_SMALL = createKey("willow/small");
	public static final ResourceKey<PlacedFeature> WILLOW_LARGE = createKey("willow/large");
	public static final ResourceKey<PlacedFeature> PINE = createKey("pine");
	public static final ResourceKey<PlacedFeature> SPRUCE_SMALL = createKey("spruce/small");
	public static final ResourceKey<PlacedFeature> SPRUCE_LARGE = createKey("spruce/large");
	public static final ResourceKey<PlacedFeature> REDWOOD_LARGE = createKey("redwood/large");
	public static final ResourceKey<PlacedFeature> REDWOOD_HUGE = createKey("redwood/huge");
	public static final ResourceKey<PlacedFeature> JUNGLE_SMALL = createKey("jungle/small");
	public static final ResourceKey<PlacedFeature> JUNGLE_LARGE = createKey("jungle/large");
	public static final ResourceKey<PlacedFeature> JUNGLE_HUGE = createKey("jungle/huge");

	public static final ResourceKey<PlacedFeature> FOREST_GRASS = createKey("forest_grass");
	public static final ResourceKey<PlacedFeature> BIRCH_FOREST_GRASS = createKey("birch_forest_grass");
	
	public static final ResourceKey<PlacedFeature> PLAINS_TREES = createKey("plains_trees");
	public static final ResourceKey<PlacedFeature> FOREST_TREES = createKey("forest_trees");
	public static final ResourceKey<PlacedFeature> FLOWER_FOREST_TREES = createKey("flower_forest_trees");
	public static final ResourceKey<PlacedFeature> BIRCH_TREES = createKey("birch_trees");
	public static final ResourceKey<PlacedFeature> DARK_FOREST_TREES = createKey("dark_forest_trees");
	public static final ResourceKey<PlacedFeature> BADLANDS_TREES = createKey("badlands_trees");
	public static final ResourceKey<PlacedFeature> WOODED_BADLANDS_TREES = createKey("wooded_badlands_trees");
	public static final ResourceKey<PlacedFeature> SAVANNA_TREES = createKey("savanna_trees");
	public static final ResourceKey<PlacedFeature> SWAMP_TREES = createKey("swamp_trees");
	public static final ResourceKey<PlacedFeature> MEADOW_TREES = createKey("meadow_trees");
	public static final ResourceKey<PlacedFeature> FIR_TREES = createKey("fir_trees");
	public static final ResourceKey<PlacedFeature> WINDSWEPT_HILLS_FIR_TREES = createKey("windswept_hills_fir_trees");
	public static final ResourceKey<PlacedFeature> PINE_TREES = createKey("pine_trees");
	public static final ResourceKey<PlacedFeature> SPRUCE_TREES = createKey("spruce_trees");
	public static final ResourceKey<PlacedFeature> SPRUCE_TUNDRA_TREES = createKey("spruce_tundra_trees");
	public static final ResourceKey<PlacedFeature> REDWOOD_TREES = createKey("redwood_trees");
	public static final ResourceKey<PlacedFeature> JUNGLE_TREES = createKey("jungle_trees");
	public static final ResourceKey<PlacedFeature> JUNGLE_EDGE_TREES = createKey("jungle_edge_trees");
    
	public static void bootstrap(Preset preset, BootstapContext<PlacedFeature> ctx) {
		HolderGetter<ConfiguredFeature<?, ?>> features = ctx.lookup(Registries.CONFIGURED_FEATURE);
		MiscellaneousSettings miscellaneous = preset.miscellaneous();

		PlacementModifier disableOnOverworld = RTFPlacementModifiers.blacklistDimensions(LevelStem.OVERWORLD);
		
		if(miscellaneous.erosionDecorator) {
			PlacementUtils.register(ctx, ERODE, features.getOrThrow(PresetConfiguredFeatures.ERODE));
		}

		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			PlacementUtils.register(ctx, DECORATE_SNOW, features.getOrThrow(PresetConfiguredFeatures.DECORATE_SNOW));
		}
		
		PlacementUtils.register(ctx, SWAMP_SURFACE, features.getOrThrow(PresetConfiguredFeatures.SWAMP_SURFACE));
		
        if(!miscellaneous.vanillaSprings) {
        	PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_WATER, features.getOrThrow(MiscOverworldFeatures.SPRING_WATER), disableOnOverworld);
        }
        
        if(!miscellaneous.vanillaLavaSprings) {
        	PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_LAVA, features.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_OVERWORLD), disableOnOverworld);
        	PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_LAVA_FROZEN, features.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_FROZEN), disableOnOverworld);
        }
        
        if(!miscellaneous.vanillaLavaLakes) {
            PlacementUtils.register(ctx, MiscOverworldPlacements.LAKE_LAVA_SURFACE, features.getOrThrow(MiscOverworldFeatures.LAKE_LAVA), disableOnOverworld);
            PlacementUtils.register(ctx, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND, features.getOrThrow(MiscOverworldFeatures.LAKE_LAVA), disableOnOverworld);
        }

        if(miscellaneous.strataDecorator) {
            PlacementUtils.register(ctx, OrePlacements.ORE_DIRT, features.getOrThrow(OreFeatures.ORE_DIRT), disableOnOverworld);
            PlacementUtils.register(ctx, OrePlacements.ORE_GRAVEL, features.getOrThrow(OreFeatures.ORE_GRAVEL), disableOnOverworld);
            PlacementUtils.register(ctx, OrePlacements.ORE_GRANITE_UPPER, features.getOrThrow(OreFeatures.ORE_GRANITE), disableOnOverworld);
            PlacementUtils.register(ctx, OrePlacements.ORE_DIORITE_UPPER, features.getOrThrow(OreFeatures.ORE_DIORITE), disableOnOverworld);
            PlacementUtils.register(ctx, OrePlacements.ORE_ANDESITE_UPPER, features.getOrThrow(OreFeatures.ORE_ANDESITE), disableOnOverworld);
            PlacementUtils.register(ctx, OrePlacements.ORE_GRANITE_LOWER, features.getOrThrow(OreFeatures.ORE_GRANITE), disableOnOverworld);
            PlacementUtils.register(ctx, OrePlacements.ORE_DIORITE_LOWER, features.getOrThrow(OreFeatures.ORE_DIORITE), disableOnOverworld);
            PlacementUtils.register(ctx, OrePlacements.ORE_ANDESITE_LOWER, features.getOrThrow(OreFeatures.ORE_ANDESITE), disableOnOverworld);
        }
        
        if(miscellaneous.customBiomeFeatures) {
        	PlacementUtils.register(ctx, OAK_SMALL, features.getOrThrow(PresetConfiguredFeatures.OAK_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
        	PlacementUtils.register(ctx, OAK_FOREST, features.getOrThrow(PresetConfiguredFeatures.OAK_FOREST), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
            PlacementUtils.register(ctx, OAK_LARGE, features.getOrThrow(PresetConfiguredFeatures.OAK_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
            PlacementUtils.register(ctx, BIRCH_SMALL, features.getOrThrow(PresetConfiguredFeatures.BIRCH_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
            PlacementUtils.register(ctx, BIRCH_FOREST, features.getOrThrow(PresetConfiguredFeatures.BIRCH_FOREST), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
            PlacementUtils.register(ctx, BIRCH_LARGE, features.getOrThrow(PresetConfiguredFeatures.BIRCH_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
            PlacementUtils.register(ctx, ACACIA_BUSH, features.getOrThrow(PresetConfiguredFeatures.ACACIA_BUSH), PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
            PlacementUtils.register(ctx, ACACIA_SMALL, features.getOrThrow(PresetConfiguredFeatures.ACACIA_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
            PlacementUtils.register(ctx, ACACIA_LARGE, features.getOrThrow(PresetConfiguredFeatures.ACACIA_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
            PlacementUtils.register(ctx, DARK_OAK_SMALL, features.getOrThrow(PresetConfiguredFeatures.DARK_OAK_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
            PlacementUtils.register(ctx, DARK_OAK_LARGE, features.getOrThrow(PresetConfiguredFeatures.DARK_OAK_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
            PlacementUtils.register(ctx, HUGE_BROWN_MUSHROOM, features.getOrThrow(PresetConfiguredFeatures.HUGE_BROWN_MUSHROOM));
            PlacementUtils.register(ctx, HUGE_RED_MUSHROOM, features.getOrThrow(PresetConfiguredFeatures.HUGE_RED_MUSHROOM));
            PlacementUtils.register(ctx, WILLOW_SMALL, features.getOrThrow(PresetConfiguredFeatures.WILLOW_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
            PlacementUtils.register(ctx, WILLOW_LARGE, features.getOrThrow(PresetConfiguredFeatures.WILLOW_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
            PlacementUtils.register(ctx, PINE, features.getOrThrow(PresetConfiguredFeatures.PINE), PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
            PlacementUtils.register(ctx, SPRUCE_SMALL, features.getOrThrow(PresetConfiguredFeatures.SPRUCE_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
            PlacementUtils.register(ctx, SPRUCE_LARGE, features.getOrThrow(PresetConfiguredFeatures.SPRUCE_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
            PlacementUtils.register(ctx, REDWOOD_LARGE, features.getOrThrow(PresetConfiguredFeatures.REDWOOD_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
            PlacementUtils.register(ctx, REDWOOD_HUGE, features.getOrThrow(PresetConfiguredFeatures.REDWOOD_HUGE), PlacementUtils.filteredByBlockSurvival(Blocks.SPRUCE_SAPLING));
            PlacementUtils.register(ctx, JUNGLE_SMALL, features.getOrThrow(PresetConfiguredFeatures.JUNGLE_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
            PlacementUtils.register(ctx, JUNGLE_LARGE, features.getOrThrow(PresetConfiguredFeatures.JUNGLE_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
            PlacementUtils.register(ctx, JUNGLE_HUGE, features.getOrThrow(PresetConfiguredFeatures.JUNGLE_HUGE), PlacementUtils.filteredByBlockSurvival(Blocks.JUNGLE_SAPLING));
            
            PlacementUtils.register(ctx, FOREST_GRASS, features.getOrThrow(PresetConfiguredFeatures.FOREST_GRASS), worldSurfaceSquaredWithCount(7));
            PlacementUtils.register(ctx, BIRCH_FOREST_GRASS, features.getOrThrow(PresetConfiguredFeatures.BIRCH_FOREST_GRASS), worldSurfaceSquaredWithCount(7));
            
            PlacementUtils.register(ctx, PLAINS_TREES, features.getOrThrow(PresetConfiguredFeatures.PLAINS_TREES), RTFPlacementModifiers.countExtra(0, 0.02F, 1), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
          	PlacementUtils.register(ctx, FOREST_TREES, features.getOrThrow(PresetConfiguredFeatures.FOREST_TREES), RTFPlacementModifiers.poisson(7, 0.25F, 0.3F, 150, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
          	PlacementUtils.register(ctx, FLOWER_FOREST_TREES, features.getOrThrow(PresetConfiguredFeatures.FLOWER_FOREST_TREES), RTFPlacementModifiers.poisson(8, 0.2F, 0.1F, 500, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
          	PlacementUtils.register(ctx, BIRCH_TREES, features.getOrThrow(PresetConfiguredFeatures.BIRCH_TREES), RTFPlacementModifiers.poisson(6, 0.25F, 0.25F, 175, 0.9F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, DARK_FOREST_TREES, features.getOrThrow(PresetConfiguredFeatures.DARK_FOREST_TREES), RTFPlacementModifiers.poisson(5, 0.3F, 0.2F, 300, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, SAVANNA_TREES, features.getOrThrow(PresetConfiguredFeatures.SAVANNA_TREES), PlacementUtils.HEIGHTMAP, RTFPlacementModifiers.countExtra(0, 0.1F, 1), BiomeFilter.biome());
        	PlacementUtils.register(ctx, BADLANDS_TREES, features.getOrThrow(PresetConfiguredFeatures.BADLANDS_TREES), PlacementUtils.HEIGHTMAP, RTFPlacementModifiers.countExtra(0, 0.02F, 3), BiomeFilter.biome());
        	PlacementUtils.register(ctx, WOODED_BADLANDS_TREES, features.getOrThrow(PresetConfiguredFeatures.WOODED_BADLANDS_TREES), PlacementUtils.HEIGHTMAP, RTFPlacementModifiers.poisson(8, 0.2F, 0.8F, 0.25F, 150, 0.75F), BiomeFilter.biome());
        	PlacementUtils.register(ctx, SWAMP_TREES, features.getOrThrow(PresetConfiguredFeatures.SWAMP_TREES), RTFPlacementModifiers.countExtra(3, 0.05F, 1), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, MEADOW_TREES, features.getOrThrow(PresetConfiguredFeatures.OAK_SMALL), RarityFilter.onAverageOnceEvery(30), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, FIR_TREES, features.getOrThrow(PresetConfiguredFeatures.FIR_TREES), RTFPlacementModifiers.poisson(4, 0.25F, 0.3F, 300, 0.6F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, WINDSWEPT_HILLS_FIR_TREES, features.getOrThrow(PresetConfiguredFeatures.FIR_TREES), RarityFilter.onAverageOnceEvery(30), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, PINE_TREES, features.getOrThrow(PresetConfiguredFeatures.PINE), RTFPlacementModifiers.poisson(7, 0.25F, 0.25F, 250, 0.7F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, SPRUCE_TREES, features.getOrThrow(PresetConfiguredFeatures.PINE), RTFPlacementModifiers.poisson(7, 0.3F, 0.25F, 250, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, SPRUCE_TUNDRA_TREES, features.getOrThrow(PresetConfiguredFeatures.PINE), RarityFilter.onAverageOnceEvery(80), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, REDWOOD_TREES, features.getOrThrow(PresetConfiguredFeatures.REDWOOD_TREES), RTFPlacementModifiers.poisson(6, 0.3F, 0.25F, 250, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, JUNGLE_TREES, features.getOrThrow(PresetConfiguredFeatures.JUNGLE_TREES), RTFPlacementModifiers.poisson(6, 0.4F, 0.2F, 400, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	PlacementUtils.register(ctx, JUNGLE_EDGE_TREES, features.getOrThrow(PresetConfiguredFeatures.JUNGLE_EDGE_TREES), RTFPlacementModifiers.poisson(8, 0.35F, 0.25F, 350, 0.75F), HeightmapPlacement.onHeightmap(Types.WORLD_SURFACE), BiomeFilter.biome());
        	
//			TODO shattered savanna
//        	PlacementUtils.register(ctx, VegetationPlacements.TREES_WINDSWEPT_SAVANNA, features.getOrThrow(VegetationFeatures.TREES_SAVANNA), RTFPlacementModifiers.disabled());
        }
	}
	
    public static List<PlacementModifier> worldSurfaceSquaredWithCount(int i) {
        return List.of(CountPlacement.of(i), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
    }
	
    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, RTFCommon.location(name));
    }
}
