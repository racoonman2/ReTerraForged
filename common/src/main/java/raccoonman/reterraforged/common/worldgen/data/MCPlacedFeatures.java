package raccoonman.reterraforged.common.worldgen.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.OrePlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.heightproviders.UniformHeight;
import net.minecraft.world.level.levelgen.heightproviders.VeryBiasedToBottomHeight;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.EnvironmentScanPlacement;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceRelativeThresholdFilter;
import raccoonman.reterraforged.common.level.levelgen.placement.NeverPlacementModifier;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class MCPlacedFeatures {
	
	public static void bootstrap(BootstapContext<PlacedFeature> ctx, Preset preset) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();

        if(!miscellaneous.vanillaSprings) {
            disable(ctx, MiscOverworldPlacements.SPRING_WATER, MiscOverworldFeatures.SPRING_WATER, CountPlacement.of(25), InSquarePlacement.spread(), HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(192)), BiomeFilter.biome());
        }
        
        if(!miscellaneous.vanillaLavaSprings) {
            disable(ctx, MiscOverworldPlacements.SPRING_LAVA, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD, CountPlacement.of(20), InSquarePlacement.spread(), HeightRangePlacement.of(VeryBiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome());
            disable(ctx, MiscOverworldPlacements.SPRING_LAVA_FROZEN, MiscOverworldFeatures.SPRING_LAVA_FROZEN, CountPlacement.of(20), InSquarePlacement.spread(), HeightRangePlacement.of(VeryBiasedToBottomHeight.of(VerticalAnchor.bottom(), VerticalAnchor.belowTop(8), 8)), BiomeFilter.biome());
        }
        
        if(!miscellaneous.vanillaLavaLakes) {
            disable(ctx, MiscOverworldPlacements.LAKE_LAVA_SURFACE, MiscOverworldFeatures.LAKE_LAVA, RarityFilter.onAverageOnceEvery(200), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_WORLD_SURFACE, BiomeFilter.biome());
            disable(ctx, MiscOverworldPlacements.LAKE_LAVA_UNDERGROUND, MiscOverworldFeatures.LAKE_LAVA, RarityFilter.onAverageOnceEvery(9), InSquarePlacement.spread(), HeightRangePlacement.of(UniformHeight.of(VerticalAnchor.absolute(0), VerticalAnchor.top())), EnvironmentScanPlacement.scanningFor(Direction.DOWN, BlockPredicate.allOf(BlockPredicate.not(BlockPredicate.ONLY_IN_AIR_PREDICATE), BlockPredicate.insideWorld(new BlockPos(0, -5, 0))), 32), SurfaceRelativeThresholdFilter.of(Heightmap.Types.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -5), BiomeFilter.biome());
        }
        
        if(miscellaneous.strataDecorator) {
            disable(ctx, OrePlacements.ORE_DIRT, OreFeatures.ORE_DIRT, commonOrePlacement(7, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(160))));
            disable(ctx, OrePlacements.ORE_GRAVEL, OreFeatures.ORE_GRAVEL, commonOrePlacement(14, HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.top())));
            disable(ctx, OrePlacements.ORE_GRANITE_UPPER, OreFeatures.ORE_GRANITE, rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
            disable(ctx, OrePlacements.ORE_DIORITE_UPPER, OreFeatures.ORE_DIORITE, rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
            disable(ctx, OrePlacements.ORE_ANDESITE_UPPER, OreFeatures.ORE_ANDESITE, rareOrePlacement(6, HeightRangePlacement.uniform(VerticalAnchor.absolute(64), VerticalAnchor.absolute(128))));
            
            disable(ctx, OrePlacements.ORE_GRANITE_LOWER, OreFeatures.ORE_GRANITE, commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
            disable(ctx, OrePlacements.ORE_DIORITE_LOWER, OreFeatures.ORE_DIORITE, commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
            disable(ctx, OrePlacements.ORE_ANDESITE_LOWER, OreFeatures.ORE_ANDESITE, commonOrePlacement(2, HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60))));
        }
	}

	private static void disable(BootstapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> placementKey, ResourceKey<ConfiguredFeature<?, ?>> featureKey, PlacementModifier... extraModifiers) {
		disable(ctx, placementKey, featureKey, ImmutableList.copyOf(extraModifiers));
	}
		
	private static void disable(BootstapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> placementKey, ResourceKey<ConfiguredFeature<?, ?>> featureKey, List<PlacementModifier> extraModifiers) {
		HolderGetter<ConfiguredFeature<?, ?>> features = ctx.lookup(Registries.CONFIGURED_FEATURE);
		
		List<PlacementModifier> modifiers = new ArrayList<>(extraModifiers);
		modifiers.add(NeverPlacementModifier.INSTANCE);
		PlacementUtils.register(ctx, placementKey, features.getOrThrow(featureKey), modifiers);
	}

    private static List<PlacementModifier> orePlacement(PlacementModifier count, PlacementModifier height) {
        return List.of(count, InSquarePlacement.spread(), height, BiomeFilter.biome());
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier height) {
        return orePlacement(CountPlacement.of(count), height);
    }

    private static List<PlacementModifier> rareOrePlacement(int count, PlacementModifier height) {
        return orePlacement(RarityFilter.onAverageOnceEvery(count), height);
    }
}
