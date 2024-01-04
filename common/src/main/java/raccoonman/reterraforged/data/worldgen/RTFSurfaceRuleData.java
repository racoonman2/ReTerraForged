package raccoonman.reterraforged.data.worldgen;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import raccoonman.reterraforged.data.worldgen.preset.MiscellaneousSettings;
import raccoonman.reterraforged.data.worldgen.preset.Preset;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.terrablender.TBCompat;

public final class RTFSurfaceRuleData {
    private static final SurfaceRules.RuleSource AIR = makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.RuleSource TERRACOTTA = makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_SAND = makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource RED_SANDSTONE = makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource DEEPSLATE = makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.RuleSource DIRT = makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource PODZOL = makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.RuleSource COARSE_DIRT = makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.RuleSource MYCELIUM = makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CALCITE = makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.RuleSource GRAVEL = makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SAND = makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource CLAY = makeStateRule(Blocks.CLAY);
    private static final SurfaceRules.RuleSource PACKED_ICE = makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource MUD = makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource POWDER_SNOW = makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.RuleSource ICE = makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = makeStateRule(Blocks.WATER);

    // maybe put forest surface in meadow as well?
    public static SurfaceRules.RuleSource overworld(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		SurfaceRules.ConditionSource conditionSource = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        SurfaceRules.ConditionSource conditionSource2 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
        SurfaceRules.ConditionSource conditionSource3 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
        SurfaceRules.ConditionSource conditionSource4 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
        SurfaceRules.ConditionSource aboveY60 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
        SurfaceRules.ConditionSource aboveY62 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
        SurfaceRules.ConditionSource aboveY63 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
        SurfaceRules.ConditionSource surfaceCheck = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource conditionSource9 = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.ConditionSource conditionSource10 = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.ConditionSource isHole = SurfaceRules.hole();
        SurfaceRules.ConditionSource isFrozenOcean = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        SurfaceRules.ConditionSource isSteep = SurfaceRules.steep();

//        SurfaceRules.RuleSource strata = miscellaneous.strataDecorator ? makeStrataRule(noise, miscellaneous.strataRegionSize) : makeStateRule(Blocks.STONE);
        
        SurfaceRules.RuleSource defaultSurface = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		conditionSource9, 
        		GRASS_BLOCK
        	), 
        	DIRT
        );
        SurfaceRules.RuleSource sandySurface = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.ON_CEILING, 
        		SANDSTONE
        	), 
        	SAND
        );
        SurfaceRules.RuleSource gravellySurface = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.ON_CEILING, 
        		makeStateRule(Blocks.STONE)
        	), 
        	GRAVEL
        );
        SurfaceRules.ConditionSource isSandy = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        SurfaceRules.ConditionSource isDesert = SurfaceRules.isBiome(Biomes.DESERT);
        SurfaceRules.RuleSource ruleSource4 = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.STONY_PEAKS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125, 0.0125), 
        				CALCITE
        			), 
        			makeStateRule(Blocks.STONE)
        		)
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.STONY_SHORE), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05, 0.05), 
        				gravellySurface
        			), 
        			makeStateRule(Blocks.STONE)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), 
        		SurfaceRules.ifTrue(
        			surfaceNoiseAbove(1.0), 
        			makeStateRule(Blocks.STONE)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		isSandy, 
        		sandySurface
        	), 
        	SurfaceRules.ifTrue(
        		isDesert, 
        		sandySurface
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), 
        		makeStateRule(Blocks.STONE)
        	)
        );
        SurfaceRules.RuleSource ruleSource5 = SurfaceRules.ifTrue(
        	SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), 
        	SurfaceRules.ifTrue(
        		conditionSource9, 
        		POWDER_SNOW
        	)
        );
        SurfaceRules.RuleSource ruleSource6 = SurfaceRules.ifTrue(
        	SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), 
        	SurfaceRules.ifTrue(
        		conditionSource9, 
        		POWDER_SNOW
        	)
        );
        SurfaceRules.RuleSource ruleSource7 = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				isSteep, 
        				PACKED_ICE
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.PACKED_ICE, -0.5, 0.2), 
        				PACKED_ICE
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.ICE, -0.0625, 0.025), 
        				ICE
        			), 
        			SurfaceRules.ifTrue(
        				conditionSource9, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				isSteep, 
        				makeStateRule(Blocks.STONE)
        			), 
        			ruleSource5, 
        			SurfaceRules.ifTrue(
        				conditionSource9, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), 
        		makeStateRule(Blocks.STONE)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.GROVE), 
        		SurfaceRules.sequence(
        			ruleSource5, 
        			DIRT
        		)
        	), 
        	ruleSource4, 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), 
        		SurfaceRules.ifTrue(
        			surfaceNoiseAbove(1.75), 
        			makeStateRule(Blocks.STONE)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(2.0), 
        				gravellySurface
        			), 
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(1.0), 
        				makeStateRule(Blocks.STONE)
        			), 
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(-1.0), 
        				DIRT
        			), 
        			gravellySurface
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), 
        		MUD
        	), 
        	DIRT
        );
        SurfaceRules.RuleSource ruleSource8 = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				isSteep, 
        				PACKED_ICE
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.PACKED_ICE, 0.0, 0.2),
        				PACKED_ICE
        			),
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.ICE, 0.0, 0.025), 
        				ICE
        			), 
        			SurfaceRules.ifTrue(
        				conditionSource9,
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				isSteep, 
        				makeStateRule(Blocks.STONE)
        			), 
        			ruleSource6,
        			SurfaceRules.ifTrue(
        				conditionSource9, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.JAGGED_PEAKS),
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				isSteep, 
        				makeStateRule(Blocks.STONE)
        			), 
        			SurfaceRules.ifTrue(
        				conditionSource9,
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.GROVE),
        		SurfaceRules.sequence(
        			ruleSource6, 
        			SurfaceRules.ifTrue(
        				conditionSource9, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	ruleSource4,
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(1.75),
        				makeStateRule(Blocks.STONE)
        			),
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(-0.5),
        				COARSE_DIRT
        			)
        		)
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS),
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(2.0), 
        				gravellySurface
        			),
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(1.0),
        				makeStateRule(Blocks.STONE)
        			),
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(-1.0),
        				defaultSurface
        			), 
        			gravellySurface
        		)
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(1.75),
        				COARSE_DIRT
        			),
        			SurfaceRules.ifTrue(
        				surfaceNoiseAbove(-0.95), 
        				PODZOL
        			)
        		)
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.ICE_SPIKES),
        		SurfaceRules.ifTrue(
        			conditionSource9, 
        			SNOW_BLOCK
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP),
        		MUD
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.MUSHROOM_FIELDS), 
        		MYCELIUM
        	),
//        	SurfaceRules.ifTrue(
//        		SurfaceRules.isBiome(Biomes.FOREST, Biomes.DARK_FOREST),
//        		makeForestRule(noise)
//        	),
        	defaultSurface
        );
        SurfaceRules.ConditionSource conditionSource16 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
        SurfaceRules.ConditionSource conditionSource17 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
        SurfaceRules.ConditionSource conditionSource18 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
        SurfaceRules.RuleSource surface = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.ON_FLOOR, 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.WOODED_BADLANDS),
        				SurfaceRules.ifTrue(
        					conditionSource, 
        					SurfaceRules.sequence(
        						SurfaceRules.ifTrue(
        							conditionSource16, 
        							COARSE_DIRT
        						),
        						SurfaceRules.ifTrue(
        							conditionSource17,
        							COARSE_DIRT
        						), 
        						SurfaceRules.ifTrue(
        							conditionSource18, 
        							COARSE_DIRT
        						),
        						defaultSurface
        					)
        				)
        			), 
//        			SurfaceRules.ifTrue(
//        				SurfaceRules.isBiome(Biomes.SWAMP), 
//        				SurfaceRules.ifTrue(
//        					SurfaceRules.not(aboveY63), 
//        					makeSwampRule(noise)
//        				)
//        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), 
        				SurfaceRules.ifTrue(
        					aboveY60, 
        					SurfaceRules.ifTrue(
        						SurfaceRules.not(aboveY63),
        						SurfaceRules.ifTrue(
        							SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), 
        							WATER
        						)
        					)
        				)
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS),
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.ON_FLOOR,
        				SurfaceRules.sequence(
        					SurfaceRules.ifTrue(
        						conditionSource2,
        						ORANGE_TERRACOTTA
        					), 
        					SurfaceRules.ifTrue(
        						conditionSource4,
        						SurfaceRules.sequence(
        							SurfaceRules.ifTrue(
        								conditionSource16, 
        								TERRACOTTA
        							),
        							SurfaceRules.ifTrue(
        								conditionSource17,
        								TERRACOTTA
        							), 
        							SurfaceRules.ifTrue(
        								conditionSource18, 
        								TERRACOTTA
        							), 
        							SurfaceRules.bandlands()
        						)
        					), 
        					SurfaceRules.ifTrue(
        						surfaceCheck, 
        						SurfaceRules.sequence(
        							SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), 
        							RED_SAND
        						)
        					),
        					SurfaceRules.ifTrue(
        						SurfaceRules.not(isHole), 
        						ORANGE_TERRACOTTA
        					), 
        					SurfaceRules.ifTrue(
        						conditionSource10, 
        						WHITE_TERRACOTTA
        					), 
        					gravellySurface
        				)
        			), 
        			SurfaceRules.ifTrue(
        				conditionSource3, 
        				SurfaceRules.sequence(
        					SurfaceRules.ifTrue(
        						aboveY63, 
        						SurfaceRules.ifTrue(
        							SurfaceRules.not(conditionSource4), 
        							ORANGE_TERRACOTTA
        						)
        					), 
        					SurfaceRules.bandlands()
        				)
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.UNDER_FLOOR, 
        				SurfaceRules.ifTrue(
        					conditionSource10, 
        					WHITE_TERRACOTTA
        				)
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.ON_FLOOR, 
        		SurfaceRules.ifTrue(
        			surfaceCheck, 
        			SurfaceRules.sequence(
        				SurfaceRules.ifTrue(
        					isFrozenOcean, 
        					SurfaceRules.ifTrue(
        						isHole, 
        						SurfaceRules.sequence(
        							SurfaceRules.ifTrue(
        								conditionSource9, 
        								AIR
        							), 
        							SurfaceRules.ifTrue(
        								SurfaceRules.temperature(), 
        								ICE
        							), 
        							WATER
        						)
        					)
        				), 
        				ruleSource8
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		conditionSource10, 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.ON_FLOOR, 
        				SurfaceRules.ifTrue(
        					isFrozenOcean, 
        					SurfaceRules.ifTrue(
        						isHole, 
        						WATER
        					)
        				)
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.UNDER_FLOOR, 
        				ruleSource7
        			), 
        			SurfaceRules.ifTrue(
        				isSandy, 
        				SurfaceRules.ifTrue(
        					SurfaceRules.DEEP_UNDER_FLOOR, 
        					SANDSTONE
        				)
        			), 
        			SurfaceRules.ifTrue(
        				isDesert, 
        				SurfaceRules.ifTrue(
        					SurfaceRules.VERY_DEEP_UNDER_FLOOR, 
        					SANDSTONE
        				)
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.ON_FLOOR, 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.FROZEN_PEAKS, Biomes.JAGGED_PEAKS), 
        				makeStateRule(Blocks.STONE)
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN), 
        				sandySurface
        			), 
        			gravellySurface
        		)
        	)
        );

        ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
        builder.add(
        	SurfaceRules.ifTrue(
        		SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)), 
        		BEDROCK
        	)
        );
        SurfaceRules.RuleSource abovePreliminarySurface = SurfaceRules.ifTrue(
        	SurfaceRules.abovePreliminarySurface(), 
        	surface
        );
        builder.add(abovePreliminarySurface);
        builder.add(
        	SurfaceRules.ifTrue(
        		SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), 
        		DEEPSLATE
        	)
        );

		if(miscellaneous.strataDecorator) {
			builder.add(makeStateRule(Blocks.STONE));
		}
        
        SurfaceRules.RuleSource root = SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
        if(TBCompat.isEnabled()	) {
//          SurfaceRuleManager.setDefaultSurfaceRules(RuleCategory.OVERWORLD, root);
        }
		return root;
    }
    
    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double d) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, d / 8.25D, Double.MAX_VALUE);
    }
}
