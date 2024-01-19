package raccoonman.reterraforged.data.worldgen.preset;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.features.TreeFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.TreePlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.SimpleStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.AlterGroundDecorator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.settings.Preset;
import raccoonman.reterraforged.data.worldgen.preset.settings.SurfaceSettings;
import raccoonman.reterraforged.world.worldgen.feature.BushFeature;
import raccoonman.reterraforged.world.worldgen.feature.ErodeFeature;
import raccoonman.reterraforged.world.worldgen.feature.ErodeSnowFeature;
import raccoonman.reterraforged.world.worldgen.feature.RTFFeatures;
import raccoonman.reterraforged.world.worldgen.feature.SwampSurfaceFeature;
import raccoonman.reterraforged.world.worldgen.feature.chance.ChanceFeature;
import raccoonman.reterraforged.world.worldgen.feature.chance.ChanceModifier;
import raccoonman.reterraforged.world.worldgen.feature.chance.RTFChanceModifiers;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateFeature;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.DecoratorConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorators;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TreeContext;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.PasteConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacement;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacements;
import raccoonman.reterraforged.world.worldgen.feature.template.template.TemplateContext;

public class PresetConfiguredFeatures {
	public static final ResourceKey<ConfiguredFeature<?, ?>> ERODE = createKey("erode");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ERODE_SNOW = createKey("erode_snow");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_SURFACE = createKey("swamp_surface");

	public static final ResourceKey<ConfiguredFeature<?, ?>> OAK_SMALL = createKey("oak/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OAK_FOREST = createKey("oak/forest");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OAK_LARGE = createKey("oak/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_SMALL = createKey("birch/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_FOREST = createKey("birch/forest");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_LARGE = createKey("birch/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ACACIA_BUSH = createKey("acacia/bush");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ACACIA_SMALL = createKey("acacia/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ACACIA_LARGE = createKey("acacia/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_OAK_SMALL = createKey("dark_oak/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_OAK_LARGE = createKey("dark_oak/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_BROWN_MUSHROOM = createKey("mushrooms/huge_brown_mushroom");
	public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_RED_MUSHROOM = createKey("mushrooms/huge_red_mushroom");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_SMALL = createKey("willow/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WILLOW_LARGE = createKey("willow/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> PINE = createKey("pine/pine");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SPRUCE_SMALL = createKey("spruce/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SPRUCE_LARGE = createKey("spruce/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SPRUCE_SMALL_ON_SNOW = createKey("spruce/small_on_snow");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SPRUCE_LARGE_ON_SNOW = createKey("spruce/large_on_snow");
	public static final ResourceKey<ConfiguredFeature<?, ?>> REDWOOD_LARGE = createKey("redwood/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> REDWOOD_HUGE = createKey("redwood/huge");
	public static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_SMALL = createKey("jungle/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_LARGE = createKey("jungle/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_HUGE = createKey("jungle/huge");

	public static final ResourceKey<ConfiguredFeature<?, ?>> MARSH_BUSH = createKey("shrubs/marsh_bush");
	public static final ResourceKey<ConfiguredFeature<?, ?>> PLAINS_BUSH = createKey("shrubs/plains_bush");
	public static final ResourceKey<ConfiguredFeature<?, ?>> STEPPE_BUSH = createKey("shrubs/steppe_bush");
	public static final ResourceKey<ConfiguredFeature<?, ?>> COLD_STEPPE_BUSH = createKey("shrubs/cold_steppe_bush");
	public static final ResourceKey<ConfiguredFeature<?, ?>> TAIGA_SCRUB_BUSH = createKey("shrubs/taiga_scrub_bush");

	public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_GRASS = createKey("forest_grass");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_FOREST_GRASS = createKey("birch_forest_grass");
	
	public static final ResourceKey<ConfiguredFeature<?, ?>> PLAINS_TREES = createKey("plains_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_TREES = createKey("forest_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FLOWER_FOREST_TREES = createKey("flower_forest_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_TREES = createKey("birch_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_FOREST_TREES = createKey("dark_forest_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SAVANNA_TREES = createKey("savanna_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BADLANDS_TREES = createKey("badlands_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> WOODED_BADLANDS_TREES = createKey("wooded_badlands_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_TREES = createKey("swamp_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FIR_TREES = createKey("fir_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> GROVE_TREES = createKey("grove_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SPRUCE_TREES = createKey("spruce_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> SPRUCE_TUNDRA_TREES = createKey("spruce_tundra_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> REDWOOD_TREES = createKey("redwood_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_TREES = createKey("jungle_trees");
	public static final ResourceKey<ConfiguredFeature<?, ?>> JUNGLE_EDGE_TREES = createKey("jungle_edge_trees");
	
	public static void bootstrap(Preset preset, BootstapContext<ConfiguredFeature<?, ?>> ctx) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		SurfaceSettings surface = preset.surface();
		SurfaceSettings.Erosion erosion = surface.erosion();
		
		ErodeFeature.Config erodeConfig = new ErodeFeature.Config(miscellaneous.rockTag(), erosion.rockVariance, erosion.rockMin, erosion.dirtVariance, erosion.dirtMin, erosion.rockSteepness, erosion.dirtSteepness, erosion.screeSteepness, erosion.heightModifier / 255F, erosion.slopeModifier / 255F, 256, 3F / 255F, 0.55F);
		if(miscellaneous.erosionDecorator) {
			FeatureUtils.register(ctx, ERODE, RTFFeatures.ERODE, erodeConfig);
		}
		
		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			FeatureUtils.register(ctx, ERODE_SNOW, RTFFeatures.ERODE_SNOW, new ErodeSnowFeature.Config(erosion.snowSteepness, (float) erosion.snowHeight / 255.0F, miscellaneous.naturalSnowDecorator, miscellaneous.smoothLayerDecorator, erodeConfig));
		}
		
		FeatureUtils.register(ctx, SWAMP_SURFACE, RTFFeatures.SWAMP_SURFACE, new SwampSurfaceFeature.Config(Blocks.CLAY.defaultBlockState(), Blocks.GRAVEL.defaultBlockState(), Blocks.DIRT.defaultBlockState()));
		
		if(miscellaneous.customBiomeFeatures) {
			HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
			Holder<PlacedFeature> oakSmall = placedFeatures.getOrThrow(PresetPlacedFeatures.OAK_SMALL);
			Holder<PlacedFeature> oakForest = placedFeatures.getOrThrow(PresetPlacedFeatures.OAK_FOREST);
			Holder<PlacedFeature> oakLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.OAK_LARGE);
			Holder<PlacedFeature> birchSmall = placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_SMALL);
			Holder<PlacedFeature> birchForest = placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_FOREST);
			Holder<PlacedFeature> birchLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.BIRCH_LARGE);
			Holder<PlacedFeature> acaciaBush = placedFeatures.getOrThrow(PresetPlacedFeatures.ACACIA_BUSH);
			Holder<PlacedFeature> acaciaSmall = placedFeatures.getOrThrow(PresetPlacedFeatures.ACACIA_SMALL);
			Holder<PlacedFeature> acaciaLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.ACACIA_LARGE);
			Holder<PlacedFeature> darkOakSmall = placedFeatures.getOrThrow(PresetPlacedFeatures.DARK_OAK_SMALL);
			Holder<PlacedFeature> darkOakLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.DARK_OAK_LARGE);
			Holder<PlacedFeature> hugeBrownMushroom = placedFeatures.getOrThrow(PresetPlacedFeatures.HUGE_BROWN_MUSHROOM);
			Holder<PlacedFeature> hugeRedMushroom = placedFeatures.getOrThrow(PresetPlacedFeatures.HUGE_RED_MUSHROOM);
			Holder<PlacedFeature> willowSmall = placedFeatures.getOrThrow(PresetPlacedFeatures.WILLOW_SMALL);
			Holder<PlacedFeature> willowLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.WILLOW_LARGE);
			Holder<PlacedFeature> spruceSmall = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_SMALL);
			Holder<PlacedFeature> spruceLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_LARGE);
			Holder<PlacedFeature> spruceSmallOnSnow = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_SMALL_ON_SNOW);
			Holder<PlacedFeature> spruceLargeOnSnow = placedFeatures.getOrThrow(PresetPlacedFeatures.SPRUCE_LARGE_ON_SNOW);
			Holder<PlacedFeature> redwoodLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.REDWOOD_LARGE);
			Holder<PlacedFeature> redwoodHuge = placedFeatures.getOrThrow(PresetPlacedFeatures.REDWOOD_HUGE);
			Holder<PlacedFeature> jungleSmall = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_SMALL);
			Holder<PlacedFeature> jungleLarge = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_LARGE);
			Holder<PlacedFeature> jungleHuge = placedFeatures.getOrThrow(PresetPlacedFeatures.JUNGLE_HUGE);
			Holder<PlacedFeature> jungleBush = placedFeatures.getOrThrow(TreePlacements.JUNGLE_BUSH);

			FeatureUtils.register(ctx, OAK_SMALL, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.OAK_SMALL));
			FeatureUtils.register(ctx, OAK_FOREST, RTFFeatures.TEMPLATE, makeTreeWithBehives(PresetTemplatePaths.OAK_FOREST, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005, ImmutableMap.of(
				Biomes.PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.SUNFLOWER_PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.FLOWER_FOREST, PresetTemplateDecoratorLists.BEEHIVE_RARITY_002_AND_005
			)));
			FeatureUtils.register(ctx, OAK_LARGE, RTFFeatures.TEMPLATE, makeTreeWithBehives(PresetTemplatePaths.OAK_LARGE, PresetTemplateDecoratorLists.BEEHIVE_RARITY_0002_AND_005, ImmutableMap.of(
				Biomes.PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.SUNFLOWER_PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.FLOWER_FOREST, PresetTemplateDecoratorLists.BEEHIVE_RARITY_002_AND_005
			)));
			FeatureUtils.register(ctx, BIRCH_SMALL, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.BIRCH_SMALL));
			FeatureUtils.register(ctx, BIRCH_FOREST, RTFFeatures.TEMPLATE, makeTreeWithBehives(PresetTemplatePaths.BIRCH_FOREST, PresetTemplateDecoratorLists.BEEHIVE_RARITY_0002_AND_005, ImmutableMap.of(
				Biomes.PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.SUNFLOWER_PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.FLOWER_FOREST, PresetTemplateDecoratorLists.BEEHIVE_RARITY_002_AND_005
			)));
			FeatureUtils.register(ctx, BIRCH_LARGE, RTFFeatures.TEMPLATE, makeTreeWithBehives(PresetTemplatePaths.BIRCH_LARGE, PresetTemplateDecoratorLists.BEEHIVE_RARITY_0002_AND_005, ImmutableMap.of(
				Biomes.PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.SUNFLOWER_PLAINS, PresetTemplateDecoratorLists.BEEHIVE_RARITY_005,
				Biomes.FLOWER_FOREST, PresetTemplateDecoratorLists.BEEHIVE_RARITY_002_AND_005
			)));
			FeatureUtils.register(ctx, ACACIA_BUSH, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.ACACIA_BUSH, 2));
			FeatureUtils.register(ctx, ACACIA_SMALL, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.ACACIA_SMALL));
			FeatureUtils.register(ctx, ACACIA_LARGE, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.ACACIA_LARGE));
			FeatureUtils.register(ctx, DARK_OAK_SMALL, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.DARK_OAK_SMALL));
			FeatureUtils.register(ctx, DARK_OAK_LARGE, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.DARK_OAK_LARGE));
			FeatureUtils.register(ctx, HUGE_BROWN_MUSHROOM, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.BROWN_MUSHROOM, 0));
			FeatureUtils.register(ctx, HUGE_RED_MUSHROOM, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.RED_MUSHROOM));
			FeatureUtils.register(ctx, WILLOW_SMALL, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.WILLOW_SMALL));
			FeatureUtils.register(ctx, WILLOW_LARGE, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.WILLOW_LARGE));
			TemplateFeature.Config<?> pineConfig = makeTree(PresetTemplatePaths.PINE);
			FeatureUtils.register(ctx, PINE, RTFFeatures.TEMPLATE, pineConfig);
			FeatureUtils.register(ctx, SPRUCE_SMALL, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.SPRUCE_SMALL));
			FeatureUtils.register(ctx, SPRUCE_LARGE, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.SPRUCE_LARGE));
			FeatureUtils.register(ctx, SPRUCE_SMALL_ON_SNOW, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.SPRUCE_SMALL, TemplatePlacements.any(), 3));
			FeatureUtils.register(ctx, SPRUCE_LARGE_ON_SNOW, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.SPRUCE_LARGE, TemplatePlacements.any(), 3));
			FeatureUtils.register(ctx, REDWOOD_LARGE, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.REDWOOD_LARGE));
			TemplateFeature.Config<?> redwoodHugeConfig = makeTree(PresetTemplatePaths.REDWOOD_HUGE, ImmutableList.of(TemplateDecorators.tree(new AlterGroundDecorator(SimpleStateProvider.simple(Blocks.PODZOL)))), TemplatePlacements.tree(), 3);
			FeatureUtils.register(ctx, REDWOOD_HUGE, RTFFeatures.TEMPLATE, redwoodHugeConfig);
			TemplateFeature.Config<?> jungleSmallConfig = makeTree(PresetTemplatePaths.JUNGLE_SMALL);
			FeatureUtils.register(ctx, JUNGLE_SMALL, RTFFeatures.TEMPLATE, jungleSmallConfig);
			FeatureUtils.register(ctx, JUNGLE_LARGE, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.JUNGLE_LARGE));
			FeatureUtils.register(ctx, JUNGLE_HUGE, RTFFeatures.TEMPLATE, makeTree(PresetTemplatePaths.JUNGLE_HUGE));
			
			FeatureUtils.register(ctx, MARSH_BUSH, RTFFeatures.BUSH, makeSmallBush(Blocks.OAK_LOG, Blocks.BIRCH_LEAVES, 0.05F, 0.09F, 0.65F));
			FeatureUtils.register(ctx, PLAINS_BUSH, RTFFeatures.BUSH, makeSmallBush(Blocks.OAK_LOG, Blocks.BIRCH_LEAVES, 0.05F, 0.09F, 0.65F));
			FeatureUtils.register(ctx, STEPPE_BUSH, RTFFeatures.BUSH, makeSmallBush(Blocks.ACACIA_LOG, Blocks.ACACIA_LEAVES, 0.06F, 0.08F, 0.7F));
			FeatureUtils.register(ctx, COLD_STEPPE_BUSH, RTFFeatures.BUSH, makeSmallBush(Blocks.SPRUCE_LOG, Blocks.OAK_LEAVES, 0.05F, 0.075F, 0.6F));
			FeatureUtils.register(ctx, TAIGA_SCRUB_BUSH, RTFFeatures.BUSH, makeSmallBush(Blocks.SPRUCE_LOG, Blocks.SPRUCE_LEAVES, 0.05F, 0.075F, 0.6F));
			
			FeatureUtils.register(ctx, PLAINS_TREES, Feature.RANDOM_SELECTOR, makeRandom(oakForest, List.of(
				makeWeighted(0.2F, oakForest), 
				makeWeighted(0.3F, oakLarge)
			)));
			FeatureUtils.register(ctx, FOREST_TREES, Feature.RANDOM_SELECTOR, makeRandom(oakForest, List.of(
				makeWeighted(0.2F, oakForest), 
				makeWeighted(0.3F, oakLarge)
			)));
			FeatureUtils.register(ctx, FLOWER_FOREST_TREES, Feature.RANDOM_SELECTOR, makeRandom(birchForest, List.of(
				makeWeighted(0.2F, birchForest), 
				makeWeighted(0.2F, birchLarge), 
				makeWeighted(0.2F, oakForest), 
				makeWeighted(0.2F, oakLarge)
			)));
			FeatureUtils.register(ctx, BIRCH_TREES, RTFFeatures.CHANCE, makeChance(
				makeChanceEntry(birchLarge, 0.2F, RTFChanceModifiers.elevation(0.25F, 0.0F), RTFChanceModifiers.biomeEdge(0.1F, 0.3F)),
				makeChanceEntry(birchForest, 0.2F, RTFChanceModifiers.elevation(0.3F, 0.0F), RTFChanceModifiers.biomeEdge(0.05F, 0.2F)),
				makeChanceEntry(birchSmall, 0.1F, RTFChanceModifiers.biomeEdge(0.25F, 0.0F)),
				makeChanceEntry(birchSmall, 0.1F, RTFChanceModifiers.elevation(0.25F, 0.65F))
			));
			FeatureUtils.register(ctx, DARK_FOREST_TREES, Feature.RANDOM_SELECTOR, makeRandom(darkOakLarge, List.of(
				makeWeighted(0.025F, hugeBrownMushroom),
				makeWeighted(0.05F, hugeRedMushroom),
				makeWeighted(0.3F, darkOakLarge),
				makeWeighted(0.2F, darkOakSmall),
				makeWeighted(0.05F, birchForest),
				makeWeighted(0.025F, oakForest)
			)));
			FeatureUtils.register(ctx, SAVANNA_TREES, Feature.RANDOM_SELECTOR, makeRandom(acaciaLarge, List.of(
				makeWeighted(0.4F, acaciaLarge), 
				makeWeighted(0.15F, acaciaSmall)
			)));
			FeatureUtils.register(ctx, BADLANDS_TREES, Feature.RANDOM_SELECTOR, makeRandom(oakSmall, List.of(
				makeWeighted(0.2F, oakSmall), 
				makeWeighted(0.1F, acaciaBush)
			)));
			FeatureUtils.register(ctx, WOODED_BADLANDS_TREES, Feature.RANDOM_SELECTOR, makeRandom(oakSmall, List.of(
				makeWeighted(0.3F, oakSmall), 
				makeWeighted(0.2F, acaciaBush)
			)));
			FeatureUtils.register(ctx, SWAMP_TREES, Feature.RANDOM_SELECTOR, makeRandom(willowLarge, List.of(
				makeWeighted(0.2F, willowSmall), 
				makeWeighted(0.35F, willowLarge)
			)));
			FeatureUtils.register(ctx, FIR_TREES, RTFFeatures.CHANCE, makeChance(
				makeChanceEntry(spruceSmall, 0.1F, RTFChanceModifiers.elevation(0.55F, 0.2F)),
				makeChanceEntry(spruceLarge, 0.25F, RTFChanceModifiers.elevation(0.3F, 0.0F))
			));
			FeatureUtils.register(ctx, GROVE_TREES, RTFFeatures.CHANCE, makeChance(
				makeChanceEntry(spruceSmallOnSnow, 0.1F, RTFChanceModifiers.elevation(1.0F, 0.2F)),
				makeChanceEntry(spruceLargeOnSnow, 0.25F, RTFChanceModifiers.elevation(0.3F, 0.0F))
			));
			
			FeatureUtils.register(ctx, REDWOOD_TREES, RTFFeatures.CHANCE, makeChance(
				makeChanceEntry(redwoodHuge, 0.4F, RTFChanceModifiers.elevation(0.15F, 0.0F), RTFChanceModifiers.biomeEdge(0.1F, 0.3F)),
				makeChanceEntry(redwoodLarge, 0.2F, RTFChanceModifiers.elevation(0.25F, 0.0F), RTFChanceModifiers.biomeEdge(0.05F, 0.25F)),
				makeChanceEntry(spruceLarge, 0.4F, RTFChanceModifiers.elevation(0.35F, 0.15F)),
				makeChanceEntry(spruceSmall, 0.2F, RTFChanceModifiers.elevation(0.5F, 0.2F))
			));
			FeatureUtils.register(ctx, JUNGLE_TREES, Feature.RANDOM_SELECTOR, makeRandom(jungleSmall, List.of(
				makeWeighted(0.2F, jungleSmall), 
				makeWeighted(0.3F, jungleLarge),
				makeWeighted(0.4F, jungleHuge),
				makeWeighted(0.5F, jungleBush)
			)));
			FeatureUtils.register(ctx, JUNGLE_EDGE_TREES, Feature.RANDOM_SELECTOR, makeRandom(jungleSmall, List.of(
				makeWeighted(0.2F, jungleSmall), 
				makeWeighted(0.3F, jungleLarge),
				makeWeighted(0.4F, jungleBush)
			)));
			FeatureUtils.register(ctx, FOREST_GRASS, Feature.RANDOM_SELECTOR, makeRandom(
				makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.GRASS, 48)),
				List.of(
					makeWeighted(0.5F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.GRASS, 56))),
					makeWeighted(0.4F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.TALL_GRASS, 56))),
					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.LARGE_FERN, 48))),
					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.FERN, 24)))
				)
			));
			FeatureUtils.register(ctx, BIRCH_FOREST_GRASS, Feature.RANDOM_SELECTOR, makeRandom(
				makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.TALL_GRASS, 48)),
				List.of(
					makeWeighted(0.8F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.TALL_GRASS, 56))),
					makeWeighted(0.5F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.LILAC, 64))),
					makeWeighted(0.3F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.LARGE_FERN, 48))),
					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.FERN, 24))),
					makeWeighted(0.1F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.PEONY, 32)))
				)
			));

	        FeatureUtils.register(ctx, MiscOverworldFeatures.DISK_CLAY, RTFFeatures.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.CLAY), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformInt.of(2, 3), 1));
	        FeatureUtils.register(ctx, MiscOverworldFeatures.DISK_GRAVEL, RTFFeatures.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 5), 2));
	        FeatureUtils.register(ctx, MiscOverworldFeatures.DISK_SAND, RTFFeatures.DISK, new DiskConfiguration(new RuleBasedBlockStateProvider(BlockStateProvider.simple(Blocks.SAND), List.of(new RuleBasedBlockStateProvider.Rule(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.AIR), BlockStateProvider.simple(Blocks.SANDSTONE)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 6), 2));

	        FeatureUtils.register(ctx, TreeFeatures.ACACIA, Feature.RANDOM_SELECTOR, makeRandom(acaciaSmall, List.of(
	        	makeWeighted(1.0F, acaciaSmall), 
	        	makeWeighted(0.5F, acaciaLarge)
			)));
	        RandomFeatureConfiguration birchConfig = makeRandom(birchSmall, List.of(
	        	makeWeighted(1.0F, birchSmall), 
	        	makeWeighted(0.5F, birchLarge)
			));
	        FeatureUtils.register(ctx, TreeFeatures.BIRCH, Feature.RANDOM_SELECTOR, birchConfig);
	        FeatureUtils.register(ctx, TreeFeatures.BIRCH_BEES_005, Feature.RANDOM_SELECTOR, birchConfig);
	        FeatureUtils.register(ctx, TreeFeatures.DARK_OAK, Feature.RANDOM_SELECTOR, makeRandom(darkOakSmall, List.of(
	        	makeWeighted(1.0F, darkOakSmall), 
	        	makeWeighted(0.5F, darkOakLarge)
			)));
	        FeatureUtils.register(ctx, TreeFeatures.JUNGLE_TREE_NO_VINE, RTFFeatures.TEMPLATE, jungleSmallConfig);
	        FeatureUtils.register(ctx, TreeFeatures.MEGA_JUNGLE_TREE, Feature.RANDOM_SELECTOR, makeRandom(jungleLarge, List.of(
	        	makeWeighted(1.0F, jungleLarge), 
	        	makeWeighted(0.5F, jungleHuge)
			)));
	        RandomFeatureConfiguration oakConfig = makeRandom(oakSmall, List.of(
	        	makeWeighted(1.0F, oakSmall), 
	        	makeWeighted(0.5F, oakLarge)
			));
	        FeatureUtils.register(ctx, TreeFeatures.OAK, Feature.RANDOM_SELECTOR, oakConfig);
	        FeatureUtils.register(ctx, TreeFeatures.OAK_BEES_005, Feature.RANDOM_SELECTOR, oakConfig);
	        FeatureUtils.register(ctx, TreeFeatures.FANCY_OAK_BEES_005, Feature.RANDOM_SELECTOR, oakConfig);
	        FeatureUtils.register(ctx, TreeFeatures.FANCY_OAK, Feature.RANDOM_SELECTOR, oakConfig);
	        FeatureUtils.register(ctx, TreeFeatures.SPRUCE, Feature.RANDOM_SELECTOR, makeRandom(spruceSmall, List.of(
	        	makeWeighted(1.0F, spruceSmall), 
	        	makeWeighted(0.5F, spruceLarge)
			)));
	        FeatureUtils.register(ctx, TreeFeatures.PINE, RTFFeatures.TEMPLATE, pineConfig);
	        FeatureUtils.register(ctx, TreeFeatures.MEGA_PINE, RTFFeatures.TEMPLATE, redwoodHugeConfig);
	        FeatureUtils.register(ctx, TreeFeatures.MEGA_SPRUCE, RTFFeatures.TEMPLATE, redwoodHugeConfig);
	    }
	}
	
	private static BushFeature.Config makeSmallBush(Block log, Block leaves, float air, float leaf, float size) {
		return new BushFeature.Config(log.defaultBlockState(), leaves.defaultBlockState(), air, leaf, size);
	}

	private static TemplateFeature.Config<?> makeTree(List<ResourceLocation> templates) {
		return makeTree(templates, 3);
	}
		
	private static TemplateFeature.Config<?> makeTree(List<ResourceLocation> templates, int baseExtension) {
		return makeTree(templates, TemplatePlacements.tree(), baseExtension);
	}
	
	private static TemplateFeature.Config<?> makeTree(List<ResourceLocation> templates, TemplatePlacement<?> placement, int baseExtension) {
		return makeTree(templates, ImmutableList.of(), placement, baseExtension);
	}
	
	private static <T extends TemplateContext> TemplateFeature.Config<T> makeTree(List<ResourceLocation> templates, List<TemplateDecorator<T>> decorators, TemplatePlacement<T> placement, int baseExtension) {
		return new TemplateFeature.Config<>(templates, placement, new PasteConfig(baseExtension, false, true, false, false), new DecoratorConfig<>(decorators, ImmutableMap.of()));
	}
	
	private static TemplateFeature.Config<TreeContext> makeTreeWithBehives(List<ResourceLocation> templates, List<TemplateDecorator<TreeContext>> behives, Map<ResourceKey<Biome>, List<TemplateDecorator<TreeContext>>> biomeOverrides) {
		return new TemplateFeature.Config<>(templates, TemplatePlacements.tree(), new PasteConfig(3, false, true, false, false), new DecoratorConfig<>(behives, biomeOverrides));
	}
	
	private static ChanceFeature.Config makeChance(ChanceFeature.Entry... entries) {
		return new ChanceFeature.Config(ImmutableList.copyOf(entries));
	}

	private static ChanceFeature.Entry makeChanceEntry(Holder<PlacedFeature> feature, float chance, ChanceModifier... modifiers) {
		return new ChanceFeature.Entry(feature, chance, ImmutableList.copyOf(modifiers));
	}
	
	private static RandomFeatureConfiguration makeRandom(Holder<PlacedFeature> defaultFeature, List<WeightedPlacedFeature> entries) {
		return new RandomFeatureConfiguration(entries, defaultFeature);
	}

    private static WeightedPlacedFeature makeWeighted(float weight, Holder<PlacedFeature> feature) {
    	return new WeightedPlacedFeature(feature, weight);
    }
    
    private static RandomPatchConfiguration makePatch(Block state, int tries) {
        return FeatureUtils.simpleRandomPatchConfiguration(tries, PlacementUtils.onlyWhenEmpty(Feature.SIMPLE_BLOCK, new SimpleBlockConfiguration(BlockStateProvider.simple(state.defaultBlockState()))));
    }
    
	private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<PlacedFeature> makeInlined(F feature, FC featureConfiguration) {
		return Holder.direct(new PlacedFeature(Holder.direct(new ConfiguredFeature<>(feature, featureConfiguration)), ImmutableList.of()));
	}

	protected static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, RTFCommon.location(name));
	}
}
