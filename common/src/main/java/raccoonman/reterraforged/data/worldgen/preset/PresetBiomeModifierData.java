package raccoonman.reterraforged.data.worldgen.preset;

import java.util.Map;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
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
import raccoonman.reterraforged.world.worldgen.biome.modifier.Filter;
import raccoonman.reterraforged.world.worldgen.biome.modifier.Order;

//TODO organize all of this stuff cause god damn
public class PresetBiomeModifierData {
	public static final ResourceKey<BiomeModifier> ADD_EROSION = createKey("add_erosion");
	public static final ResourceKey<BiomeModifier> ADD_SNOW_PROCESSING = createKey("add_snow_processing");
	public static final ResourceKey<BiomeModifier> ADD_SWAMP_SURFACE = createKey("add_swamp_surface");
	
	public static final ResourceKey<BiomeModifier> REPLACE_PLAINS_TREES = createKey("replace_plains_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FOREST_TREES = createKey("replace_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FLOWER_FOREST_TREES = createKey("replace_flower_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_BIRCH_TREES = createKey("replace_birch_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_DARK_FOREST_TREES = createKey("replace_dark_forest_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SAVANNA_TREES = createKey("replace_savanna_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SWAMP_TREES = createKey("replace_swamp_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_MEADOW_TREES = createKey("replace_meadow_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_FIR_TREES = createKey("replace_fir_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_WINDSWEPT_HILLS_FIR_TREES = createKey("replace_windswept_hills_fir_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_PINE_TREES = createKey("replace_pine_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SPRUCE_TREES = createKey("replace_spruce_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_SPRUCE_TUNDRA_TREES = createKey("replace_spruce_tundra_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_REDWOOD_TREES = createKey("replace_redwood_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_JUNGLE_TREES = createKey("replace_jungle_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_JUNGLE_EDGE_TREES = createKey("replace_jungle_edge_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_BADLANDS_TREES = createKey("replace_jungle_edge_trees");
	public static final ResourceKey<BiomeModifier> REPLACE_WOODED_BADLANDS_TREES = createKey("replace_wooded_badlands_trees");

	public static final ResourceKey<BiomeModifier> ADD_FOREST_GRASS = createKey("add_forest_grass");
	public static final ResourceKey<BiomeModifier> ADD_BIRCH_FOREST_GRASS = createKey("add_birch_forest_grass");
	
	public static void bootstrap(Preset preset, BootstapContext<BiomeModifier> ctx) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);
		HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);

		HolderSet<Biome> plains = HolderSet.direct(biomes.getOrThrow(Biomes.RIVER), biomes.getOrThrow(Biomes.PLAINS), biomes.getOrThrow(Biomes.SUNFLOWER_PLAINS));
		HolderSet<Biome> forests = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST));
		HolderSet<Biome> flowerForests = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.FLOWER_FOREST));
		HolderSet<Biome> birchForests = HolderSet.direct(biomes.getOrThrow(Biomes.BIRCH_FOREST), biomes.getOrThrow(Biomes.OLD_GROWTH_BIRCH_FOREST));
		HolderSet<Biome> darkForests = HolderSet.direct(biomes.getOrThrow(Biomes.DARK_FOREST));
		HolderSet<Biome> savannas = HolderSet.direct(biomes.getOrThrow(Biomes.SAVANNA), biomes.getOrThrow(Biomes.SAVANNA_PLATEAU), biomes.getOrThrow(Biomes.WINDSWEPT_SAVANNA));
		HolderSet<Biome> swamps = HolderSet.direct(biomes.getOrThrow(Biomes.SWAMP));
		HolderSet<Biome> meadows = HolderSet.direct(biomes.getOrThrow(Biomes.MEADOW));
		HolderSet<Biome> firForests = HolderSet.direct(biomes.getOrThrow(Biomes.GROVE), biomes.getOrThrow(Biomes.WINDSWEPT_FOREST));
		HolderSet<Biome> windsweptHills = HolderSet.direct(biomes.getOrThrow(Biomes.WINDSWEPT_HILLS), biomes.getOrThrow(Biomes.WINDSWEPT_GRAVELLY_HILLS));
		HolderSet<Biome> pineForests = HolderSet.direct(biomes.getOrThrow(Biomes.TAIGA), biomes.getOrThrow(Biomes.OLD_GROWTH_SPRUCE_TAIGA));
		HolderSet<Biome> spruceForests = HolderSet.direct(biomes.getOrThrow(Biomes.SNOWY_TAIGA));
		HolderSet<Biome> spruceTundras = HolderSet.direct(biomes.getOrThrow(Biomes.SNOWY_PLAINS));
		HolderSet<Biome> redwoodForests = HolderSet.direct(biomes.getOrThrow(Biomes.OLD_GROWTH_PINE_TAIGA));
		HolderSet<Biome> jungles = HolderSet.direct(biomes.getOrThrow(Biomes.JUNGLE), biomes.getOrThrow(Biomes.BAMBOO_JUNGLE));
		HolderSet<Biome> jungleEdges = HolderSet.direct(biomes.getOrThrow(Biomes.SPARSE_JUNGLE));
		HolderSet<Biome> woodedBadlands = HolderSet.direct(biomes.getOrThrow(Biomes.WOODED_BADLANDS));
		
		HolderSet<Biome> forestsWithGrass = HolderSet.direct(biomes.getOrThrow(Biomes.FOREST), biomes.getOrThrow(Biomes.DARK_FOREST));
		
		Holder<PlacedFeature> plainsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PLAINS_TREES);
		Holder<PlacedFeature> forestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_TREES);
		Holder<PlacedFeature> flowerForestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FLOWER_FOREST_TREES);
		Holder<PlacedFeature> birchTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_TREES);
		Holder<PlacedFeature> darkForestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.DARK_FOREST_TREES);
		Holder<PlacedFeature> savannaTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SAVANNA_TREES);
		Holder<PlacedFeature> swampTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_TREES);
		Holder<PlacedFeature> meadowTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.MEADOW_TREES);
		Holder<PlacedFeature> firTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FIR_TREES);
		Holder<PlacedFeature> windsweptHillsFirTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.WINDSWEPT_HILLS_FIR_TREES);
		Holder<PlacedFeature> pineTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PINE_TREES);
		Holder<PlacedFeature> spruceTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_TREES);
		Holder<PlacedFeature> spruceTundraTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_TUNDRA_TREES);
		Holder<PlacedFeature> redwoodTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.REDWOOD_TREES);
		Holder<PlacedFeature> jungleTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_TREES);
		Holder<PlacedFeature> jungleEdgeTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_EDGE_TREES);
		Holder<PlacedFeature> woodedBadlandsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.WOODED_BADLANDS_TREES);

		if(miscellaneous.customBiomeFeatures) {
			ctx.register(REPLACE_PLAINS_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, plains, Map.of(
				VegetationPlacements.TREES_PLAINS, plainsTrees
			)));
			ctx.register(REPLACE_FOREST_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, forests, Map.of(
				VegetationPlacements.TREES_BIRCH_AND_OAK, forestTrees
			)));
			ctx.register(REPLACE_FLOWER_FOREST_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, flowerForests, Map.of(
				VegetationPlacements.TREES_FLOWER_FOREST, flowerForestTrees
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
			ctx.register(REPLACE_MEADOW_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, meadows, Map.of(
				VegetationPlacements.TREES_MEADOW, meadowTrees
			)));
			ctx.register(REPLACE_FIR_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, firForests, Map.of(
				VegetationPlacements.TREES_GROVE, firTrees,
				VegetationPlacements.TREES_WINDSWEPT_FOREST, firTrees
			)));
			ctx.register(REPLACE_WINDSWEPT_HILLS_FIR_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, windsweptHills, Map.of(
				VegetationPlacements.TREES_WINDSWEPT_HILLS, windsweptHillsFirTrees
			)));
			ctx.register(REPLACE_PINE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, pineForests, Map.of(
				VegetationPlacements.TREES_TAIGA, pineTrees,
				VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA, pineTrees
			)));
			ctx.register(REPLACE_SPRUCE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, spruceForests, Map.of(
				VegetationPlacements.TREES_TAIGA, spruceTrees
			)));
			ctx.register(REPLACE_SPRUCE_TUNDRA_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, spruceTundras, Map.of(
				VegetationPlacements.TREES_SNOWY, spruceTundraTrees
			)));
			ctx.register(REPLACE_REDWOOD_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, redwoodForests, Map.of(
				VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA, redwoodTrees
			)));
			ctx.register(REPLACE_JUNGLE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, jungles, Map.of(
				VegetationPlacements.TREES_JUNGLE, jungleTrees,
				VegetationPlacements.BAMBOO_VEGETATION, jungleTrees
			)));
			ctx.register(REPLACE_JUNGLE_EDGE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, jungleEdges, Map.of(
				VegetationPlacements.TREES_SPARSE_JUNGLE, jungleEdgeTrees
			)));
//			ctx.register(REPLACE_BADLANDS_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, jungleEdges, Map.of(
//				VegetationPlacements.TREES_BADLANDS, jungleEdgeTrees
//			)));
			ctx.register(REPLACE_WOODED_BADLANDS_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, woodedBadlands, Map.of(
				VegetationPlacements.TREES_BADLANDS, woodedBadlandsTrees
			)));
			ctx.register(ADD_FOREST_GRASS, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, forestsWithGrass, placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_GRASS)));
			ctx.register(ADD_BIRCH_FOREST_GRASS, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, birchForests, placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_FOREST_GRASS)));
		}
		
		if(miscellaneous.erosionDecorator) {
			ctx.register(ADD_EROSION, prepend(GenerationStep.Decoration.RAW_GENERATION, placedFeatures.getOrThrow(PresetPlacedFeatures.ERODE)));
		}
		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			ctx.register(ADD_SNOW_PROCESSING, append(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, placedFeatures.getOrThrow(PresetPlacedFeatures.DECORATE_SNOW)));
		}
		
		ctx.register(ADD_SWAMP_SURFACE, prepend(GenerationStep.Decoration.RAW_GENERATION, Filter.Behavior.WHITELIST, swamps, placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_SURFACE)));
	}
	
	@SafeVarargs
	private static BiomeModifier prepend(GenerationStep.Decoration step, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.PREPEND, step, HolderSet.direct(features));
	}

	@SafeVarargs
	private static BiomeModifier prepend(GenerationStep.Decoration step, Filter.Behavior filterBehavior, HolderSet<Biome> biomes, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.PREPEND, step, filterBehavior, biomes, HolderSet.direct(features));
	}

	@SafeVarargs
	private static BiomeModifier append(GenerationStep.Decoration step, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.APPEND, step, HolderSet.direct(features));
	}
	
	@SafeVarargs
	private static BiomeModifier append(GenerationStep.Decoration step, Filter.Behavior filterBehavior, HolderSet<Biome> biomes, Holder<PlacedFeature>... features) {
		return BiomeModifiers.add(Order.APPEND, step, filterBehavior, biomes, HolderSet.direct(features));
	}
	
	private static ResourceKey<BiomeModifier> createKey(String name) {
        return ResourceKey.create(RTFRegistries.BIOME_MODIFIER, RTFCommon.location(name));
	}
}
