package raccoonman.reterraforged.data.worldgen;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.features.MiscOverworldFeatures;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
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
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.featuresize.TwoLayersFeatureSize;
import net.minecraft.world.level.levelgen.feature.foliageplacers.BushFoliagePlacer;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.trunkplacers.StraightTrunkPlacer;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.worldgen.preset.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.world.worldgen.feature.BushFeature;
import raccoonman.reterraforged.world.worldgen.feature.DecorateSnowFeature;
import raccoonman.reterraforged.world.worldgen.feature.ErodeFeature;
import raccoonman.reterraforged.world.worldgen.feature.RTFFeatures;
import raccoonman.reterraforged.world.worldgen.feature.chance.ChanceFeature;
import raccoonman.reterraforged.world.worldgen.feature.chance.ChanceModifier;
import raccoonman.reterraforged.world.worldgen.feature.template.TemplateFeature;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.DecoratorConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TemplateDecorator;
import raccoonman.reterraforged.world.worldgen.feature.template.decorator.TreeContext;
import raccoonman.reterraforged.world.worldgen.feature.template.paste.PasteConfig;
import raccoonman.reterraforged.world.worldgen.feature.template.placement.TemplatePlacements;

public class RTFConfiguredFeatures {
	public static final ResourceKey<ConfiguredFeature<?, ?>> ERODE = createKey("processing/erode");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DECORATE_SNOW = createKey("processing/decorate_snow");
	
	public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_GRASS = createKey("forest/grass");
	public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_BUSH = createKey("forest/bush");
	
	public static final ResourceKey<ConfiguredFeature<?, ?>> OAK_FOREST = createKey("oak/forest");
	public static final ResourceKey<ConfiguredFeature<?, ?>> OAK_LARGE = createKey("oak/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_GRASS = createKey("birch/grass");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_BUSH = createKey("birch/bush");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_SMALL = createKey("birch/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_FOREST = createKey("birch/forest");
	public static final ResourceKey<ConfiguredFeature<?, ?>> BIRCH_LARGE = createKey("birch/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ACACIA_BUSH = createKey("acacia/bush");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ACACIA_SMALL = createKey("acacia/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> ACACIA_LARGE = createKey("acacia/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_OAK_LARGE = createKey("dark_oak/large");
	public static final ResourceKey<ConfiguredFeature<?, ?>> DARK_OAK_SMALL = createKey("dark_oak/small");
	public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_BROWN_MUSHROOM = createKey("mushrooms/huge_brown_mushroom");
	public static final ResourceKey<ConfiguredFeature<?, ?>> HUGE_RED_MUSHROOM = createKey("mushrooms/huge_red_mushroom");
	
	public static void bootstrap(Preset preset, BootstapContext<ConfiguredFeature<?, ?>> ctx) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
		if(miscellaneous.erosionDecorator) {
			FeatureUtils.register(ctx, ERODE, RTFFeatures.ERODE, new ErodeFeature.Config());
		}
		
		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			FeatureUtils.register(ctx, DECORATE_SNOW, RTFFeatures.DECORATE_SNOW, new DecorateSnowFeature.Config(miscellaneous.naturalSnowDecorator, miscellaneous.smoothLayerDecorator));
		}
		
		if(miscellaneous.customBiomeFeatures) {
			HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
//			Holder<PlacedFeature> oakForest = placedFeatures.getOrThrow(RTFPlacedFeatures.OAK_FOREST);
//			Holder<PlacedFeature> oakLarge = placedFeatures.getOrThrow(RTFPlacedFeatures.OAK_LARGE);
//			Holder<PlacedFeature> birchSmall = placedFeatures.getOrThrow(RTFPlacedFeatures.BIRCH_SMALL);
//			Holder<PlacedFeature> birchForest = placedFeatures.getOrThrow(RTFPlacedFeatures.BIRCH_FOREST);
//			Holder<PlacedFeature> birchLarge = placedFeatures.getOrThrow(RTFPlacedFeatures.BIRCH_LARGE);
//			Holder<PlacedFeature> acaciaSmall = placedFeatures.getOrThrow(RTFPlacedFeatures.ACACIA_SMALL);
//			Holder<PlacedFeature> acaciaLarge = placedFeatures.getOrThrow(RTFPlacedFeatures.ACACIA_LARGE);
//			Holder<PlacedFeature> darkOakSmall = placedFeatures.getOrThrow(RTFPlacedFeatures.DARK_OAK_SMALL);
//			Holder<PlacedFeature> darkOakLarge = placedFeatures.getOrThrow(RTFPlacedFeatures.DARK_OAK_LARGE);
//			Holder<PlacedFeature> hugeBrownMushroom = placedFeatures.getOrThrow(RTFPlacedFeatures.HUGE_BROWN_MUSHROOM);
//			Holder<PlacedFeature> hugeRedMushroom = placedFeatures.getOrThrow(RTFPlacedFeatures.HUGE_RED_MUSHROOM);

			FeatureUtils.register(ctx, FOREST_GRASS, Feature.RANDOM_SELECTOR, makeRandom(
				makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.GRASS, 48)),
				List.of(
					makeWeighted(0.5F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.GRASS, 56))),
					makeWeighted(0.4F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.TALL_GRASS, 56))),
					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.LARGE_FERN, 48))),
					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.FERN, 24)))
				)
			));
//			FeatureUtils.register(ctx, FOREST_BUSH, Feature.TREE, makeLargeBush(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES));
//	
//			FeatureUtils.register(ctx, OAK_FOREST, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.OAK_FOREST));
//			FeatureUtils.register(ctx, OAK_LARGE, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.OAK_LARGE));
//
//			FeatureUtils.register(ctx, VegetationFeatures.TREES_PLAINS, Feature.RANDOM_SELECTOR, makeRandom(oakForest, List.of(
//				makeWeighted(0.2F, oakForest), 
//				makeWeighted(0.3F, oakLarge)
//			)));
//			
//			FeatureUtils.register(ctx, BIRCH_GRASS, Feature.RANDOM_SELECTOR, makeRandom(
//				makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.TALL_GRASS, 48)),
//				List.of(
//					makeWeighted(0.8F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.TALL_GRASS, 56))),
//					makeWeighted(0.5F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.LILAC, 64))),
//					makeWeighted(0.3F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.LARGE_FERN, 48))),
//					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.FERN, 24))),
//					makeWeighted(0.1F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.PEONY, 32)))
//				)
//			));
//			FeatureUtils.register(ctx, BIRCH_BUSH, Feature.TREE, makeLargeBush(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES));
//			FeatureUtils.register(ctx, BIRCH_SMALL, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.BIRCH_SMALL));
//			FeatureUtils.register(ctx, BIRCH_FOREST, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.BIRCH_FOREST));
//			FeatureUtils.register(ctx, BIRCH_LARGE, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.BIRCH_LARGE));
//	        FeatureUtils.register(ctx, TreeFeatures.BIRCH_BEES_0002, RTFFeatures.CHANCE, makeChance(
//	        	makeChanceEntry(birchLarge, 0.2F, RTFChanceModifiers.elevation(0.25F, 0.0F), RTFChanceModifiers.biomeEdge(0.1F, 0.3F)),
//	        	makeChanceEntry(birchForest, 0.2F, RTFChanceModifiers.elevation(0.3F, 0.0F), RTFChanceModifiers.biomeEdge(0.05F, 0.2F)),
//	        	makeChanceEntry(birchSmall, 0.1F, RTFChanceModifiers.biomeEdge(0.25F, 0.0F)),
//	        	makeChanceEntry(birchSmall, 0.1F, RTFChanceModifiers.elevation(0.25F, 0.65F))
//	        ));
//			
//			FeatureUtils.register(ctx, ACACIA_BUSH, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.ACACIA_BUSH));
//			FeatureUtils.register(ctx, ACACIA_SMALL, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.ACACIA_SMALL));
//			FeatureUtils.register(ctx, ACACIA_LARGE, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.ACACIA_LARGE));
//			FeatureUtils.register(ctx, VegetationFeatures.TREES_SAVANNA, Feature.RANDOM_SELECTOR, makeRandom(acaciaLarge, List.of(
//				makeWeighted(0.4F, acaciaLarge), 
//				makeWeighted(0.15F, acaciaSmall)
//			)));
//			
//			FeatureUtils.register(ctx, DARK_OAK_SMALL, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.DARK_OAK_SMALL));
//			FeatureUtils.register(ctx, DARK_OAK_LARGE, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.DARK_OAK_LARGE));
//			FeatureUtils.register(ctx, HUGE_BROWN_MUSHROOM, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.BROWN_MUSHROOM, 0));
//			FeatureUtils.register(ctx, HUGE_RED_MUSHROOM, RTFFeatures.TEMPLATE, makeTree(TemplatePaths.RED_MUSHROOM));
//			FeatureUtils.register(ctx, VegetationFeatures.DARK_FOREST_VEGETATION, Feature.RANDOM_SELECTOR, makeRandom(darkOakLarge, List.of(
//				makeWeighted(0.025F, hugeBrownMushroom),
//				makeWeighted(0.05F, hugeRedMushroom),
//				makeWeighted(0.3F, darkOakLarge),
//				makeWeighted(0.2F, darkOakSmall),
//				makeWeighted(0.05F, birchForest),
//				makeWeighted(0.025F, oakForest)
//			)));
//	        
	        FeatureUtils.register(ctx, MiscOverworldFeatures.DISK_CLAY, RTFFeatures.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.CLAY), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformInt.of(2, 3), 1));
	        FeatureUtils.register(ctx, MiscOverworldFeatures.DISK_GRAVEL, RTFFeatures.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 5), 2));
	        FeatureUtils.register(ctx, MiscOverworldFeatures.DISK_SAND, RTFFeatures.DISK, new DiskConfiguration(new RuleBasedBlockStateProvider(BlockStateProvider.simple(Blocks.SAND), List.of(new RuleBasedBlockStateProvider.Rule(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.AIR), BlockStateProvider.simple(Blocks.SANDSTONE)))), BlockPredicate.matchesBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformInt.of(2, 6), 2));
	    }
	}

	private static BushFeature.Config makeSmallBush(Block log, Block leaves, float air, float leaf, float size) {
		return new BushFeature.Config(log.defaultBlockState(), leaves.defaultBlockState(), air, leaf, size);
	}
	
	private static TreeConfiguration makeLargeBush(Block log, Block leaf) {
		return new TreeConfiguration.TreeConfigurationBuilder(BlockStateProvider.simple(log), new StraightTrunkPlacer(1, 0, 0), BlockStateProvider.simple(leaf), new BushFoliagePlacer(ConstantInt.of(2), ConstantInt.of(1), 2), new TwoLayersFeatureSize(0, 0, 0)).build();
	}
	
	private static TemplateFeature.Config<?> makeTree(List<ResourceLocation> templates) {
		return makeTree(templates, 3);
	}

	private static TemplateFeature.Config<?> makeTree(List<ResourceLocation> templates, int baseExtension) {
		return makeTree(templates, ImmutableList.of(), baseExtension);
	}
	
	private static TemplateFeature.Config<?> makeTree(List<ResourceLocation> templates, List<TemplateDecorator<TreeContext>> decorators, int baseExtension) {
		return new TemplateFeature.Config<>(templates, TemplatePlacements.tree(), new PasteConfig(baseExtension, false, true, false, false), new DecoratorConfig<>(decorators, ImmutableMap.of()));
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
