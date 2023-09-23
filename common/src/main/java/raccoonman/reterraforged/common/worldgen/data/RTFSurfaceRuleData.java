package raccoonman.reterraforged.common.worldgen.data;

import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.SurfaceRuleData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.geology.Strata;
import raccoonman.reterraforged.common.level.levelgen.geology.Stratum;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Rand;
import raccoonman.reterraforged.common.level.levelgen.rule.GeoRuleSource;
import raccoonman.reterraforged.common.level.levelgen.surface.condition.DensityThresholdCondition;
import raccoonman.reterraforged.common.worldgen.data.preset.MiscellaneousSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.tags.RTFBlockTags;

//TODO add terrablender compat (we'll probably just add it as a dependency so we can use it for our custom biomes too)
public final class RTFSurfaceRuleData {

	//TODO forest surface generation
	//TODO we also need to make sure that dirt properly generates on steepish ledges
	//TODO we need to add the option to prevent vanilla surface extensions from running
    public static SurfaceRules.RuleSource overworld(HolderGetter<DensityFunction> densityFunctions, Preset preset) {
        MiscellaneousSettings miscellaneous = preset.miscellaneous();
    	
//        ImmutableList.Builder<SurfaceExtensionSource> extensions = ImmutableList.builder();
    	Holder<DensityFunction> height = densityFunctions.getOrThrow(RTFNoiseRouterData.HEIGHT);
    	Holder<DensityFunction> slope = densityFunctions.getOrThrow(RTFNoiseRouterData.SLOPE);

    	int yScale = preset.terrain().general.yScale;
    	int seaLevel = preset.world().properties.seaLevel;
    	
        if(miscellaneous.strataDecorator) {
//            extensions.add(createGeoExtension(miscellaneous.strataRegionSize, yScale));
        }

//        ImmutableList.Builder<SurfaceExtensionSource> decorators = ImmutableList.builder();
//    	decorators.add(createErosionExtension(height, seaLevel, yScale, miscellaneous.naturalSnowDecorator));
        if(miscellaneous.smoothLayerDecorator) {
        	//TODO add one for powder snow
//        	decorators.add(new SmoothLayerSurfaceExtension(height, yScale, ImmutableList.of(new Layer(Blocks.SNOW_BLOCK, Blocks.SNOW, SnowLayerBlock.LAYERS))));
        }
        return SurfaceRules.sequence(
        	SurfaceRuleData.overworld(),
//        	createGeoRule(miscellaneous.strataRegionSize, yScale),
        	//TODO only apply this is we're on the surface (we can use the stoneDepth fields in Context)
        // can we apply the geology surface rule after erosion? that way we dont have to calculate geology twice
        	createErosionRule(height, slope)
//    		new SurfaceExtensionRuleSource(extensions.build(), decorators.build())
    	);
    }

    //FIXME we only apply this to rock
    //		idr but i dont think thats how 1.16 does it
    private static GeoRuleSource createGeoRule(int regionSize, int yScale) {
    	List<Strata> strata = new LinkedList<>();
    	for(int i = 0; i < 100; i++) {
    		Noise noise = Source.perlin(354215 + i, regionSize, 3);
        	strata.add(new Strata(ReTerraForged.resolve("strata-" + i), RTFBlockTags.ROCK, ImmutableList.of(
        		new Stratum(RTFBlockTags.SOIL, 0, 1, 0.1F, 0.25F, noise),
        		new Stratum(RTFBlockTags.SEDIMENT, 0, 2, 0.05F, 0.15F, noise),
        		new Stratum(RTFBlockTags.CLAY, 0, 2, 0.05F, 0.1F, noise),
        		new Stratum(RTFBlockTags.ROCK, 10, 30, 0.1F, 1.5F, noise)
        	)));
    	}
    	return new GeoRuleSource(strata, Holder.direct(new NoiseWrapper.Marker(Source.cell(21345, regionSize).warp(213415, regionSize / 4, 2, regionSize / 2D).warp(421351, 15, 2, 30))));
    }

    private static final float SLOPE_MODIFIER = 3F / 255F;
    //TODO don't apply this is we're in a wetland or river
    private static SurfaceRules.RuleSource createErosionRule(Holder<DensityFunction> height, Holder<DensityFunction> slope) {
    	double rockSteepness = 0.65F;
    	return SurfaceRules.ifTrue(
    		SurfaceRules.ON_FLOOR,
    		SurfaceRules.ifTrue(
    			new DensityThresholdCondition(Holder.direct(DensityFunctions.add(new DensityFunctions.HolderHolder(slope), new NoiseWrapper.Marker(new Rand(1.0F).mul(Source.constant(SLOPE_MODIFIER))))), rockSteepness),
    			SurfaceRules.state(Blocks.STONE.defaultBlockState())
        	) 
    	);
    }
//    
//    private static ErosionSurfaceExtensionSource createErosionExtension(Holder<DensityFunction> height, int seaLevel, int yScale, boolean erodeSnow) {
//    	List<MaterialSource> materials = new ArrayList<>();
//    	materials.add(new MaterialSource(0.65F, 30, 140, SurfaceRules.state(Blocks.STONE.defaultBlockState()))); //rock
//    	materials.add(new MaterialSource(0.47F, 40, 95,  SurfaceRules.state(Blocks.STONE.defaultBlockState()))); //dirt
//    	return new ErosionSurfaceExtensionSource(materials, height, seaLevel, yScale, 6.0F / 255F, 3.0F / 255F, erodeSnow);
//    }
}

