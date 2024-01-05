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
import raccoonman.reterraforged.world.worldgen.feature.SwampSurfaceFeature;
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
	public static final ResourceKey<ConfiguredFeature<?, ?>> SWAMP_SURFACE = createKey("processing/swamp_surface");

	public static final ResourceKey<ConfiguredFeature<?, ?>> ACACIA_TREES = createKey("acacia_trees");

	public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_GRASS = createKey("forest_grass");
		
	public static void bootstrap(Preset preset, BootstapContext<ConfiguredFeature<?, ?>> ctx) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		
		if(miscellaneous.erosionDecorator) {
			FeatureUtils.register(ctx, ERODE, RTFFeatures.ERODE, new ErodeFeature.Config());
		}
		
		if(miscellaneous.naturalSnowDecorator || miscellaneous.smoothLayerDecorator) {
			FeatureUtils.register(ctx, DECORATE_SNOW, RTFFeatures.DECORATE_SNOW, new DecorateSnowFeature.Config(miscellaneous.naturalSnowDecorator, miscellaneous.smoothLayerDecorator));
		}

		FeatureUtils.register(ctx, SWAMP_SURFACE, RTFFeatures.SWAMP_SURFACE, new SwampSurfaceFeature.Config(Blocks.CLAY.defaultBlockState(), Blocks.GRAVEL.defaultBlockState(), Blocks.DIRT.defaultBlockState()));
		
		if(miscellaneous.customBiomeFeatures) {
			HolderGetter<PlacedFeature> placedFeatures = ctx.lookup(Registries.PLACED_FEATURE);
			
			FeatureUtils.register(ctx, FOREST_GRASS, Feature.RANDOM_SELECTOR, makeRandom(
				makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.GRASS, 48)),
				List.of(
					makeWeighted(0.5F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.GRASS, 56))),
					makeWeighted(0.4F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.TALL_GRASS, 56))),
					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.LARGE_FERN, 48))),
					makeWeighted(0.2F, makeInlined(Feature.RANDOM_PATCH, makePatch(Blocks.FERN, 24)))
				)
			));
			
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
