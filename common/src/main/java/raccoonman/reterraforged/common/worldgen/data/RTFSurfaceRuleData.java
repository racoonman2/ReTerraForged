package raccoonman.reterraforged.common.worldgen.data;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.geology.Strata;
import raccoonman.reterraforged.common.level.levelgen.geology.Stratum;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.GeoSurfaceExtensionSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.SurfaceExtensionRuleSource;
import raccoonman.reterraforged.common.level.levelgen.surface.extension.SurfaceExtensionSource;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.tags.RTFBlockTags;

public class RTFSurfaceRuleData {

    public static SurfaceRules.RuleSource overworld(HolderGetter<DensityFunction> densityFunctions, Preset preset) {
        MiscellaneousSettings miscellaneous = preset.miscellaneous();
    	ImmutableList.Builder<SurfaceExtensionSource> extensions = ImmutableList.builder();
        if(miscellaneous.strataDecorator) {
            extensions.add(createGeoExtension(miscellaneous.strataRegionSize));
        }
//        extensions.add(new ErosionExtensionSource(densityFunctions.getOrThrow(RTFNoiseRouterData.HEIGHT)));
        return SurfaceRules.sequence(
        	SurfaceRuleData.overworld(),
    		new SurfaceExtensionRuleSource(extensions.build())
    	);
    }

    private static GeoSurfaceExtensionSource createGeoExtension(int scale) {
    	List<Strata> strata = new LinkedList<>();
    	for(int i = 0; i < 100; i++) {
    		Noise noise = Source.perlin(354215 + i, scale, 3);
        	strata.add(new Strata(ReTerraForged.resolve("strata-" + i), ImmutableList.of(
        		new Stratum(RTFBlockTags.SOIL, 0, 1, 0.1F, 0.25F, noise),
        		new Stratum(RTFBlockTags.SEDIMENT, 0, 2, 0.05F, 0.15F, noise),
        		new Stratum(RTFBlockTags.CLAY, 0, 2, 0.05F, 0.1F, noise),
        		new Stratum(RTFBlockTags.ROCK, 10, 30, 0.1F, 1.5F, noise)
        	)));
    	}
    	return new GeoSurfaceExtensionSource(strata, new NoiseWrapper.Marker(Source.cell(21345, scale).warp(213415, scale / 4, 2, scale / 2D).warp(421351, 15, 2, 30)));
    }
}

