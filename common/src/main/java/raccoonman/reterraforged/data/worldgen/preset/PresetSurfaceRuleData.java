package raccoonman.reterraforged.data.worldgen.preset;

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
import raccoonman.reterraforged.data.worldgen.preset.settings.WorldPreset;
import raccoonman.reterraforged.tags.RTFBlockTags;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.surface.rule.RTFSurfaceRules;
import raccoonman.reterraforged.world.worldgen.surface.rule.StrataRule.Strata;

//TODO add forest surfaces
// maybe have a custom meadow or cherry forest surface ?
public class PresetSurfaceRuleData {
	private static final SurfaceRules.RuleSource PACKED_ICE = SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState());
	private static final SurfaceRules.RuleSource PODZOL = SurfaceRules.state(Blocks.PODZOL.defaultBlockState());
	private static final SurfaceRules.RuleSource DIRT = SurfaceRules.state(Blocks.DIRT.defaultBlockState());
	
    public static SurfaceRules.RuleSource overworld(WorldPreset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise) {
    	return makeSurface(preset, densityFunctions, noise, SurfaceRuleData.overworld());
    }
    
    public static SurfaceRules.RuleSource makeSurface(WorldPreset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise, SurfaceRules.RuleSource ruleSource) {
    	ConditionSource isForest = SurfaceRules.isBiome(Biomes.FOREST);
    	ConditionSource isFrozenPeak = SurfaceRules.isBiome(Biomes.FROZEN_PEAKS);
    	return SurfaceRules.sequence(
    		SurfaceRules.ifTrue(
    			SurfaceRules.abovePreliminarySurface(),
    			SurfaceRules.sequence(
	    			SurfaceRules.ifTrue(
	    				isForest, 
	    				makeForestRule(noise)
	    			),
	    			SurfaceRules.ifTrue(
	    				isFrozenPeak, 
	    				makeFrozenPeakRule()
	    			)
	    		)
        	),
    		ruleSource,
    		makeStrataRule(noise)
        );
    }
    
    private static SurfaceRules.RuleSource makeForestRule(HolderGetter<Noise> noise) {
    	Holder<Noise> forestNoise = noise.getOrThrow(PresetSurfaceNoise.FOREST);
    	return RTFSurfaceRules.noise(forestNoise, List.of(
    		Pair.of(0.65F, PODZOL),
    		Pair.of(0.725F, DIRT)
    	));
    }
    
    //TODO remove the desert stuff from ErodeFeature and move it here
    private static SurfaceRules.RuleSource makeDesertRule(HolderGetter<Noise> noise) {

    	return null;
    }
    
    private static SurfaceRules.RuleSource makeFrozenPeakRule() {
    	return SurfaceRules.sequence(
			SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, PACKED_ICE),
			SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, PACKED_ICE)
    	);
    }
    
	private static SurfaceRules.RuleSource makeStrataRule(HolderGetter<Noise> noise) {
		Holder<Noise> depth = noise.getOrThrow(PresetStrataNoise.STRATA_DEPTH);
		
		List<Strata> strata = new ArrayList<>();
		strata.add(new Strata(RTFBlockTags.SOIL, depth, 3, 0, 1, 0.1F, 0.25F));
		strata.add(new Strata(RTFBlockTags.SEDIMENT, depth, 3, 0, 2, 0.05F, 0.15F));
		strata.add(new Strata(RTFBlockTags.CLAY, depth, 3, 0, 2, 0.05F, 0.1F));
		strata.add(new Strata(RTFBlockTags.ROCK, depth, 3, 10, 30, 0.1F, 1.5F));
		return RTFSurfaceRules.strata(RTFCommon.location("overworld_strata"), noise.getOrThrow(PresetStrataNoise.STRATA_SELECTOR), strata, 100);
	}
}
