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
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldPreset;
import raccoonman.reterraforged.registries.RTFRegistries;
import raccoonman.reterraforged.tags.RTFBiomeTags;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifier;
import raccoonman.reterraforged.world.worldgen.biome.modifier.BiomeModifiers;
import raccoonman.reterraforged.world.worldgen.biome.modifier.Filter;
import raccoonman.reterraforged.world.worldgen.biome.modifier.Order;

public class PresetBiomeModifierData {
	public static final ResourceKey<BiomeModifier> ERODE = createKey("raw_generation/erode");
	public static final ResourceKey<BiomeModifier> SWAMP_GENERATION = createKey("raw_generation/swamp_surface");
	public static final ResourceKey<BiomeModifier> ERODE_SNOW = createKey("modification/erode_snow");
	
	public static final ResourceKey<BiomeModifier> PLAINS_TREES = createKey("vegetation/trees/plains");
	public static final ResourceKey<BiomeModifier> FOREST_TREES = createKey("vegetation/trees/forest");
	public static final ResourceKey<BiomeModifier> FLOWER_FOREST_TREES = createKey("vegetation/trees/flower_forest");
	public static final ResourceKey<BiomeModifier> BIRCH_TREES = createKey("vegetation/trees/birch");
	public static final ResourceKey<BiomeModifier> DARK_FOREST_TREES = createKey("vegetation/trees/dark_forest");
	public static final ResourceKey<BiomeModifier> SAVANNA_TREES = createKey("vegetation/trees/savanna");
	public static final ResourceKey<BiomeModifier> SWAMP_TREES = createKey("vegetation/trees/swamp");
	public static final ResourceKey<BiomeModifier> MEADOW_TREES = createKey("vegetation/trees/meadow");
	public static final ResourceKey<BiomeModifier> FIR_TREES = createKey("vegetation/trees/fir");
	public static final ResourceKey<BiomeModifier> GROVE_TREES = createKey("vegetation/trees/grove");
	public static final ResourceKey<BiomeModifier> WINDSWEPT_HILLS_FIR_TREES = createKey("vegetation/trees/fir_windswept_hills");
	public static final ResourceKey<BiomeModifier> PINE_TREES = createKey("vegetation/trees/pine");
	public static final ResourceKey<BiomeModifier> SPRUCE_TREES = createKey("vegetation/trees/spruce");
	public static final ResourceKey<BiomeModifier> SPRUCE_TUNDRA_TREES = createKey("vegetation/trees/spruce_tundra");
	public static final ResourceKey<BiomeModifier> REDWOOD_TREES = createKey("vegetation/trees/redwood");
	public static final ResourceKey<BiomeModifier> JUNGLE_TREES = createKey("vegetation/trees/jungle");
	public static final ResourceKey<BiomeModifier> JUNGLE_EDGE_TREES = createKey("vegetation/trees/jungle_edge");
	public static final ResourceKey<BiomeModifier> BADLANDS_TREES = createKey("vegetation/trees/badlands");
	public static final ResourceKey<BiomeModifier> WOODED_BADLANDS_TREES = createKey("vegetation/trees/wooded_badlands");

	public static final ResourceKey<BiomeModifier> MARSH_BUSH = createKey("vegetation/bushes/marsh");
	public static final ResourceKey<BiomeModifier> PLAINS_BUSH = createKey("vegetation/bushes/plains");
	public static final ResourceKey<BiomeModifier> STEPPE_BUSH = createKey("vegetation/bushes/steppe");
	public static final ResourceKey<BiomeModifier> COLD_STEPPE_BUSH = createKey("vegetation/bushes/cold_steppe");
	public static final ResourceKey<BiomeModifier> ADD_TAIGA_SCRUB_BUSH = createKey("vegetation/bushes/taiga_scrub");
	
	public static final ResourceKey<BiomeModifier> FOREST_GRASS = createKey("vegetation/grass/forest");
	public static final ResourceKey<BiomeModifier> BIRCH_FOREST_GRASS = createKey("vegetation/grass/birch_forest");
	
	public static void bootstrap(WorldPreset preset, BootstapContext<BiomeModifier> ctx) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
		HolderGetter<Biome> biomes = ctx.lookup(Registries.BIOME);

		HolderSet<Biome> hasSwampGeneration = biomes.getOrThrow(RTFBiomeTags.HAS_SWAMP_GENERATION);
		
		if(miscellaneous.erosionDecorator) {
			ctx.register(ERODE, prepend(GenerationStep.Decoration.RAW_GENERATION, placedFeatures.getOrThrow(PresetPlacedFeatures.ERODE)));
		}
		
		ctx.register(SWAMP_GENERATION, prepend(GenerationStep.Decoration.RAW_GENERATION, Filter.Behavior.WHITELIST, hasSwampGeneration, placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_SURFACE)));
		
		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			ctx.register(ERODE_SNOW, append(GenerationStep.Decoration.TOP_LAYER_MODIFICATION, placedFeatures.getOrThrow(PresetPlacedFeatures.ERODE_SNOW)));
		}
		
		if(miscellaneous.customBiomeFeatures) {
			HolderSet<Biome> hasSwampTrees = biomes.getOrThrow(RTFBiomeTags.HAS_SWAMP_TREES);
			HolderSet<Biome> hasPlainsTrees = biomes.getOrThrow(RTFBiomeTags.HAS_PLAINS_TREES);
			HolderSet<Biome> hasForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_FOREST_TREES);
			HolderSet<Biome> hasFlowerForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_FLOWER_FOREST_TREES);
			HolderSet<Biome> hasBirchForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_BIRCH_FOREST_TREES);
			HolderSet<Biome> hasDarkForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_DARK_FOREST_TREES);
			HolderSet<Biome> hasSavannaTrees = biomes.getOrThrow(RTFBiomeTags.HAS_SAVANNA_TREES);
			HolderSet<Biome> hasMeadowTrees = biomes.getOrThrow(RTFBiomeTags.HAS_MEADOW_TREES);
			HolderSet<Biome> hasFirForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_FIR_FOREST_TREES);
			HolderSet<Biome> hasGroveTrees = biomes.getOrThrow(RTFBiomeTags.HAS_GROVE_TREES);
			HolderSet<Biome> hasWindsweptHillsTrees = biomes.getOrThrow(RTFBiomeTags.HAS_WINDSWEPT_HILLS_TREES);
			HolderSet<Biome> hasPineForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_PINE_FOREST_TREES);
			HolderSet<Biome> hasSpruceForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_SPRUCE_FOREST_TREES);
			HolderSet<Biome> hasSpruceTundraTrees = biomes.getOrThrow(RTFBiomeTags.HAS_SPRUCE_TUNDRA_TREES);
			HolderSet<Biome> hasRedwoodForestTrees = biomes.getOrThrow(RTFBiomeTags.HAS_REDWOOD_FOREST_TREES);
			HolderSet<Biome> hasJungleTrees = biomes.getOrThrow(RTFBiomeTags.HAS_JUNGLE_TREES);
			HolderSet<Biome> hasJungleEdgeTrees = biomes.getOrThrow(RTFBiomeTags.HAS_JUNGLE_EDGE_TREES);
			HolderSet<Biome> hasBadlandsTrees = biomes.getOrThrow(RTFBiomeTags.HAS_BADLANDS_TREES);
			HolderSet<Biome> hasWoodedBadlandsTrees = biomes.getOrThrow(RTFBiomeTags.HAS_WOODED_BADLANDS_TREES);
			
			Holder<PlacedFeature> plainsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PLAINS_TREES);
			Holder<PlacedFeature> forestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_TREES);
			Holder<PlacedFeature> flowerForestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FLOWER_FOREST_TREES);
			Holder<PlacedFeature> birchTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_TREES);
			Holder<PlacedFeature> darkForestTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.DARK_FOREST_TREES);
			Holder<PlacedFeature> savannaTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SAVANNA_TREES);
			Holder<PlacedFeature> swampTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SWAMP_TREES);
			Holder<PlacedFeature> meadowTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.MEADOW_TREES);
			Holder<PlacedFeature> firTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.FIR_TREES);
			Holder<PlacedFeature> groveTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.GROVE_TREES);
			Holder<PlacedFeature> windsweptHillsFirTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.WINDSWEPT_HILLS_FIR_TREES);
			Holder<PlacedFeature> pineTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.PINE_TREES);
			Holder<PlacedFeature> spruceTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_TREES);
			Holder<PlacedFeature> spruceTundraTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_TUNDRA_TREES);
			Holder<PlacedFeature> redwoodTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.REDWOOD_TREES);
			Holder<PlacedFeature> jungleTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_TREES);
			Holder<PlacedFeature> jungleEdgeTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_EDGE_TREES);
			Holder<PlacedFeature> badlandsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.BADLANDS_TREES);
			Holder<PlacedFeature> woodedBadlandsTrees = placedFeatures.getOrThrow(PresetPlacedFeatures.WOODED_BADLANDS_TREES);
			
			ctx.register(PLAINS_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasPlainsTrees, Map.of(
				VegetationPlacements.TREES_PLAINS, plainsTrees,
				VegetationPlacements.TREES_WATER, plainsTrees
			)));
			ctx.register(FOREST_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasForestTrees, Map.of(
				VegetationPlacements.TREES_BIRCH_AND_OAK, forestTrees
			)));
			ctx.register(FLOWER_FOREST_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasFlowerForestTrees, Map.of(
				VegetationPlacements.TREES_FLOWER_FOREST, flowerForestTrees
			)));
			ctx.register(BIRCH_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasBirchForestTrees, Map.of(
				VegetationPlacements.TREES_BIRCH, birchTrees,
				VegetationPlacements.BIRCH_TALL, birchTrees
			)));
			ctx.register(DARK_FOREST_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasDarkForestTrees, Map.of(
				VegetationPlacements.DARK_FOREST_VEGETATION, darkForestTrees
			)));
			ctx.register(SAVANNA_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasSavannaTrees, Map.of(
				VegetationPlacements.TREES_SAVANNA, savannaTrees
			)));
			ctx.register(SWAMP_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasSwampTrees, Map.of(
				VegetationPlacements.TREES_SWAMP, swampTrees
			)));
			ctx.register(MEADOW_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasMeadowTrees, Map.of(
				VegetationPlacements.TREES_MEADOW, meadowTrees
			)));
			ctx.register(FIR_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasFirForestTrees, Map.of(
				VegetationPlacements.TREES_WINDSWEPT_FOREST, firTrees
			)));
			ctx.register(GROVE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasGroveTrees, Map.of(
				VegetationPlacements.TREES_GROVE, groveTrees
			)));
			ctx.register(WINDSWEPT_HILLS_FIR_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasWindsweptHillsTrees, Map.of(
				VegetationPlacements.TREES_WINDSWEPT_HILLS, windsweptHillsFirTrees
			)));
			ctx.register(PINE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasPineForestTrees, Map.of(
				VegetationPlacements.TREES_TAIGA, pineTrees,
				VegetationPlacements.TREES_OLD_GROWTH_SPRUCE_TAIGA, pineTrees
			)));
			ctx.register(SPRUCE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasSpruceForestTrees, Map.of(
				VegetationPlacements.TREES_TAIGA, spruceTrees
			)));
			ctx.register(SPRUCE_TUNDRA_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasSpruceTundraTrees, Map.of(
				VegetationPlacements.TREES_SNOWY, spruceTundraTrees,
				VegetationPlacements.TREES_WATER, spruceTundraTrees
			)));
			ctx.register(REDWOOD_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasRedwoodForestTrees, Map.of(
				VegetationPlacements.TREES_OLD_GROWTH_PINE_TAIGA, redwoodTrees
			)));
			ctx.register(JUNGLE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasJungleTrees, Map.of(
				VegetationPlacements.TREES_JUNGLE, jungleTrees,
				VegetationPlacements.BAMBOO_VEGETATION, jungleTrees
			)));
			ctx.register(JUNGLE_EDGE_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasJungleEdgeTrees, Map.of(
				VegetationPlacements.TREES_SPARSE_JUNGLE, jungleEdgeTrees
			)));
			ctx.register(BADLANDS_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasBadlandsTrees, Map.of(
				VegetationPlacements.TREES_BADLANDS, badlandsTrees,
				VegetationPlacements.TREES_WINDSWEPT_SAVANNA, badlandsTrees
			)));
			ctx.register(WOODED_BADLANDS_TREES, BiomeModifiers.replace(GenerationStep.Decoration.VEGETAL_DECORATION, hasWoodedBadlandsTrees, Map.of(
				VegetationPlacements.TREES_BADLANDS, woodedBadlandsTrees
			)));

			HolderSet<Biome> hasMarshBushes = biomes.getOrThrow(RTFBiomeTags.HAS_MARSH_BUSHES);
			HolderSet<Biome> hasPlainsBushes = biomes.getOrThrow(RTFBiomeTags.HAS_PLAINS_BUSHES);
			HolderSet<Biome> hasSteppeBushes = biomes.getOrThrow(RTFBiomeTags.HAS_STEPPE_BUSHES);
			HolderSet<Biome> hasColdSteppeBushes = biomes.getOrThrow(RTFBiomeTags.HAS_COLD_STEPPE_BUSHES);
			HolderSet<Biome> hasColdTaigaScrubBushes = biomes.getOrThrow(RTFBiomeTags.HAS_COLD_TAIGA_SCRUB_BUSHES);
			
			ctx.register(MARSH_BUSH, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, hasMarshBushes, placedFeatures.getOrThrow(PresetPlacedFeatures.MARSH_BUSH)));
			ctx.register(PLAINS_BUSH, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, hasPlainsBushes, placedFeatures.getOrThrow(PresetPlacedFeatures.PLAINS_BUSH)));
			ctx.register(STEPPE_BUSH, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, hasSteppeBushes, placedFeatures.getOrThrow(PresetPlacedFeatures.STEPPE_BUSH)));
			ctx.register(COLD_STEPPE_BUSH, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, hasColdSteppeBushes, placedFeatures.getOrThrow(PresetPlacedFeatures.COLD_STEPPE_BUSH)));
			ctx.register(ADD_TAIGA_SCRUB_BUSH, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, hasColdTaigaScrubBushes, placedFeatures.getOrThrow(PresetPlacedFeatures.TAIGA_SCRUB_BUSH)));
			
			HolderSet<Biome> hasForestGrass = biomes.getOrThrow(RTFBiomeTags.HAS_FOREST_GRASS);
			HolderSet<Biome> hasBirchForestGrass = biomes.getOrThrow(RTFBiomeTags.HAS_BIRCH_FOREST_GRASS);
			
			ctx.register(FOREST_GRASS, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, hasForestGrass, placedFeatures.getOrThrow(PresetPlacedFeatures.FOREST_GRASS)));
			ctx.register(BIRCH_FOREST_GRASS, prepend(GenerationStep.Decoration.VEGETAL_DECORATION, Filter.Behavior.WHITELIST, hasBirchForestGrass, placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_FOREST_GRASS)));
		}
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
