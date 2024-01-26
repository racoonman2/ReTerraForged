package raccoonman.reterraforged.data.preset;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.Noises;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.preset.settings.MiscellaneousSettings;
import raccoonman.reterraforged.data.preset.settings.Preset;
import raccoonman.reterraforged.data.preset.settings.WorldSettings;
import raccoonman.reterraforged.tags.RTFBlockTags;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.surface.condition.RTFSurfaceConditions;
import raccoonman.reterraforged.world.worldgen.surface.rule.RTFSurfaceRules;
import raccoonman.reterraforged.world.worldgen.surface.rule.StrataRule.Strata;

// maybe have a custom meadow or cherry forest surface ?
public class PresetSurfaceRuleData {
	private static final SurfaceRules.RuleSource PACKED_ICE = SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState());
	private static final SurfaceRules.RuleSource PODZOL = SurfaceRules.state(Blocks.PODZOL.defaultBlockState());
	private static final SurfaceRules.RuleSource GRASS = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
	private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
	private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.STONE.defaultBlockState());
	private static final SurfaceRules.RuleSource GRAVEL = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());
    private static final SurfaceRules.RuleSource COARSE_DIRT = SurfaceRules.state(Blocks.COARSE_DIRT.defaultBlockState());
	
    public static SurfaceRules.RuleSource overworld(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise) {
    	return PresetSurfaceDataTest.overworld(preset);// makeSurface(preset, densityFunctions, noise, SurfaceRuleData.overworld());
    }
    
    public static SurfaceRules.RuleSource makeSurface(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise, SurfaceRules.RuleSource source) {
    	WorldSettings worldSettings = preset.world();
    	WorldSettings.Properties properties = worldSettings.properties;
    	MiscellaneousSettings miscellaneousSettings = preset.miscellaneous();
    	Levels levels = new Levels(properties.terrainScaler(), properties.seaLevel);
    	
//    	SurfaceRules.ConditionSource isDesert = SurfaceRules.isBiome(Biomes.DESERT);
//    	SurfaceRules.ConditionSource isMountain = RTFSurfaceConditions.isTerrain(TerrainType.MOUNTAINS_1, TerrainType.MOUNTAINS_2, TerrainType.MOUNTAINS_3, TerrainType.MOUNTAIN_CHAIN);
    	SurfaceRules.ConditionSource noise1 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.909, -0.5454);
    	SurfaceRules.ConditionSource noise2 = SurfaceRules.noiseCondition(Noises.SURFACE, -0.1818, 0.1818);
    	SurfaceRules.ConditionSource noise3 = SurfaceRules.noiseCondition(Noises.SURFACE, 0.5454, 0.909);
        SurfaceRules.ConditionSource below97 = SurfaceRules.yBlockCheck(VerticalAnchor.absolute(97), 2);
        SurfaceRules.ConditionSource surface = SurfaceRules.waterBlockCheck(0, 0);
       	SurfaceRules.ifTrue(
    		SurfaceRules.ON_FLOOR,
    		SurfaceRules.sequence(
    			SurfaceRules.ifTrue(
    				SurfaceRules.isBiome(Biomes.WOODED_BADLANDS), 
    				SurfaceRules.ifTrue(
    					below97, 
    					SurfaceRules.sequence(
    						SurfaceRules.ifTrue(noise1, COARSE_DIRT), 
    						SurfaceRules.ifTrue(noise2, COARSE_DIRT), 
    						SurfaceRules.ifTrue(noise3, COARSE_DIRT),
    						SurfaceRules.sequence(
    							SurfaceRules.ifTrue(surface, GRASS), 
    							DIRT
    						)
    					)
    				)
    			)
    		)
    	);
    	
    	SurfaceRules.RuleSource grassSurface = makeSurface(GRASS, DIRT);
    	SurfaceRules.RuleSource gravelSurface = makeSurface(GRAVEL, GRAVEL);
    	SurfaceRules.RuleSource stoneSurface = makeSurface(STONE, STONE);
    	SurfaceRules.RuleSource frozenPeakSurface = makeSurface(PACKED_ICE, PACKED_ICE);
    	SurfaceRules.RuleSource forestSurface = makeForestSurface(noise);
    	
    	return SurfaceRules.sequence(
    		SurfaceRules.ifTrue(
    			SurfaceRules.abovePreliminarySurface(),
    			SurfaceRules.sequence(
    				SurfaceRules.ifTrue(
    					SurfaceRules.isBiome(Biomes.WINDSWEPT_GRAVELLY_HILLS),
    					SurfaceRules.sequence(
    						SurfaceRules.ifTrue(
    							RTFSurfaceConditions.isMoreSedimentThan(0.6F), 
    							gravelSurface
    						),
    						grassSurface
    					)
    	    		),
    				SurfaceRules.ifTrue(
    					SurfaceRules.isBiome(Biomes.WINDSWEPT_HILLS),
    					SurfaceRules.sequence(
    						SurfaceRules.ifTrue(
    							RTFSurfaceConditions.isMoreSedimentThan(2.5F), 
    							stoneSurface
        					),
    						grassSurface
        				)
        	    	),
	    			SurfaceRules.ifTrue(
	    				SurfaceRules.isBiome(Biomes.FROZEN_PEAKS), 
						SurfaceRules.sequence(
							frozenPeakSurface
	    				)
		    		),
	    			SurfaceRules.ifTrue(
	    				SurfaceRules.isBiome(Biomes.FOREST), 
	    				forestSurface
	    			)
//	    			SurfaceRules.ifTrue(
//	    				isDesert, 
//	    				desertSurface
//		    		)
	    		)
        	),
    		source,
    		makeStrata(miscellaneousSettings, noise)
        );
    }
    
    private static SurfaceRules.RuleSource makeForestSurface(HolderGetter<Noise> noise) {
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
    
    //TODO remove the desert stuff from ErodeFeature and move it here
//    private static SurfaceRules.RuleSource makeDesertSurface(Levels levels, HolderGetter<Noise> noise) {
//    	float minHeight = levels.ground(10);
//    	float threshold = levels.ground(40);
//    	
//    	SurfaceRules.RuleSource erosionMaterial
//    	
//    	return SurfaceRules.ifTrue(
//    		RTFSurfaceConditions.isSteeperThan(0.15F),
//    		SurfaceRules.ifTrue(
//    			RTFSurfaceConditions.isHigherThan(minHeight),
//    			erosionMaterial
//    		)
//    	);
//    }
    
    private static SurfaceRules.RuleSource makeSurface(SurfaceRules.RuleSource top, SurfaceRules.RuleSource under) {
    	return SurfaceRules.sequence(
    		SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, top),
    		SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, under)
    	);
    }
    
	private static SurfaceRules.RuleSource makeStrata(MiscellaneousSettings miscellaneousSettings, HolderGetter<Noise> noise) {
		Holder<Noise> depth = noise.getOrThrow(PresetStrataNoise.STRATA_DEPTH);
		
		List<Strata> strata = new ArrayList<>();
		strata.add(new Strata(RTFBlockTags.SOIL, depth, 3, 0, 1, 0.1F, 0.25F));
		strata.add(new Strata(RTFBlockTags.SEDIMENT, depth, 3, 0, 2, 0.05F, 0.15F));
		strata.add(new Strata(RTFBlockTags.CLAY, depth, 3, 0, 2, 0.05F, 0.1F));
		strata.add(new Strata(miscellaneousSettings.rockTag(), depth, 3, 10, 30, 0.1F, 1.5F));
		return RTFSurfaceRules.strata(RTFCommon.location("overworld_strata"), noise.getOrThrow(PresetStrataNoise.STRATA_SELECTOR), strata, 100);
	}
}
