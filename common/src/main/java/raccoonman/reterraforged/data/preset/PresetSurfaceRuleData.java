package raccoonman.reterraforged.data.preset;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.CaveSurface;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.settings.SurfaceSettings;
import raccoonman.reterraforged.data.preset.settings.WorldSettings;
import raccoonman.reterraforged.tags.RTFBlockTags;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.surface.condition.RTFSurfaceConditions;
import raccoonman.reterraforged.world.worldgen.surface.rule.RTFSurfaceRules;
import raccoonman.reterraforged.world.worldgen.surface.rule.StrataRule;
import raccoonman.reterraforged.world.worldgen.util.Scaling;

public class PresetSurfaceRuleData {
    private static final SurfaceRules.RuleSource AIR = PresetSurfaceRuleData.makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = PresetSurfaceRuleData.makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = PresetSurfaceRuleData.makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = PresetSurfaceRuleData.makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.RuleSource BROWN_TERRACOTTA = PresetSurfaceRuleData.makeStateRule(Blocks.BROWN_TERRACOTTA);
    private static final SurfaceRules.RuleSource TERRACOTTA = PresetSurfaceRuleData.makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_SAND = PresetSurfaceRuleData.makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource RED_SANDSTONE = PresetSurfaceRuleData.makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource STONE = PresetSurfaceRuleData.makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource DEEPSLATE = PresetSurfaceRuleData.makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.RuleSource DIRT = PresetSurfaceRuleData.makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource PODZOL = PresetSurfaceRuleData.makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.RuleSource COARSE_DIRT = PresetSurfaceRuleData.makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.RuleSource MYCELIUM = PresetSurfaceRuleData.makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = PresetSurfaceRuleData.makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CALCITE = PresetSurfaceRuleData.makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.RuleSource GRAVEL = PresetSurfaceRuleData.makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SAND = PresetSurfaceRuleData.makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = PresetSurfaceRuleData.makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource SMOOTH_SANDSTONE = PresetSurfaceRuleData.makeStateRule(Blocks.SMOOTH_SANDSTONE);
    private static final SurfaceRules.RuleSource PACKED_ICE = PresetSurfaceRuleData.makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = PresetSurfaceRuleData.makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource MUD = PresetSurfaceRuleData.makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource POWDER_SNOW = PresetSurfaceRuleData.makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.RuleSource ICE = PresetSurfaceRuleData.makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = PresetSurfaceRuleData.makeStateRule(Blocks.WATER);

    private static final ResourceLocation STRATA_CACHE_ID = RTFCommon.location("default");
    
    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }
    
    public static SurfaceRules.RuleSource overworld(Preset preset, HolderGetter<Noise> noise) {
		WorldSettings worldSettings = preset.world();
		WorldSettings.Properties properties = worldSettings.properties;
		Scaling scaling = Scaling.make(properties.terrainScaler(), properties.seaLevel);
    	MiscellaneousSettings miscellaneousSettings = preset.miscellaneous();
    	
    	SurfaceSettings surfaceSettings = preset.surface();
    	SurfaceSettings.Erosion erosion = surfaceSettings.erosion();

    	int strataBufferAmount = 5;
    	
    	SurfaceRules.ConditionSource y4BelowSurface = SurfaceRules.stoneDepthCheck(3, false, CaveSurface.FLOOR);
        SurfaceRules.ConditionSource below97 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        SurfaceRules.ConditionSource below256 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(256), 0);
        SurfaceRules.ConditionSource above63 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(63), -1);
        SurfaceRules.ConditionSource above74 = SurfaceRules.yStartCheck(VerticalAnchor.absolute(74), 1);
        SurfaceRules.ConditionSource below60 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(60), 0);
        SurfaceRules.ConditionSource below62 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(62), 0);
        SurfaceRules.ConditionSource below63 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(63), 0);
        SurfaceRules.ConditionSource y1BelowSurface = SurfaceRules.waterBlockCheck(-1, 0);
        SurfaceRules.ConditionSource yOnSurface = SurfaceRules.waterBlockCheck(0, 0);
        SurfaceRules.ConditionSource y6BelowSurface = SurfaceRules.waterStartCheck(-6, -1);
        SurfaceRules.ConditionSource hole = SurfaceRules.hole();
        SurfaceRules.ConditionSource frozenOcean = SurfaceRules.isBiome(Biomes.FROZEN_OCEAN, Biomes.DEEP_FROZEN_OCEAN);
        SurfaceRules.ConditionSource badlands = SurfaceRules.isBiome(Biomes.BADLANDS, Biomes.ERODED_BADLANDS, Biomes.WOODED_BADLANDS);
        SurfaceRules.ConditionSource steep = SurfaceRules.steep();
        SurfaceRules.ConditionSource erodedRock = RTFSurfaceConditions.steepness(erosion.rockSteepness, noise.getOrThrow(PresetSurfaceNoise.STEEPNESS_VARIANCE));
        SurfaceRules.RuleSource erodedDirt = makeErodedDirtRule(noise, erosion);
        SurfaceRules.RuleSource grass = SurfaceRules.sequence(
        	erodedDirt,
        	SurfaceRules.ifTrue(
        		yOnSurface,
        		GRASS_BLOCK
        	), 
        	DIRT
        );
        SurfaceRules.RuleSource sand = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
        SurfaceRules.RuleSource gravel = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
        SurfaceRules.RuleSource windswept = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		RTFSurfaceConditions.sediment(5.3F), 
        		STONE
            ),
        	SurfaceRules.ifTrue(
        		RTFSurfaceConditions.sediment(2.4F), 
        		gravel
            ),
        	SurfaceRules.ifTrue(
        		RTFSurfaceConditions.erosion(8.25F),
        		STONE
        	),
        	SurfaceRules.ifTrue(
	        	RTFSurfaceConditions.erosion(5.5F),
	        	gravel
	        )
        );
        SurfaceRules.ConditionSource sandyBeach = SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.BEACH, Biomes.SNOWY_BEACH);
        SurfaceRules.ConditionSource desert = SurfaceRules.isBiome(Biomes.DESERT);
        SurfaceRules.RuleSource stony = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.STONY_PEAKS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.CALCITE, -0.0125, 0.0125), 
        				CALCITE
        			), 
        			STONE
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.STONY_SHORE), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.noiseCondition(Noises.GRAVEL, -0.05, 0.05), 
        				gravel
        			), 
        			STONE
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS), 
        		SurfaceRules.ifTrue(
//        			PresetSurfaceRuleData.surfaceNoiseAbove(1.0), 
        			RTFSurfaceConditions.sediment(5.0F),
        			STONE
        		)
        	), 
        	SurfaceRules.ifTrue(
        		sandyBeach, 
        		sand
        	), 
        	SurfaceRules.ifTrue(
        		desert, 
        		sand
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.DRIPSTONE_CAVES), 
        		STONE
        	)
        );
        SurfaceRules.RuleSource snowUnderFloor = SurfaceRules.ifTrue(
        	SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.45, 0.58), 
        	SurfaceRules.ifTrue(
        		yOnSurface,
        		POWDER_SNOW
        	)
        );
        SurfaceRules.RuleSource snowOnFloor = SurfaceRules.ifTrue(
        	SurfaceRules.noiseCondition(Noises.POWDER_SNOW, 0.35, 0.6), 
        	SurfaceRules.ifTrue(
        		yOnSurface, 
        		POWDER_SNOW
        	)
        );
        SurfaceRules.RuleSource underFloor = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				steep, 
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
        				yOnSurface, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				steep, 
        				STONE
        			), 
        			snowUnderFloor, 
        			SurfaceRules.ifTrue(
        				yOnSurface, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), 
        		STONE
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.GROVE), 
        		SurfaceRules.sequence(
        			snowUnderFloor, 
        			DIRT
        		)
        	), 
        	stony, 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), 
        		SurfaceRules.ifTrue(
        			PresetSurfaceRuleData.surfaceNoiseAbove(1.75), 
        			STONE
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), 
        		SurfaceRules.sequence(
        			windswept,
        			DIRT
        			
//        			SurfaceRules.ifTrue(
//        				PresetSurfaceRuleData.surfaceNoiseAbove(2.0), 
//        				gravel
//        			), 
//        			SurfaceRules.ifTrue(
//        				PresetSurfaceRuleData.surfaceNoiseAbove(1.0), 
//        				STONE
//        			), 
//        			SurfaceRules.ifTrue(
//        				PresetSurfaceRuleData.surfaceNoiseAbove(-1.0), 
//        				DIRT
//        			), 
//        			gravel
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), 
        		MUD
        	),
        	erodedDirt,
        	DIRT
        );
        SurfaceRules.RuleSource onFloor = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				steep, 
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
        				yOnSurface, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.SNOWY_SLOPES), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				steep, 
        				STONE
        			), 
        			snowOnFloor, 
        			SurfaceRules.ifTrue(
        				yOnSurface, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.JAGGED_PEAKS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				steep, 
        				DEEPSLATE
        			), 
        			SurfaceRules.ifTrue(
        				yOnSurface, 
        				SNOW_BLOCK
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.GROVE), 
        		SurfaceRules.sequence(
        			snowOnFloor, 
        			SurfaceRules.ifTrue(
        				yOnSurface, 
        				SNOW_BLOCK
        			)
        		)
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.FOREST, Biomes.DARK_FOREST),
        		makeForestRule(noise)
        	),
        	stony, 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				PresetSurfaceRuleData.surfaceNoiseAbove(1.75), 
        				STONE
        			), 
        			SurfaceRules.ifTrue(
        				PresetSurfaceRuleData.surfaceNoiseAbove(-0.5), 
        				COARSE_DIRT
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), 
        		SurfaceRules.sequence(
            		windswept,
            		GRASS_BLOCK
//        			SurfaceRules.ifTrue(
//        				PresetSurfaceRuleData.surfaceNoiseAbove(2.0), 
//        				gravel
//        			), 
//        			SurfaceRules.ifTrue(
//        				PresetSurfaceRuleData.surfaceNoiseAbove(1.0),
//        				STONE
//        			),
//        			SurfaceRules.ifTrue(
//        				PresetSurfaceRuleData.surfaceNoiseAbove(-1.0),
//        				grass
//        			),
//        			gravel
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				PresetSurfaceRuleData.surfaceNoiseAbove(1.75), 
        				COARSE_DIRT
        			), 
        			SurfaceRules.ifTrue(
        				PresetSurfaceRuleData.surfaceNoiseAbove(-0.95), 
        				PODZOL
        			)
        		)
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.ICE_SPIKES), 
        		SurfaceRules.ifTrue(
        			yOnSurface,
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
        	grass
        );
        SurfaceRules.ConditionSource surfaceNoise1 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
        SurfaceRules.ConditionSource surfaceNoise2 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
        SurfaceRules.ConditionSource surfaceNoise3 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
        SurfaceRules.RuleSource surface = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		y4BelowSurface, 
        		makeDesertRule(scaling, noise)
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.ON_FLOOR, 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.WOODED_BADLANDS),
        				SurfaceRules.ifTrue(
	        				SurfaceRules.not(erodedRock),
	        				SurfaceRules.ifTrue(
	        					below97, 
	        					SurfaceRules.sequence(
	        						SurfaceRules.ifTrue(
	        							surfaceNoise1, 
	        							COARSE_DIRT
			        				), 
	        						SurfaceRules.ifTrue(
	        							surfaceNoise2, 
	        							COARSE_DIRT
			        				), 
	        						SurfaceRules.ifTrue(
	        							surfaceNoise3, 
	        							COARSE_DIRT
			        				), 
	        						grass
			        			)
	        				)
	        			)
        			),
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.SWAMP), 
        				SurfaceRules.ifTrue(
        					below62, 
        					SurfaceRules.ifTrue(
        						SurfaceRules.not(below63), 
        						SurfaceRules.ifTrue(
        							SurfaceRules.noiseCondition(Noises.SWAMP, 0.0), 
        							WATER
        						)
        					)
        				)
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), 
        				SurfaceRules.ifTrue(
        					below60, 
        					SurfaceRules.ifTrue(
        						SurfaceRules.not(below63), 
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
        		badlands, 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.ON_FLOOR, 
        				SurfaceRules.sequence(
        					SurfaceRules.ifTrue(
        						below256,
        						ORANGE_TERRACOTTA
        					), 
        					SurfaceRules.ifTrue(
        						above74, 
        						SurfaceRules.sequence(
        							SurfaceRules.ifTrue(
        								surfaceNoise1, 
        								TERRACOTTA
        							), 
        							SurfaceRules.ifTrue(
        								surfaceNoise2, 
        								TERRACOTTA
        							), 
        							SurfaceRules.ifTrue(
        								surfaceNoise3, 
        								TERRACOTTA
        							), 
        							SurfaceRules.bandlands()
        						)
        					), 
        					SurfaceRules.ifTrue(
        						y1BelowSurface,
        						SurfaceRules.sequence(
        							SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, RED_SANDSTONE), 
        							RED_SAND
        						)
        					), 
        					SurfaceRules.ifTrue(
        						SurfaceRules.not(hole), 
        						ORANGE_TERRACOTTA
        					), 
        					SurfaceRules.ifTrue(
        						y6BelowSurface, 
        						WHITE_TERRACOTTA
        					), 
        					gravel
        				)
        			), 
        			SurfaceRules.ifTrue(
        				above63, 
        				SurfaceRules.sequence(
        					SurfaceRules.ifTrue(
        						below63, 
        						SurfaceRules.ifTrue(
        							SurfaceRules.not(above74), 
        							ORANGE_TERRACOTTA
        						)
        					), 
        					SurfaceRules.bandlands()
        				)
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.UNDER_FLOOR, 
        				SurfaceRules.ifTrue(
        					y6BelowSurface, 
        					WHITE_TERRACOTTA
        				)
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.ON_FLOOR, 
        		SurfaceRules.ifTrue(
        			y1BelowSurface, 
        			SurfaceRules.sequence(
        				SurfaceRules.ifTrue(
        					frozenOcean, 
        					SurfaceRules.ifTrue(
        						hole, 
        						SurfaceRules.sequence(
        							SurfaceRules.ifTrue(
        								yOnSurface, 
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
        				SurfaceRules.ifTrue(
        					SurfaceRules.not(erodedRock),
        					onFloor
        				)
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		y6BelowSurface, 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.ON_FLOOR, 
        				SurfaceRules.ifTrue(
        					frozenOcean, 
        					SurfaceRules.ifTrue(
        						hole, 
        						WATER
        					)
        				)
        			),
        			SurfaceRules.ifTrue(
        				SurfaceRules.UNDER_FLOOR,
        				SurfaceRules.ifTrue(
        					SurfaceRules.not(erodedRock),
        					underFloor
        				)
        			),
        			SurfaceRules.ifTrue(
        				sandyBeach,
        				SurfaceRules.ifTrue(
        					SurfaceRules.DEEP_UNDER_FLOOR, 
        					SANDSTONE
        				)
        			),
        			SurfaceRules.ifTrue(
        				desert, 
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
        				STONE
        			), 
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.WARM_OCEAN, Biomes.LUKEWARM_OCEAN, Biomes.DEEP_LUKEWARM_OCEAN),
        				sand
        			),
        			SurfaceRules.ifTrue(
        				SurfaceRules.not(erodedRock),
        				gravel
        			)
        		)
        	)//,
//        	SurfaceRules.ifTrue(
//            	erodedRock,
//            	SurfaceRules.ifTrue(
//            		SurfaceRules.stoneDepthCheck(strataBufferAmount, false, CaveSurface.FLOOR),
//            		makeStrataRule(strataBufferAmount, miscellaneousSettings, noise)
//            	)
//            )
        );
        List<SurfaceRules.RuleSource> list = Lists.newArrayList(
        	SurfaceRules.ifTrue(
        		SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
        		BEDROCK
        	),
        	SurfaceRules.ifTrue(
        		SurfaceRules.abovePreliminarySurface(),
        		surface
        	),
//        	makeStrataRule(1, miscellaneousSettings, noise),
        	SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE)
        );
        SurfaceRules.RuleSource rules = SurfaceRules.sequence(list.toArray(SurfaceRules.RuleSource[]::new));
        return rules;
    }
    
    private static SurfaceRules.RuleSource makeDesertRule(Scaling scaling, HolderGetter<Noise> noise) {
    	Holder<Noise> variance = noise.getOrThrow(PresetSurfaceNoise.DESERT);
    	float min = scaling.ground(10);
    	float level = scaling.ground(40);
    	
    	SurfaceRules.ConditionSource aboveLevel = RTFSurfaceConditions.height(level, variance);
        SurfaceRules.ConditionSource desert = SurfaceRules.isBiome(Biomes.DESERT);
    	return SurfaceRules.ifTrue(
    		RTFSurfaceConditions.height(min),
    		SurfaceRules.sequence(
    			SurfaceRules.ifTrue(
    				RTFSurfaceConditions.steepness(0.15F), 
			        SurfaceRules.ifTrue(
			        	desert, 
			        	SurfaceRules.ifTrue(
			        		aboveLevel, 
			        		SurfaceRules.sequence(
								SurfaceRules.ifTrue(RTFSurfaceConditions.steepness(0.975F), TERRACOTTA),
								SurfaceRules.ifTrue(RTFSurfaceConditions.steepness(0.85F), BROWN_TERRACOTTA),
								SurfaceRules.ifTrue(RTFSurfaceConditions.steepness(0.75F), ORANGE_TERRACOTTA),
								SurfaceRules.ifTrue(RTFSurfaceConditions.steepness(0.65F), TERRACOTTA), 
								SMOOTH_SANDSTONE
							)
						)
    	            )
    			),
        		SurfaceRules.ifTrue(
        			RTFSurfaceConditions.steepness(0.3F), 
        			SurfaceRules.ifTrue(
        				desert, 
        				SMOOTH_SANDSTONE
        			)
            	)
    		)
    	);
    }
    
    private static SurfaceRules.RuleSource makeForestRule(HolderGetter<Noise> noise) {
    	return SurfaceRules.ifTrue(
    		SurfaceRules.ON_FLOOR, 
    		RTFSurfaceRules.noise(
    			noise.getOrThrow(PresetSurfaceNoise.FOREST), 
    			List.of(
    				Pair.of(0.65F, PODZOL),
    				Pair.of(0.725F, DIRT)
    			)
    		)	
    	);
    }
    
	private static SurfaceRules.RuleSource makeStrataRule(int buffer, MiscellaneousSettings miscellaneousSettings, HolderGetter<Noise> noise) {
		List<StrataRule.Layer> layers = new ArrayList<>();
		
		Holder<Noise> depth = noise.getOrThrow(PresetStrataNoise.STRATA_DEPTH);
		layers.add(new StrataRule.Layer(RTFBlockTags.SOIL, depth, 3, 0, 1, 0.1F, 0.25F));
		layers.add(new StrataRule.Layer(RTFBlockTags.SEDIMENT, depth, 3, 0, 2, 0.05F, 0.15F));
		layers.add(new StrataRule.Layer(RTFBlockTags.CLAY, depth, 3, 0, 2, 0.05F, 0.1F));
		layers.add(new StrataRule.Layer(miscellaneousSettings.rockTag(), depth, 3, 10, 30, 0.1F, 1.5F));
		return new StrataRule(STRATA_CACHE_ID, buffer, 100, noise.getOrThrow(PresetSurfaceNoise.STRATA_REGION), layers);
	}

    private static SurfaceRules.RuleSource makeErodedDirtRule(HolderGetter<Noise> noise, SurfaceSettings.Erosion settings) {
    	return SurfaceRules.ifTrue(
    		RTFSurfaceConditions.steepness(settings.dirtSteepness, noise.getOrThrow(PresetSurfaceNoise.STEEPNESS_VARIANCE)),
    		SurfaceRules.ifTrue(
    			RTFSurfaceConditions.height(noise.getOrThrow(PresetSurfaceNoise.ERODED_DIRT), noise.getOrThrow(PresetSurfaceNoise.HEIGHT_VARIANCE)),
    			COARSE_DIRT
    		)
    	);
    }
	
    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double target) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, target / 8.25D, Double.MAX_VALUE);
    }
}