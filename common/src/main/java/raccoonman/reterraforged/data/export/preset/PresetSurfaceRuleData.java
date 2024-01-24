package raccoonman.reterraforged.data.export.preset;

import java.util.ArrayList;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceRules.ConditionSource;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.data.preset.Preset;
import raccoonman.reterraforged.data.preset.WorldSettings;
import raccoonman.reterraforged.tags.RTFBlockTags;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.surface.condition.RTFSurfaceConditions;
import raccoonman.reterraforged.world.worldgen.surface.rule.RTFSurfaceRules;
import raccoonman.reterraforged.world.worldgen.surface.rule.StrataRule.Strata;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;

// maybe have a custom meadow or cherry forest surface ?
public class PresetSurfaceRuleData {
	private static final SurfaceRules.RuleSource PACKED_ICE = SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState());
	private static final SurfaceRules.RuleSource PODZOL = SurfaceRules.state(Blocks.PODZOL.defaultBlockState());
	private static final SurfaceRules.RuleSource GRASS = SurfaceRules.state(Blocks.GRASS_BLOCK.defaultBlockState());
	private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
	private static final SurfaceRules.RuleSource STONE = SurfaceRules.state(Blocks.STONE.defaultBlockState());
	private static final SurfaceRules.RuleSource GRAVEL = SurfaceRules.state(Blocks.GRAVEL.defaultBlockState());
	
    public static SurfaceRules.RuleSource overworld(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise) {
    	return makeSurface(preset, densityFunctions, noise, SurfaceRuleData.overworld());
    }
    
    public static SurfaceRules.RuleSource makeSurface(Preset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise, SurfaceRules.RuleSource ruleSource) {
    	WorldSettings worldSettings = preset.world();
    	WorldSettings.Properties properties = worldSettings.properties;
    	MiscellaneousSettings miscellaneousSettings = preset.miscellaneous();
    	Levels levels = new Levels(properties.terrainScaler(), properties.seaLevel);
    	
    	ConditionSource isForest = SurfaceRules.isBiome(Biomes.FOREST);
    	ConditionSource isFrozenPeak = SurfaceRules.isBiome(Biomes.FROZEN_PEAKS);
    	ConditionSource isDesert = SurfaceRules.isBiome(Biomes.DESERT);
    	ConditionSource isMountain = RTFSurfaceConditions.isTerrain(TerrainType.MOUNTAINS_1, TerrainType.MOUNTAINS_2, TerrainType.MOUNTAINS_3, TerrainType.MOUNTAIN_CHAIN);
    	ConditionSource isSediment = SurfaceRules.not(RTFSurfaceConditions.isMoreSedimentThan(0.4F));
    	ConditionSource isWindsweptForestOrHills = SurfaceRules.isBiome(Biomes.WINDSWEPT_FOREST, Biomes.WINDSWEPT_GRAVELLY_HILLS, Biomes.WINDSWEPT_HILLS);
    	return SurfaceRules.sequence(
    		SurfaceRules.ifTrue(
    			SurfaceRules.abovePreliminarySurface(),
    			SurfaceRules.sequence(
//    				SurfaceRules.ifTrue(
//    					isWindsweptForestOrHills,
//    					SurfaceRules.sequence(
//    						SurfaceRules.ifTrue(
//    							isSediment, 
//    							makeSedimentRule()
//    						),
//    						SurfaceRules.ifTrue(
//    							SurfaceRules.ON_FLOOR, 
//    							GRASS
//    						),
//    						SurfaceRules.ifTrue(
//    							SurfaceRules.UNDER_FLOOR, 
//    							DIRT
//    						)
//    					)
//    	    		),
	    			SurfaceRules.ifTrue(
	    				isForest, 
	    				makeForestRule(noise)
	    			),
	    			SurfaceRules.ifTrue(
	    				isFrozenPeak, 
	    				makeFrozenPeakRule()
	    			)
//	    			SurfaceRules.ifTrue(
//	    				isDesert, 
//	    				makeDesertRule(levels, noise)
//		    		)
	    		)
        	),
    		ruleSource,
    		makeStrataRule(miscellaneousSettings, noise)
        );
    }
    
    private static SurfaceRules.RuleSource makeForestRule(HolderGetter<Noise> noise) {
    	Holder<Noise> forestNoise = noise.getOrThrow(PresetSurfaceNoise.FOREST);
    	return SurfaceRules.ifTrue(
    		SurfaceRules.ON_FLOOR, 
    		RTFSurfaceRules.noise(
    			forestNoise, 
    			List.of(
    				Pair.of(0.65F, PODZOL),
    				Pair.of(0.725F, DIRT)
    			)
    		)	
    	);
    }
    
    //TODO remove the desert stuff from ErodeFeature and move it here
//    private static SurfaceRules.RuleSource makeDesertRule(Levels levels, HolderGetter<Noise> noise) {
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
    
    private static SurfaceRules.RuleSource makeFrozenPeakRule() {
    	return SurfaceRules.sequence(
			SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, PACKED_ICE),
			SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, PACKED_ICE)
    	);
    }
    
    private static SurfaceRules.RuleSource makeSedimentRule() {
    	return SurfaceRules.ifTrue(
    		SurfaceRules.not(RTFSurfaceConditions.isSteeperThan(0.6F)), 
    		SurfaceRules.sequence(
    			SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, GRAVEL),
    			SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, GRAVEL)
    		)
    	);
    }
    
	private static SurfaceRules.RuleSource makeStrataRule(MiscellaneousSettings miscellaneousSettings, HolderGetter<Noise> noise) {
		Holder<Noise> depth = noise.getOrThrow(PresetStrataNoise.STRATA_DEPTH);
		
		List<Strata> strata = new ArrayList<>();
		strata.add(new Strata(RTFBlockTags.SOIL, depth, 3, 0, 1, 0.1F, 0.25F));
		strata.add(new Strata(RTFBlockTags.SEDIMENT, depth, 3, 0, 2, 0.05F, 0.15F));
		strata.add(new Strata(RTFBlockTags.CLAY, depth, 3, 0, 2, 0.05F, 0.1F));
		strata.add(new Strata(miscellaneousSettings.rockTag(), depth, 3, 10, 30, 0.1F, 1.5F));
		return RTFSurfaceRules.strata(RTFCommon.location("overworld_strata"), noise.getOrThrow(PresetStrataNoise.STRATA_SELECTOR), strata, 100);
	}
}
