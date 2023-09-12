package raccoonman.reterraforged.common.data;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;

public class RTFSurfaceRuleData {
    private static final SurfaceRules.RuleSource AIR = RTFSurfaceRuleData.makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = RTFSurfaceRuleData.makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = RTFSurfaceRuleData.makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = RTFSurfaceRuleData.makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.RuleSource TERRACOTTA = RTFSurfaceRuleData.makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_SAND = RTFSurfaceRuleData.makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource RED_SANDSTONE = RTFSurfaceRuleData.makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource STONE = RTFSurfaceRuleData.makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource DEEPSLATE = RTFSurfaceRuleData.makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.RuleSource DIRT = RTFSurfaceRuleData.makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource PODZOL = RTFSurfaceRuleData.makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.RuleSource COARSE_DIRT = RTFSurfaceRuleData.makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.RuleSource MYCELIUM = RTFSurfaceRuleData.makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = RTFSurfaceRuleData.makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CALCITE = RTFSurfaceRuleData.makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.RuleSource GRAVEL = RTFSurfaceRuleData.makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SAND = RTFSurfaceRuleData.makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = RTFSurfaceRuleData.makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource PACKED_ICE = RTFSurfaceRuleData.makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = RTFSurfaceRuleData.makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource MUD = RTFSurfaceRuleData.makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource POWDER_SNOW = RTFSurfaceRuleData.makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.RuleSource ICE = RTFSurfaceRuleData.makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = RTFSurfaceRuleData.makeStateRule(Blocks.WATER);

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static SurfaceRules.RuleSource overworld() {
        return RTFSurfaceRuleData.overworldLike(true, false, true);
    }

    public static SurfaceRules.RuleSource overworldLike(boolean bl, boolean bl2, boolean bl3) {
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
        SurfaceRules.ConditionSource hole = SurfaceRules.hole();
        SurfaceRules.ConditionSource isFrozenOcean = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        SurfaceRules.ConditionSource steep = SurfaceRules.steep();
        SurfaceRules.RuleSource ceilingDirt = SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource9, GRASS_BLOCK), DIRT);
        SurfaceRules.RuleSource ceilingSand = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
        SurfaceRules.RuleSource ceilingGravel = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
        SurfaceRules.ConditionSource isBeachOrWarm = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        SurfaceRules.ConditionSource isDesert = SurfaceRules.isBiome(Biomes.DESERT);
        SurfaceRules.RuleSource ruleSource4 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125, 0.0125), CALCITE), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.STONY_SHORE), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05, 0.05), ceilingGravel), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(1.0), STONE)), SurfaceRules.ifTrue(isBeachOrWarm, ceilingSand), SurfaceRules.ifTrue(isDesert, ceilingSand), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), STONE));
        SurfaceRules.RuleSource ruleSource5 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), SurfaceRules.ifTrue(conditionSource9, POWDER_SNOW));
        SurfaceRules.RuleSource ruleSource6 = SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), SurfaceRules.ifTrue(conditionSource9, POWDER_SNOW));
        SurfaceRules.RuleSource ruleSource7 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, -0.0625, 0.025), ICE), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), ruleSource5, SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(ruleSource5, DIRT)), ruleSource4, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(1.75), STONE)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(2.0), ceilingGravel), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(1.0), STONE), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(-1.0), DIRT), ceilingGravel)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD), DIRT);
        SurfaceRules.RuleSource ruleSource8 = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0, 0.2), PACKED_ICE), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.ICE, 0.0, 0.025), ICE), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), ruleSource6, SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), SurfaceRules.sequence(SurfaceRules.ifTrue(steep, STONE), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.GROVE), SurfaceRules.sequence(ruleSource6, SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK))), ruleSource4, SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(1.75), STONE), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(-0.5), COARSE_DIRT))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(2.0), ceilingGravel), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(1.0), STONE), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(-1.0), ceilingDirt), ceilingGravel)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA), SurfaceRules.sequence(SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(1.75), COARSE_DIRT), SurfaceRules.ifTrue(RTFSurfaceRuleData.surfaceNoiseAbove(-0.95), PODZOL))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.ICE_SPIKES), SurfaceRules.ifTrue(conditionSource9, SNOW_BLOCK)), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), MUD), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), MYCELIUM), ceilingDirt);
        SurfaceRules.ConditionSource conditionSource16 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
        SurfaceRules.ConditionSource conditionSource17 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
        SurfaceRules.ConditionSource conditionSource18 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
        SurfaceRules.RuleSource floor = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), SurfaceRules.ifTrue(conditionSource, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource16, COARSE_DIRT), SurfaceRules.ifTrue(conditionSource17, COARSE_DIRT), SurfaceRules.ifTrue(conditionSource18, COARSE_DIRT), ceilingDirt))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.SWAMP), SurfaceRules.ifTrue(conditionSource6, SurfaceRules.ifTrue(SurfaceRules.not(conditionSource7), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), SurfaceRules.ifTrue(conditionSource5, SurfaceRules.ifTrue(SurfaceRules.not(conditionSource7), SurfaceRules.ifTrue(SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), WATER)))))), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS), SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource2, ORANGE_TERRACOTTA), SurfaceRules.ifTrue(conditionSource4, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource16, TERRACOTTA), SurfaceRules.ifTrue(conditionSource17, TERRACOTTA), SurfaceRules.ifTrue(conditionSource18, TERRACOTTA), SurfaceRules.bandlands())), SurfaceRules.ifTrue(conditionSource8, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), RED_SAND)), SurfaceRules.ifTrue(SurfaceRules.not(hole), ORANGE_TERRACOTTA), SurfaceRules.ifTrue(conditionSource10, WHITE_TERRACOTTA), ceilingGravel)), SurfaceRules.ifTrue(conditionSource3, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource7, SurfaceRules.ifTrue(SurfaceRules.not(conditionSource4), ORANGE_TERRACOTTA)), SurfaceRules.bandlands())), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, SurfaceRules.ifTrue(conditionSource10, WHITE_TERRACOTTA)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(conditionSource8, SurfaceRules.sequence(SurfaceRules.ifTrue(isFrozenOcean, SurfaceRules.ifTrue(hole, SurfaceRules.sequence(SurfaceRules.ifTrue(conditionSource9, AIR), SurfaceRules.ifTrue(SurfaceRules.temperature(), ICE), WATER))), ruleSource8))), SurfaceRules.ifTrue(conditionSource10, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.ifTrue(isFrozenOcean, SurfaceRules.ifTrue(hole, WATER))), SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, ruleSource7), SurfaceRules.ifTrue(isBeachOrWarm, SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, SANDSTONE)), SurfaceRules.ifTrue(isDesert, SurfaceRules.ifTrue(SurfaceRules.VERY_DEEP_UNDER_FLOOR, SANDSTONE)))), SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), STONE), SurfaceRules.ifTrue(SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), ceilingSand), ceilingGravel)));
        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
        if (bl2) {
            builder.add(SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.verticalGradient("bedrock_roof", VerticalAnchor.belowTop(5), VerticalAnchor.top())), BEDROCK));
        }
        if (bl3) {
            builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), BEDROCK));
        }
        SurfaceRules.RuleSource aboveSurface = SurfaceRules.ifTrue(SurfaceRules.abovePreliminarySurface(), floor);
        builder.add(bl ? aboveSurface : floor);
        
//        int scale = 600;
//        builder.add(
//        	SurfaceRules.ifTrue(SurfaceRules.not(SurfaceRules.abovePreliminarySurface()), 
//        		new StrataSurfaceRule.Source(Source.cell(scale)
//        	.warp(scale / 4, 2, scale / 2D)
//        	.warp(15, 2, 30), n)));
        builder.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
        return SurfaceRules.sequence((SurfaceRules.RuleSource[])builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }

    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double d) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, d / 8.25, Double.MAX_VALUE);
    }
}

