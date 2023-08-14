package raccoonman.reterraforged.common.registries.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.MiscOverworldPlacements;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public final class RTFPlacedFeatures {
	
	public static void register(BootstapContext<PlacedFeature> ctx) {
        HolderGetter<ConfiguredFeature<?, ?>> features = ctx.lookup(Registries.CONFIGURED_FEATURE);
        
	    PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_LAVA, features.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_OVERWORLD), CountPlacement.of(0), BiomeFilter.biome());
        PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_LAVA_FROZEN, features.getOrThrow(MiscOverworldFeatures.SPRING_LAVA_FROZEN), CountPlacement.of(0), BiomeFilter.biome());
        PlacementUtils.register(ctx, MiscOverworldPlacements.SPRING_WATER, features.getOrThrow(MiscOverworldFeatures.SPRING_WATER), CountPlacement.of(0), BiomeFilter.biome());
	}
}
