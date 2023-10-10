package raccoonman.reterraforged.common.worldgen.data;

import java.util.ArrayList;
import java.util.Collections;
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
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import raccoonman.reterraforged.common.level.levelgen.placement.NeverPlacementModifier;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class MCPlacedFeatures {
	
	//TODO generate snow under trees (or we could support for this and people can just the snow-under-trees mod)
	public static void bootstrap(BootstapContext<PlacedFeature> ctx, Preset preset) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();

        if(!miscellaneous.vanillaSprings) {
            disable(ctx, MiscOverworldPlacements.SPRING_WATER, MiscOverworldFeatures.SPRING_WATER);
        }
        
        if(!miscellaneous.vanillaLavaSprings) {
            disable(ctx, MiscOverworldPlacements.SPRING_LAVA, MiscOverworldFeatures.SPRING_LAVA_OVERWORLD);
            disable(ctx, MiscOverworldPlacements.SPRING_LAVA_FROZEN, MiscOverworldFeatures.SPRING_LAVA_FROZEN);
        }
        
        if(!miscellaneous.vanillaLavaLakes) {
            disable(ctx, MiscOverworldPlacements.LAKE_LAVA_SURFACE, MiscOverworldFeatures.LAKE_LAVA);
        }
        
        if(miscellaneous.strataDecorator) {
            disable(ctx, OrePlacements.ORE_DIRT, OreFeatures.ORE_DIRT);
            disable(ctx, OrePlacements.ORE_GRAVEL, OreFeatures.ORE_GRAVEL);
            disable(ctx, OrePlacements.ORE_GRANITE_UPPER, OreFeatures.ORE_GRANITE);
            disable(ctx, OrePlacements.ORE_DIORITE_UPPER, OreFeatures.ORE_DIORITE);
            disable(ctx, OrePlacements.ORE_ANDESITE_UPPER, OreFeatures.ORE_ANDESITE);
            
            //note: we have to include this modifier as well or else we get feature order errors
            PlacementModifier low = HeightRangePlacement.uniform(VerticalAnchor.absolute(0), VerticalAnchor.absolute(60));
            disable(ctx, OrePlacements.ORE_GRANITE_LOWER, OreFeatures.ORE_GRANITE, low);
            disable(ctx, OrePlacements.ORE_DIORITE_LOWER, OreFeatures.ORE_DIORITE, low);
            disable(ctx, OrePlacements.ORE_ANDESITE_LOWER, OreFeatures.ORE_ANDESITE, low);
        }
	}

	private static void disable(BootstapContext<PlacedFeature> ctx, ResourceKey<PlacedFeature> placementKey, ResourceKey<ConfiguredFeature<?, ?>> featureKey, PlacementModifier... extraModifiers) {
		HolderGetter<ConfiguredFeature<?, ?>> features = ctx.lookup(Registries.CONFIGURED_FEATURE);
		
		List<PlacementModifier> modifiers = new ArrayList<>();
		modifiers.add(NeverPlacementModifier.INSTANCE);
		modifiers.add(BiomeFilter.biome());
		Collections.addAll(modifiers, extraModifiers);
		PlacementUtils.register(ctx, placementKey, features.getOrThrow(featureKey), modifiers);
	}
}
