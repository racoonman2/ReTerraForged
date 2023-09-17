package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.common.level.levelgen.placement.NeverPlacementModifier;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class MCPlacedFeatures {
	
	//TODO add vanillaLakes functionality (what does that even do in 1.16?)
	public static void bootstrap(BootstapContext<PlacedFeature> ctx, Preset preset) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
        HolderGetter<ConfiguredFeature<?, ?>> features = ctx.lookup(Registries.CONFIGURED_FEATURE);

    	//TODO: we should find some way to remove the features from the biome feature list instead
        if(!miscellaneous.vanillaSprings) {
        	PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_WATER, features.getOrThrow(MiscOverworldFeatures.SPRING_WATER), NeverPlacementModifier.INSTANCE, BiomeFilter.biome());
        }
        if(!miscellaneous.vanillaLavaSprings) {
    	    PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_LAVA, features.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_OVERWORLD), NeverPlacementModifier.INSTANCE, BiomeFilter.biome());
            PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_LAVA_FROZEN, features.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_FROZEN), NeverPlacementModifier.INSTANCE, BiomeFilter.biome());
        }
        if(!miscellaneous.vanillaLavaLakes) {
            PlacementUtils.register(ctx, MiscOverworldPlacements.LAKE_LAVA_SURFACE, features.getOrThrow(MiscOverworldFeatures.LAKE_LAVA), NeverPlacementModifier.INSTANCE, BiomeFilter.biome());
        }
	}
}
