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
	public static final ResourceKey<PlacedFeature> SWAMP_SURFACE = createKey("processing/swamp_surface");

	public static final ResourceKey<PlacedFeature> ACACIA_TREES = createKey("acacia_trees");
	
	public static final ResourceKey<PlacedFeature> FOREST_GRASS = createKey("forest_grass");
    
	public static void bootstrap(Preset preset, BootstapContext<PlacedFeature> ctx) {
		HolderGetter<ConfiguredFeature<?, ?>> features = ctx.lookup(Registries.CONFIGURED_FEATURE);
		MiscellaneousSettings miscellaneous = preset.miscellaneous();

		PlacementModifier disableOnOverworld = RTFPlacementModifiers.blacklistDimensions(LevelStem.OVERWORLD);
		
		PlacementUtils.register(ctx, ACACIA_TREES, features.getOrThrow(RTFConfiguredFeatures.ERODE));
		
		if(miscellaneous.erosionDecorator) {
			PlacementUtils.register(ctx, ERODE, features.getOrThrow(RTFConfiguredFeatures.ERODE));
		}

		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			PlacementUtils.register(ctx, DECORATE_SNOW, features.getOrThrow(RTFConfiguredFeatures.DECORATE_SNOW));
		}
		
		PlacementUtils.register(ctx, SWAMP_SURFACE, features.getOrThrow(RTFConfiguredFeatures.SWAMP_SURFACE));
		
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
        	PlacementUtils.register(ctx, FOREST_GRASS, features.getOrThrow(RTFConfiguredFeatures.FOREST_GRASS), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, CountPlacement.of(7));
        }
	}
	    
    private static ResourceKey<PlacedFeature> createKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, RTFCommon.location(name));
    }
}
