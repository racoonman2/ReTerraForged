package raccoonman.reterraforged.common.worldgen.data;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.ErosionRule;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.StrataRule;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.StrataRule.Strata;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.tags.RTFBlockTags;

// TODO port all the surfaces in SetupFactory
// TODO plain stone erosion
public final class RTFSurfaceRuleData {

    public static SurfaceRules.RuleSource overworld(HolderGetter<DensityFunction> densityFunctions, HolderGetter<Noise> noise, Preset preset) {
		MiscellaneousSettings miscellaneous = preset.miscellaneous();
		ImmutableList.Builder<SurfaceRules.RuleSource> builder = ImmutableList.builder();
		builder.add(SurfaceRuleData.overworld());
		
		if(miscellaneous.strataDecorator) {
			builder.add(makeStrataRule(noise));
		}
		builder.add(new ErosionRule(densityFunctions.getOrThrow(RTFNoiseRouterData.HEIGHT), densityFunctions.getOrThrow(RTFNoiseRouterData.STEEPNESS), densityFunctions.getOrThrow(RTFNoiseRouterData.SEDIMENT), RTFBlockTags.ERODIBLE, 1, 10.0F));
        return SurfaceRules.sequence(builder.build().toArray(SurfaceRules.RuleSource[]::new));
    }
	
	private static SurfaceRules.RuleSource makeStrataRule(HolderGetter<Noise> noise) {
		Holder<Noise> depth = noise.getOrThrow(StrataNoise.STRATA_DEPTH);
		
		List<Strata> strata = new ArrayList<>();
		strata.add(new Strata(RTFBlockTags.SOIL, depth, 3, 0, 1, 0.1F, 0.25F));
		strata.add(new Strata(RTFBlockTags.SEDIMENT, depth, 3, 0, 2, 0.05F, 0.15F));
		strata.add(new Strata(RTFBlockTags.CLAY, depth, 3, 0, 2, 0.05F, 0.1F));
		strata.add(new Strata(RTFBlockTags.ROCK, depth, 3, 10, 30, 0.1F, 1.5F));
		return new StrataRule(RTFRegistries.resolve("overworld_strata"), noise.getOrThrow(StrataNoise.STRATA_SELECTOR), strata, 100);
	}
}
