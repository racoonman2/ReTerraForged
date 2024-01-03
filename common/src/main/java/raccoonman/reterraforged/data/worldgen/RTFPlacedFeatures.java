package raccoonman.reterraforged.data.worldgen;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.world.worldgen.feature.placement.RTFPlacementModifiers;

public class RTFPlacedFeatures {
	public static final ResourceKey<PlacedFeature> ERODE = createKey("processing/erode");
	public static final ResourceKey<PlacedFeature> DECORATE_SNOW = createKey("processing/decorate_snow");
	
	public static final ResourceKey<PlacedFeature> FOREST_GRASS = createKey("forest/grass");
//	public static final ResourceKey<PlacedFeature> FOREST_BUSH = createKey("forest/bush");
	public static final ResourceKey<PlacedFeature> OAK_FOREST = createKey("oak/forest");
	public static final ResourceKey<PlacedFeature> OAK_LARGE = createKey("oak/large");
	public static final ResourceKey<PlacedFeature> BIRCH_GRASS = createKey("birch/grass");
	public static final ResourceKey<PlacedFeature> BIRCH_BUSH = createKey("birch/bush");
	public static final ResourceKey<PlacedFeature> BIRCH_SMALL = createKey("birch/small");
	public static final ResourceKey<PlacedFeature> BIRCH_FOREST = createKey("birch/forest");
	public static final ResourceKey<PlacedFeature> BIRCH_LARGE = createKey("birch/large");
	public static final ResourceKey<PlacedFeature> ACACIA_SMALL = createKey("acacia/small");
	public static final ResourceKey<PlacedFeature> ACACIA_LARGE = createKey("acacia/large");
	public static final ResourceKey<PlacedFeature> DARK_OAK_SMALL = createKey("dark_oak/small");
	public static final ResourceKey<PlacedFeature> DARK_OAK_LARGE = createKey("dark_oak/large");
	public static final ResourceKey<PlacedFeature> HUGE_BROWN_MUSHROOM = createKey("mushrooms/huge_brown_mushroom");
	public static final ResourceKey<PlacedFeature> HUGE_RED_MUSHROOM = createKey("mushrooms/huge_red_mushroom");
    
	public static void bootstrap(Preset preset, BootstapContext<PlacedFeature> ctx) {
		HolderGetter<ConfiguredFeature<?, ?>> features = ctx.lookup(Registries.CONFIGURED_FEATURE);
		MiscellaneousSettings miscellaneous = preset.miscellaneous();

		PlacementModifier disableOnOverworld = RTFPlacementModifiers.blacklistDimensions(LevelStem.OVERWORLD);
		
		if(miscellaneous.erosionDecorator) {
			PlacementUtils.register(ctx, ERODE, features.getOrThrow(RTFConfiguredFeatures.ERODE));
		}

		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			PlacementUtils.register(ctx, DECORATE_SNOW, features.getOrThrow(RTFConfiguredFeatures.DECORATE_SNOW));
		}
		
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
        	PlacementModifier treeThreshold = SurfaceWaterDepthFilter.forMaxDepth(0);
    		
        	PlacementUtils.register(ctx, FOREST_GRASS, features.getOrThrow(RTFConfiguredFeatures.FOREST_GRASS), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(7));
//        	PlacementUtils.register(ctx, FOREST_BUSH, features.getOrThrow(RTFConfiguredFeatures.FOREST_BUSH), RTFPlacementModifiers.countExtra(1, 0.05F, 1), TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
//
//        	PlacementUtils.register(ctx, OAK_FOREST, features.getOrThrow(RTFConfiguredFeatures.OAK_FOREST), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
//            PlacementUtils.register(ctx, OAK_LARGE, features.getOrThrow(RTFConfiguredFeatures.OAK_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.OAK_SAPLING));
//
//            PlacementUtils.register(ctx, BIRCH_GRASS, features.getOrThrow(RTFConfiguredFeatures.BIRCH_GRASS), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(7));
//            PlacementUtils.register(ctx, BIRCH_BUSH, features.getOrThrow(RTFConfiguredFeatures.BIRCH_BUSH), RTFPlacementModifiers.countExtra(0, 0.05F, 1), TREE_THRESHOLD, PlacementUtils.HEIGHTMAP_OCEAN_FLOOR, BiomeFilter.biome());
//            PlacementUtils.register(ctx, BIRCH_SMALL, features.getOrThrow(RTFConfiguredFeatures.BIRCH_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
//            PlacementUtils.register(ctx, BIRCH_FOREST, features.getOrThrow(RTFConfiguredFeatures.BIRCH_FOREST), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
//            PlacementUtils.register(ctx, BIRCH_LARGE, features.getOrThrow(RTFConfiguredFeatures.BIRCH_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.BIRCH_SAPLING));
//            
//            PlacementUtils.register(ctx, ACACIA_SMALL, features.getOrThrow(RTFConfiguredFeatures.ACACIA_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
//            PlacementUtils.register(ctx, ACACIA_LARGE, features.getOrThrow(RTFConfiguredFeatures.ACACIA_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.ACACIA_SAPLING));
//
//            PlacementUtils.register(ctx, DARK_OAK_SMALL, features.getOrThrow(RTFConfiguredFeatures.DARK_OAK_SMALL), PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
//            PlacementUtils.register(ctx, DARK_OAK_LARGE, features.getOrThrow(RTFConfiguredFeatures.DARK_OAK_LARGE), PlacementUtils.filteredByBlockSurvival(Blocks.DARK_OAK_SAPLING));
//
//            PlacementUtils.register(ctx, HUGE_BROWN_MUSHROOM, features.getOrThrow(RTFConfiguredFeatures.HUGE_BROWN_MUSHROOM));
//            PlacementUtils.register(ctx, HUGE_RED_MUSHROOM, features.getOrThrow(RTFConfiguredFeatures.HUGE_RED_MUSHROOM));
//
//        	PlacementUtils.register(ctx, VegetationPlacements.TREES_PLAINS, features.getOrThrow(VegetationFeatures.TREES_PLAINS), PlacementUtils.HEIGHTMAP, RTFPlacementModifiers.countExtra(0, 0.02F, 1));
//        	PlacementUtils.register(ctx, VegetationPlacements.TREES_BIRCH, features.getOrThrow(TreeFeatures.BIRCH_BEES_0002), RTFPlacementModifiers.poisson(6, 0.25F, 0.25F, 175, 0.9F), HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE));
//        	PlacementUtils.register(ctx, VegetationPlacements.TREES_SAVANNA, features.getOrThrow(VegetationFeatures.TREES_SAVANNA), PlacementUtils.HEIGHTMAP, RTFPlacementModifiers.countExtra(0, 0.1F, 1));
//        	PlacementUtils.register(ctx, VegetationPlacements.DARK_FOREST_VEGETATION, features.getOrThrow(VegetationFeatures.DARK_FOREST_VEGETATION), BiomeFilter.biome(), RTFPlacementModifiers.poisson(5, 0.3F, 0.2F, 300, 0.75F), HeightmapPlacement.onHeightmap(Heightmap.Types.WORLD_SURFACE));
        	
//			TODO shattered savanna
//        	PlacementUtils.register(ctx, VegetationPlacements.TREES_WINDSWEPT_SAVANNA, features.getOrThrow(VegetationFeatures.TREES_SAVANNA), RTFPlacementModifiers.disabled());
        }
	}
	    
    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, RTFCommon.location(name));
    }
}
