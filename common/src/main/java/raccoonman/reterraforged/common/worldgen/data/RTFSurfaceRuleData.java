package raccoonman.reterraforged.common.worldgen.data;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.ErosionExtensionSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.ExtensionRuleSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.geology.StrataExtensionSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.geology.StrataGenerator;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.tags.RTFBlockTags;

public class RTFSurfaceRuleData {

    public static SurfaceRules.RuleSource overworld(HolderGetter<DensityFunction> densityFunctions, Preset preset) {
        MiscellaneousSettings miscellaneous = preset.miscellaneous();
    	ImmutableList.Builder<ExtensionRuleSource.ExtensionSource> filters = ImmutableList.builder();
        if(miscellaneous.strataDecorator) {
            filters.add(createStrataExtension(miscellaneous.strataRegionSize));
        }
        filters.add(new ErosionExtensionSource(densityFunctions.getOrThrow(RTFNoiseRouterData.HEIGHT)));
        return SurfaceRules.sequence(
        	SurfaceRuleData.overworld(),
    		new ExtensionRuleSource(filters.build())
    	);
    }

    private static ExtensionRuleSource.ExtensionSource createStrataExtension(int scale) {
        DensityFunction selector = new NoiseWrapper.Marker(Source.cell(scale)
        	.warp(1, scale / 4, 2, scale / 2.0D)
        	.warp(2, 15, 2, 30));
    	Noise noise = Source.build(0, scale, 3).build(Source.PERLIN);
        int seed = 342135;
    	return new StrataExtensionSource(Holder.direct(selector), new StrataGenerator(ImmutableList.of(
        	new StrataGenerator.Layer(RTFBlockTags.SOIL, seed, 3, noise, 0, 2, 0.1F, 0.25F),
        	new StrataGenerator.Layer(RTFBlockTags.SEDIMENT, seed, 3, noise, 0, 2, 0.05F, 0.15F),
        	new StrataGenerator.Layer(RTFBlockTags.CLAY, seed, 3, noise, 0, 2, 0.05F, 0.1F),
        	new StrataGenerator.Layer(RTFBlockTags.ROCK, seed, 3, noise, 10, 30, 0.1F, 1.5F)
        ), 100));
    }
}

