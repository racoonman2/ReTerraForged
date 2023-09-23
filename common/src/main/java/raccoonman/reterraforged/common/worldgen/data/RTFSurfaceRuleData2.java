package raccoonman.reterraforged.common.worldgen.data;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.geology.Strata;
import raccoonman.reterraforged.common.level.levelgen.geology.Stratum;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Rand;
import raccoonman.reterraforged.common.level.levelgen.rule.GeoRuleSource;
import raccoonman.reterraforged.common.level.levelgen.surface.condition.DensityThresholdCondition;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.tags.RTFBlockTags;

public final class RTFSurfaceRuleData2 {
    private static final SurfaceRules.RuleSource AIR = RTFSurfaceRuleData2.makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = RTFSurfaceRuleData2.makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = RTFSurfaceRuleData2.makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = RTFSurfaceRuleData2.makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.RuleSource TERRACOTTA = RTFSurfaceRuleData2.makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_SAND = RTFSurfaceRuleData2.makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource RED_SANDSTONE = RTFSurfaceRuleData2.makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource STONE = RTFSurfaceRuleData2.makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource DEEPSLATE = RTFSurfaceRuleData2.makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.RuleSource DIRT = RTFSurfaceRuleData2.makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource PODZOL = RTFSurfaceRuleData2.makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.RuleSource COARSE_DIRT = RTFSurfaceRuleData2.makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.RuleSource MYCELIUM = RTFSurfaceRuleData2.makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = RTFSurfaceRuleData2.makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CALCITE = RTFSurfaceRuleData2.makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.RuleSource GRAVEL = RTFSurfaceRuleData2.makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SAND = RTFSurfaceRuleData2.makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = RTFSurfaceRuleData2.makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource PACKED_ICE = RTFSurfaceRuleData2.makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = RTFSurfaceRuleData2.makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource MUD = RTFSurfaceRuleData2.makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource POWDER_SNOW = RTFSurfaceRuleData2.makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.RuleSource ICE = RTFSurfaceRuleData2.makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = RTFSurfaceRuleData2.makeStateRule(Blocks.WATER);

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    //FIXME frozen peaks dont apply!
    public static SurfaceRules.RuleSource overworld(HolderGetter<DensityFunction> densityFunctions, Preset preset) {
    	MiscellaneousSettings misc = preset.miscellaneous();
    	TerrainSettings terrain = preset.terrain();
    	
    	Holder<DensityFunction> height = densityFunctions.getOrThrow(RTFNoiseRouterData.HEIGHT);
    	Holder<DensityFunction> slope = densityFunctions.getOrThrow(RTFNoiseRouterData.SLOPE);
    	
    	//TODO forest surface generation
    	//TODO plain stone erosion

    	SurfaceRules.RuleSource geo = makeGeoRule(misc.strataRegionSize, terrain.general.yScale);
    	
        SurfaceRules.ConditionSource conditionSource = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        SurfaceRules.ConditionSource conditionSource2 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
        SurfaceRules.ConditionSource conditionSource3 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
        SurfaceRules.ConditionSource conditionSource4 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
        SurfaceRules.ConditionSource conditionSource5 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
        SurfaceRules.ConditionSource conditionSource6 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
        SurfaceRules.ConditionSource conditionSource7 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
        SurfaceRules.ConditionSource conditionSource8 = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource conditionSource9 = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.ConditionSource conditionSource10 = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.ConditionSource conditionSource11 = SurfaceRules.hole();
        SurfaceRules.ConditionSource conditionSource12 = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        SurfaceRules.ConditionSource conditionSource13 = SurfaceRules.steep();
        SurfaceRules.RuleSource ruleSource = SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource9, GRASS_BLOCK), DIRT);
        SurfaceRules.RuleSource ruleSource2 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
        SurfaceRules.RuleSource ruleSource3 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
        SurfaceRules.ConditionSource conditionSource14 = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        SurfaceRules.ConditionSource conditionSource15 = SurfaceRules.isBiome(Biomes.DESERT);
        SurfaceRules.RuleSource ruleSource4 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_SHORE), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05, 0.05), ruleSource3), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(1.0), STONE)), SurfaceRules.ifTrue(conditionSource14, ruleSource2), SurfaceRules.ifTrue(conditionSource15, ruleSource2), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), STONE));
        SurfaceRules.RuleSource ruleSource5 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), SurfaceRules.ifTrue(conditionSource9, POWDER_SNOW));
        SurfaceRules.RuleSource ruleSource6 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), SurfaceRules.ifTrue(conditionSource9, POWDER_SNOW));
        SurfaceRules.RuleSource ruleSource7 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource13, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625, 0.025), ICE), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource13, STONE), ruleSource5, SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(ruleSource5, DIRT)), ruleSource4, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(1.75), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(2.0), ruleSource3), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(1.0), STONE), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(-1.0), DIRT), ruleSource3)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD), DIRT);
        SurfaceRules.RuleSource ruleSource8 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource13, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0, 0.025), ICE), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource13, STONE), ruleSource6, SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource13, STONE), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(ruleSource6, SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), ruleSource4, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(1.75), STONE), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(-0.5), COARSE_DIRT))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(2.0), ruleSource3), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(1.0), STONE), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(-1.0), ruleSource), ruleSource3)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(1.75), COARSE_DIRT), SurfaceRules.ifTrue(RTFSurfaceRuleData2.surfaceNoiseAbove(-0.95), PODZOL))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM), ruleSource);
        SurfaceRules.ConditionSource conditionSource16 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
        SurfaceRules.ConditionSource conditionSource17 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
        SurfaceRules.ConditionSource conditionSource18 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
        SurfaceRules.RuleSource ruleSource9 = SurfaceRules.ifTrue(SurfaceRules.not(isErodedRock(slope)), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), SurfaceRules.ifTrue(conditionSource, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource16, COARSE_DIRT), SurfaceRules.ifTrue(conditionSource17, COARSE_DIRT), SurfaceRules.ifTrue(conditionSource18, COARSE_DIRT), ruleSource))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SWAMP), SurfaceRules.ifTrue(conditionSource6, SurfaceRules.ifTrue(SurfaceRules.not(conditionSource7), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), SurfaceRules.ifTrue(conditionSource5, SurfaceRules.ifTrue(SurfaceRules.not(conditionSource7), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource2, ORANGE_TERRACOTTA), SurfaceRules.ifTrue(conditionSource4, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource16, TERRACOTTA), SurfaceRules.ifTrue(conditionSource17, TERRACOTTA), SurfaceRules.ifTrue(conditionSource18, TERRACOTTA), SurfaceRules.bandlands())), SurfaceRules.ifTrue(conditionSource8, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)), SurfaceRules.ifTrue(SurfaceRules.not(conditionSource11), ORANGE_TERRACOTTA), SurfaceRules.ifTrue(conditionSource10, WHITE_TERRACOTTA), ruleSource3)), SurfaceRules.ifTrue(conditionSource3, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource7, SurfaceRules.ifTrue(SurfaceRules.not(conditionSource4), ORANGE_TERRACOTTA)), SurfaceRules.bandlands())), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(conditionSource10, WHITE_TERRACOTTA)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(conditionSource8, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource12, SurfaceRules.ifTrue(conditionSource11, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource9, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER))), ruleSource8))), SurfaceRules.ifTrue(conditionSource10, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(conditionSource12, SurfaceRules.ifTrue(conditionSource11, WATER))), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, ruleSource7), SurfaceRules.ifTrue(conditionSource14, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE)), SurfaceRules.ifTrue(conditionSource15, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SANDSTONE)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), ruleSource2), ruleSource3))));
        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
        SurfaceRules.RuleSource ruleSource10 = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), ruleSource9);
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
//        builder.add(SurfaceRules.ifTrue(SurfaceRules.yBlockCheck(VerticalAnchor.absolute(0), 0), SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.abovePreliminarySurface()), geo)));
        builder.add(ruleSource10);
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
        return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }
    
    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double d) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, d / 8.25, Double.MAX_VALUE);
    }

    //FIXME we only apply this to rock
    //		idr but i dont think thats how 1.16 does it
    private static GeoRuleSource makeGeoRule(int regionSize, int yScale) {
    	List<Strata> strata = new LinkedList<>();
    	for(int i = 0; i < 100; i++) {
    		Noise noise = Source.perlin(354215 + i, regionSize, 3);
        	strata.add(new Strata(ReTerraForged.resolve("strata-" + i), RTFBlockTags.ROCK, ImmutableList.of(
        		new Stratum(RTFBlockTags.SOIL, 0, 1, 0.1F, 0.25F, noise),
        		new Stratum(RTFBlockTags.SEDIMENT, 0, 2, 0.05F, 0.15F, noise),
        		new Stratum(RTFBlockTags.CLAY, 0, 2, 0.05F, 0.1F, noise),
        		new Stratum(RTFBlockTags.ROCK, 10, 30, 0.1F, 1.5F, noise)
        	)));
    	}
    	return new GeoRuleSource(strata, Holder.direct(new NoiseWrapper.Marker(Source.cell(21345, regionSize).warp(213415, regionSize / 4, 2, regionSize / 2D).warp(421351, 15, 2, 30))));
    }

    private static final float SLOPE_MODIFIER = 3F / 255F;
    //TODO don't apply this is we're in a wetland or river
    private static SurfaceRules.RuleSource createErosionRule(Holder<DensityFunction> height, Holder<DensityFunction> slope) {
    	return SurfaceRules.ifTrue(
    		SurfaceRules.ON_FLOOR,
    		SurfaceRules.ifTrue(
    			SurfaceRules.not(isErodedRock(slope)),
    			SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState())
        	) 
    	);
    }
    
    private static SurfaceRules.ConditionSource isErodedRock(Holder<DensityFunction> slope) {
    	double rockSteepness = 0.65F;
    	return new DensityThresholdCondition(Holder.direct(DensityFunctions.cache2d(DensityFunctions.add(new DensityFunctions.HolderHolder(slope), new NoiseWrapper.Marker(new Rand(1.0F).mul(Source.constant(SLOPE_MODIFIER)))))), rockSteepness);
    }
}

