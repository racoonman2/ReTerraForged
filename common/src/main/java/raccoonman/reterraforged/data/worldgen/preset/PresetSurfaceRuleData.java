package raccoonman.reterraforged.data.worldgen.preset;

import java.util.ArrayList;
import java.util.List;

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
	public static final SurfaceRules.RuleSource PACKED_ICE = SurfaceRules.state(Blocks.PACKED_ICE.defaultBlockState());
	
    public static SurfaceRules.RuleSource overworld(WorldPreset preset, HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise) {
    	ConditionSource isFrozenPeak = SurfaceRules.isBiome(Biomes.FROZEN_PEAKS);
    	return SurfaceRules.sequence(
    		SurfaceRules.ifTrue(
    			SurfaceRules.abovePreliminarySurface(),
    			SurfaceRules.ifTrue(
    				isFrozenPeak, 
    				SurfaceRules.sequence(
    					SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, PACKED_ICE),
    					SurfaceRules.ifTrue(SurfaceRules.UNDER_FLOOR, PACKED_ICE),
    					SurfaceRules.ifTrue(SurfaceRules.DEEP_UNDER_FLOOR, PACKED_ICE)
    				)
    			)
    		),
    		SurfaceRuleData.overworld()
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
