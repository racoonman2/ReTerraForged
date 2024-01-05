package raccoonman.reterraforged.data.worldgen;

import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifiers;
import raccoonman.reterraforged.world.worldgen.biome.modifier.Order;

public class BiomeModifierData {
	public static final ResourceKey<BiomeModifier> ADD_PRE_PROCESSING = createKey("add_pre_processing");
	public static final ResourceKey<BiomeModifier> ADD_POST_PROCESSING = createKey("add_post_processing");
	public static final ResourceKey<BiomeModifier> ADD_SWAMP_SURFACE = createKey("add_swamp_surface");

	public static final ResourceKey<BiomeModifier> REPLACE_ACACIA_TREES = createKey("replace_acacia_trees");
	
	public static void bootstrap(Preset preset, BootstapContext<BiomeModifier> ctx) {
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
		
		HolderSet<Biome> forests = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.DARK_FOREST));
		HolderSet<Biome> swamps = HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP));

		ctx.register(ADD_PRE_PROCESSING, prepend(GenerationStep.Decoration.RAW_GENERATION, placedFeatures.getOrThrow(RTFPlacedFeatures.ERODE)));
		ctx.register(ADD_POST_PROCESSING, append(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, placedFeatures.getOrThrow(RTFPlacedFeatures.DECORATE_SNOW)));
		ctx.register(ADD_SWAMP_SURFACE, prepend(GenerationStep.Decoration.RAW_GENERATION, swamps, placedFeatures.getOrThrow(RTFPlacedFeatures.SWAMP_SURFACE)));
		
		ctx.register(REPLACE_ACACIA_TREES, replaceAcaciaTrees(placedFeatures));
//		ctx.register(ADD_FOREST_GRASS, BiomeModifiers.add(Order.PREPEND, GenerationStep.Decoration.VEGETAL_DECORATION, forests, placedFeatures.getOrThrow(RTFPlacedFeatures.FOREST_GRASS)));
	}
	
	private static BiomeModifier replaceAcaciaTrees(HolderGetter<PlacedFeature> placedFeatures) {
		return BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, Map.of(
			VegetationPlacements.TREES_SAVANNA, placedFeatures.getOrThrow(RTFPlacedFeatures.ACACIA_TREES)
		));
	}
	
	@SafeVarargs
	private static BiomeModifier prepend(GenerationStep.Decoration step, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.PREPEND, step, HolderSet.direct(features));
	}

	@SafeVarargs
	private static BiomeModifier prepend(GenerationStep.Decoration step, HolderSet<Biome> biomes, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.PREPEND, step, biomes, HolderSet.direct(features));
	}

	@SafeVarargs
	private static BiomeModifier append(GenerationStep.Decoration step, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.APPEND, step, HolderSet.direct(features));
	}
	
	@SafeVarargs
	private static BiomeModifier append(GenerationStep.Decoration step, HolderSet<Biome> biomes, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.APPEND, step, biomes, HolderSet.direct(features));
	}
	
	private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(RTFRegistries.BIOME_MODIFIER, RTFCommon.location(name));
	}
}
