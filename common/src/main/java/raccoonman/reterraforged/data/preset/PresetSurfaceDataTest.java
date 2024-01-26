package raccoonman.reterraforged.data.preset;

import com.google.common.collect.ImmutableList;

import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.settings.SurfaceSettings;
import raccoonman.reterraforged.world.worldgen.surface.condition.RTFSurfaceConditions;

public class PresetSurfaceDataTest {
    private static final SurfaceRules.RuleSource AIR = PresetSurfaceDataTest.makeStateRule(Blocks.AIR);
    private static final SurfaceRules.RuleSource BEDROCK = PresetSurfaceDataTest.makeStateRule(Blocks.BEDROCK);
    private static final SurfaceRules.RuleSource WHITE_TERRACOTTA = PresetSurfaceDataTest.makeStateRule(Blocks.WHITE_TERRACOTTA);
    private static final SurfaceRules.RuleSource ORANGE_TERRACOTTA = PresetSurfaceDataTest.makeStateRule(Blocks.ORANGE_TERRACOTTA);
    private static final SurfaceRules.RuleSource TERRACOTTA = PresetSurfaceDataTest.makeStateRule(Blocks.TERRACOTTA);
    private static final SurfaceRules.RuleSource RED_SAND = PresetSurfaceDataTest.makeStateRule(Blocks.RED_SAND);
    private static final SurfaceRules.RuleSource RED_SANDSTONE = PresetSurfaceDataTest.makeStateRule(Blocks.RED_SANDSTONE);
    private static final SurfaceRules.RuleSource STONE = PresetSurfaceDataTest.makeStateRule(Blocks.STONE);
    private static final SurfaceRules.RuleSource DEEPSLATE = PresetSurfaceDataTest.makeStateRule(Blocks.DEEPSLATE);
    private static final SurfaceRules.RuleSource DIRT = PresetSurfaceDataTest.makeStateRule(Blocks.DIRT);
    private static final SurfaceRules.RuleSource PODZOL = PresetSurfaceDataTest.makeStateRule(Blocks.PODZOL);
    private static final SurfaceRules.RuleSource COARSE_DIRT = PresetSurfaceDataTest.makeStateRule(Blocks.COARSE_DIRT);
    private static final SurfaceRules.RuleSource MYCELIUM = PresetSurfaceDataTest.makeStateRule(Blocks.MYCELIUM);
    private static final SurfaceRules.RuleSource GRASS_BLOCK = PresetSurfaceDataTest.makeStateRule(Blocks.GRASS_BLOCK);
    private static final SurfaceRules.RuleSource CALCITE = PresetSurfaceDataTest.makeStateRule(Blocks.CALCITE);
    private static final SurfaceRules.RuleSource GRAVEL = PresetSurfaceDataTest.makeStateRule(Blocks.GRAVEL);
    private static final SurfaceRules.RuleSource SAND = PresetSurfaceDataTest.makeStateRule(Blocks.SAND);
    private static final SurfaceRules.RuleSource SANDSTONE = PresetSurfaceDataTest.makeStateRule(Blocks.SANDSTONE);
    private static final SurfaceRules.RuleSource PACKED_ICE = PresetSurfaceDataTest.makeStateRule(Blocks.PACKED_ICE);
    private static final SurfaceRules.RuleSource SNOW_BLOCK = PresetSurfaceDataTest.makeStateRule(Blocks.SNOW_BLOCK);
    private static final SurfaceRules.RuleSource MUD = PresetSurfaceDataTest.makeStateRule(Blocks.MUD);
    private static final SurfaceRules.RuleSource POWDER_SNOW = PresetSurfaceDataTest.makeStateRule(Blocks.POWDER_SNOW);
    private static final SurfaceRules.RuleSource ICE = PresetSurfaceDataTest.makeStateRule(Blocks.ICE);
    private static final SurfaceRules.RuleSource WATER = PresetSurfaceDataTest.makeStateRule(Blocks.WATER);

    private static SurfaceRules.RuleSource makeStateRule(Block block) {
        return SurfaceRules.state(block.defaultBlockState());
    }

    public static SurfaceRules.RuleSource overworld(Preset preset) {
    	SurfaceSettings surfaceSettings = preset.surface();
    	SurfaceSettings.Erosion erosion = surfaceSettings.erosion();
    	
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
        SurfaceRules.ConditionSource rockSteepness = RTFSurfaceConditions.isSteeperThan(erosion.rockSteepness);
        SurfaceRules.ConditionSource dirtSteepness = RTFSurfaceConditions.isSteeperThan(erosion.dirtSteepness);
        SurfaceRules.RuleSource grass = SurfaceRules.sequence(
        	SurfaceRules.ifTrue(
        		yOnSurface, 
        		GRASS_BLOCK
        	), 
        	DIRT
        );
        SurfaceRules.RuleSource sand = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, SANDSTONE), SAND);
        SurfaceRules.RuleSource gravel = SurfaceRules.sequence(SurfaceRules.ifTrue(SurfaceRules.ON_CEILING, STONE), GRAVEL);
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
        			PresetSurfaceDataTest.surfaceNoiseAbove(1.0), 
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
        			PresetSurfaceDataTest.surfaceNoiseAbove(1.75), 
        			STONE
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(2.0), 
        				gravel
        			), 
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(1.0), 
        				STONE
        			), 
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(-1.0), 
        				DIRT
        			), 
        			gravel
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.MANGROVE_SWAMP), 
        		MUD
        	),
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
        				STONE
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
        	stony, 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_SAVANNA), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(1.75), 
        				STONE
        			), 
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(-0.5), 
        				COARSE_DIRT
        			)
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS), 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(2.0), 
        				gravel
        			), 
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(1.0),
        				STONE
        			),
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(-1.0),
        				grass
        			),
        			gravel
        		)
        	), 
        	SurfaceRules.ifTrue(
        		SurfaceRules.isBiome(Biomes.OLD_GROWTH_PINE_TAIGA, Biomes.OLD_GROWTH_SPRUCE_TAIGA),
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(1.75), 
        				COARSE_DIRT
        			), 
        			SurfaceRules.ifTrue(
        				PresetSurfaceDataTest.surfaceNoiseAbove(-0.95), 
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
        		SurfaceRules.ON_FLOOR, 
        		SurfaceRules.sequence(
        			SurfaceRules.ifTrue(
        				SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), 
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
        					SurfaceRules.not(rockSteepness),
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
        					SurfaceRules.not(rockSteepness),
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
        				SurfaceRules.not(rockSteepness),
        				gravel
        			)
        		)
        	)
        );
        ImmutableList.Builder<SurfaceRules.RuleSource> rules = ImmutableList.builder();
        rules.add(
        	SurfaceRules.ifTrue(
        		SurfaceRules.verticalGradient("bedrock_floor", VerticalAnchor.bottom(), VerticalAnchor.aboveBottom(5)),
        		BEDROCK
        	)
        );
        SurfaceRules.RuleSource preliminarySurface = SurfaceRules.ifTrue(
        	SurfaceRules.abovePreliminarySurface(), 
        	surface
        );
        
        rules.add(preliminarySurface);
        rules.add(SurfaceRules.ifTrue(SurfaceRules.verticalGradient("deepslate", VerticalAnchor.absolute(0), VerticalAnchor.absolute(8)), DEEPSLATE));
        return SurfaceRules.sequence(rules.build().toArray(SurfaceRules.RuleSource[]::new));
    }

    private static SurfaceRules.ConditionSource surfaceNoiseAbove(double target) {
        return SurfaceRules.noiseCondition(Noises.SURFACE, target / 8.25D, Double.MAX_VALUE);
    }
}