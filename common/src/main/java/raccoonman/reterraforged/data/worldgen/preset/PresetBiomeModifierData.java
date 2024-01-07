package raccoonman.reterraforged.data.worldgen.preset;

import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.features.VegetationFeatures;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifiers;
import raccoonman.reterraforged.world.worldgen.biome.modifier.Order;

public class PresetBiomeModifierData {
	public static final ResourceKey<BiomeModifier> ADD_EROSION = createKey("add_erosion");
	public static final ResourceKey<BiomeModifier> ADD_SNOW_PROCESSING = createKey("add_snow_processing");
	public static final ResourceKey<BiomeModifier> ADD_SWAMP_SURFACE = createKey("add_swamp_surface");
	
	public static final ResourceKey<BiomeModifier> REPLACE_PLAINS_TREES = createKey("replace_plains_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FOREST_TREES = createKey("replace_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_BIRCH_TREES = createKey("replace_birch_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_DARK_FOREST_TREES = createKey("replace_dark_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SAVANNA_TREES = createKey("replace_savanna_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SWAMP_TREES = createKey("replace_swamp_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FIR_TREES = createKey("replace_fir_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_PINE_TREES = createKey("replace_pine_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SPRUCE_TREES = createKey("replace_spruce_trees");

	public static final ResourceKey<BiomeModifier> ADD_FOREST_GRASS = createKey("add_forest_grass");
	public static final ResourceKey<BiomeModifier> ADD_BIRCH_FOREST_GRASS = createKey("add_birch_forest_grass");
	
	public static void bootstrap(Preset preset, BootstapContext<BiomeModifier> ctx) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);

		HolderSet<Biome> plains = HolderSet.direct(biomes.getOrThrow(Biomes.RIVER), biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS));
		HolderSet<Biome> forests = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST));
		HolderSet<Biome> birchForests = HolderSet.direct(biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST));
		HolderSet<Biome> darkForests = HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST));
		HolderSet<Biome> savannas = HolderSet.direct(biomes.getOrThrow(Biomes.SAVANNA), biomes.getOrThrow(Biomes.SAVANNA_PLATEAU), biomes.getOrThrow(Biomes.WINDSWEPT_SAVANNA));
		HolderSet<Biome> swamps = HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP));
		HolderSet<Biome> firForests = HolderSet.direct(biomes.getOrThrow(Biomes.GROVE), biomes.getOrThrow(Biomes.WINDSWEPT_HILLS), biomes.getOrThrow(Biomes.WINDSWEPT_GRAVELLY_HILLS), biomes.getOrThrow(Biomes.WINDSWEPT_FOREST));
		HolderSet<Biome> pineForests = HolderSet.direct(biomes.getOrThrow(Biomes.TAIGA), biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA));
		HolderSet<Biome> spruceForests = HolderSet.direct(biomes.getOrThrow(Biomes.SNOWY_TAIGA));
		HolderSet<Biome> forestsWithGrass = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.DARK_FOREST));
				
		Holder<PlacedFeature> plainsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PLAINS_TREES);
		Holder<PlacedFeature> forestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_TREES);
		Holder<PlacedFeature> birchTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_TREES);
		Holder<PlacedFeature> darkForestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.DARK_FOREST_TREES);
		Holder<PlacedFeature> savannaTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SAVANNA_TREES);
		Holder<PlacedFeature> swampTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_TREES);
		Holder<PlacedFeature> firTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FIR_TREES);
		Holder<PlacedFeature> pineTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PINE_TREES);
		Holder<PlacedFeature> spruceTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_TREES);
		
		ctx.register(REPLACE_PLAINS_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, plains, Map.of(
			VegetationPlacements.TREES_PLAINS, plainsTrees
		)));
		ctx.register(REPLACE_FOREST_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, forests, Map.of(
			VegetationPlacements.TREES_BIRCH_AND_OAK, forestTrees
		)));
		ctx.register(REPLACE_BIRCH_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, birchForests, Map.of(
			VegetationPlacements.TREES_BIRCH, birchTrees,
			VegetationPlacements.BIRCH_TALL, birchTrees
		)));
		ctx.register(REPLACE_DARK_FOREST_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, darkForests, Map.of(
			VegetationPlacements.DARK_FOREST_VEGETATION, darkForestTrees
		)));
		ctx.register(REPLACE_SAVANNA_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, savannas, Map.of(
			VegetationPlacements.TREES_SAVANNA, savannaTrees,
			VegetationPlacements.TREES_WINDSWEPT_SAVANNA, savannaTrees
		)));
		ctx.register(REPLACE_SWAMP_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, swamps, Map.of(
			VegetationPlacements.TREES_SWAMP, swampTrees
		)));
		ctx.register(REPLACE_FIR_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, firForests, Map.of(
			VegetationPlacements.TREES_WINDSWEPT_FOREST, firTrees,
			VegetationPlacements.TREES_TAIGA, firTrees,
			VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA, firTrees,
			VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA, firTrees,
			VegetationPlacements.TREES_GROVE, firTrees
		)));
		ctx.register(REPLACE_PINE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, pineForests, Map.of(
			VegetationPlacements.TREES_TAIGA, pineTrees,
			VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA, pineTrees,
			VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA, pineTrees
		)));
		ctx.register(REPLACE_SPRUCE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, spruceForests, Map.of(
			VegetationPlacements.TREES_TAIGA, spruceTrees,
			VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA, spruceTrees,
			VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA, spruceTrees
		)));
		
		ctx.register(ADD_FOREST_GRASS, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, forestsWithGrass, placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_GRASS)));
		ctx.register(ADD_BIRCH_FOREST_GRASS, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, birchForests, placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_FOREST_GRASS)));

		if(miscellaneous.erosionDecorator) {
			ctx.register(ADD_EROSION, prepend(GenerationStep.Decoration.RAW_GENERATION, placedFeatures.getOrThrow(PresetPlacedFeatures.ERODE)));
		}
		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			ctx.register(ADD_SNOW_PROCESSING, append(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, placedFeatures.getOrThrow(PresetPlacedFeatures.DECORATE_SNOW)));
		}
		
		ctx.register(ADD_SWAMP_SURFACE, prepend(GenerationStep.Decoration.RAW_GENERATION, swamps, placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_SURFACE)));
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
