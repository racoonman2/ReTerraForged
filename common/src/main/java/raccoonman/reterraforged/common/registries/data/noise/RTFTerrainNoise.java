package raccoonman.reterraforged.common.registries.data.noise;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.Blender;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.AdvancedContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.LandForm;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionEdge;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionId;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionLerper;
import raccoonman.reterraforged.common.level.levelgen.noise.terrain.region.RegionSelector;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.storage.WeightMap;

public final class RTFTerrainNoise {
	public static final ResourceKey<Noise> CONTINENT_FREQUENCY = resolve("continent_frequency");
	public static final ResourceKey<Noise> CONTINENT_CLIFF = resolve("continent_cliff");
	public static final ResourceKey<Noise> CONTINENT_BAY = resolve("continent_bay");
	public static final ResourceKey<Noise> CONTINENT = resolve("continent");
	
	public static final ResourceKey<Noise> GROUND = resolve("ground");
	public static final ResourceKey<Noise> REGION_WARP_SCALE = resolve("region_warp_scale");
	public static final ResourceKey<Noise> REGION_WARP = resolve("region_warp");
	public static final ResourceKey<Noise> REGION_WARP_STRENGTH = resolve("region_warp_strength");
	public static final ResourceKey<Noise> REGION_SCALE = resolve("region_scale");
	public static final ResourceKey<Noise> REGION = resolve("region");
	public static final ResourceKey<Noise> REGION_EDGE = resolve("region_edge");
	public static final ResourceKey<Noise> REGION_SELECTOR = resolve("region_selector");
	public static final ResourceKey<Noise> REGION_BORDERS = resolve("region_borders");
	public static final ResourceKey<Noise> REGION_LERPER = resolve("region_lerper");
	public static final ResourceKey<Noise> MOUNTAIN_SHAPE = resolve("mountain_shape");
	public static final ResourceKey<Noise> MOUNTAINS = resolve("mountains");
	public static final ResourceKey<Noise> LAND = resolve("land");
	public static final ResourceKey<Noise> OCEAN = resolve("ocean");
	public static final ResourceKey<Noise> ROOT = resolve("root");
	public static final ResourceKey<Noise> HEIGHT = resolve("height");
	
	public static void register(BootstapContext<Noise> ctx) {
		final int continentScale = 3000;
		final int tectonicScale = continentScale * 4;
		Noise continentFrequency = register(ctx, CONTINENT_FREQUENCY, Source.constant(1.0F / tectonicScale));
		Noise continentCliff = register(ctx, CONTINENT_CLIFF, createContinentCliff(continentScale, continentFrequency));
		Noise continentBay = register(ctx, CONTINENT_BAY, createContinentBay(continentFrequency));
		Noise continent = register(ctx, CONTINENT, createContinent(continentCliff, continentBay, tectonicScale, NoiseUtil.round(tectonicScale * 0.33F), continentFrequency));
		
		Noise mountainShape = register(ctx, MOUNTAIN_SHAPE, createMountainShape());
		Noise ground = register(ctx, GROUND, Source.constant(63.0F / 256.0F));
		
		Noise regionWarpScale = register(ctx, REGION_WARP_SCALE, Source.constant(400.0D));
		Noise regionWarp = register(ctx, REGION_WARP, createRegionWarp(regionWarpScale));
		Noise regionWarpStrength = register(ctx, REGION_WARP_STRENGTH, Source.constant(200.0D));
		Noise regionScale = register(ctx, REGION_SCALE, Source.constant(1200));
		Noise region = register(ctx, REGION, createRegion(regionWarp, regionWarpStrength, regionScale));
		Noise regionEdge = register(ctx, REGION_EDGE, createRegionEdge(regionWarp, regionWarpStrength, regionScale));
		Noise regionSelector = register(ctx, REGION_SELECTOR, new RegionSelector(region, createTerrain(ctx, ground, regionScale)));
        Noise regionBorders = register(ctx, REGION_BORDERS, new LandForm(ground, RTFLandForms.plains()));
		Noise regionLerper = register(ctx, REGION_LERPER, new RegionLerper(regionEdge, regionBorders, regionSelector));
		Noise mountains = register(ctx, MOUNTAINS, new LandForm(ground, RTFLandForms.mountains()));
		Noise land = register(ctx, LAND, new Blender(mountainShape, regionLerper, mountains, 0.3f, 0.8f, 0.575f, Interpolation.LINEAR));
		// this needs to be scaled correctly
		Noise height = register(ctx, HEIGHT, land.mul(Source.constant(256.0D)));
	}
	
	private static Noise createContinentCliff(int continentScale, Noise frequency) {
		return Source.build(continentScale / 2, 2).build(Source.SIMPLEX).clamp(0.1, 0.25).map(0.0, 1.0).freq(Source.constant(1.0D).div(frequency), Source.constant(1.0D).div(frequency));
	}
	
	private static Noise createContinentBay(Noise frequency) {
		return Source.simplex(100, 1).scale(0.1).bias(0.9).freq(Source.constant(1.0D).div(frequency), Source.constant(1.0D).div(frequency));
	}

	private static Noise createContinent(Noise cliff, Noise bay, int tectonicScale, float strength, Noise frequency) {
		Domain warp = createWarp(tectonicScale);
		return new AdvancedContinent(0.25F, 0.7F, 0.502F, 0.25F, cliff, bay).freq(frequency, frequency).warp(warp);
	}
	
	protected static Domain createWarp(final int tectonicScale) {
        final int warpScale = NoiseUtil.round(tectonicScale * 0.225f);
        final double strength = NoiseUtil.round(tectonicScale * 0.33f);
        int continentNoiseOctaves = 5;
        float continentNoiseGain = 0.26F;
        float continentNoiseLacunarity = 4.33F;
        return Domain.warp(
        	Source.build(warpScale, continentNoiseOctaves).gain(continentNoiseGain).lacunarity(continentNoiseLacunarity).build(Source.PERLIN), 
        	Source.build(warpScale, continentNoiseOctaves).gain(continentNoiseGain).lacunarity(continentNoiseLacunarity).build(Source.PERLIN), 
        	Source.constant(strength)
        );
    }
	
	private static Noise createMountainShape() {
        Noise mountainShapeBase = Source.cellEdge(1000, EdgeFunction.DISTANCE_2_ADD).warp(333, 2, 250.0).shift(12345);
        return mountainShapeBase.curve(Interpolation.CURVE3).clamp(0.0, 0.9).map(0.0, 1.0);
	}

	private static Noise createRegionWarp(Noise scale) {
		return Source.builder().octaves(1).legacySimplex().freq(Source.constant(1.0D).div(scale), Source.constant(1.0D).div(scale));
	}
	
	private static Noise createRegion(Noise warp, Noise warpStrength, Noise scale) {
		return new RegionId(DistanceFunction.NATURAL, 0.7F).freq(Source.constant(1.0D).div(scale), Source.constant(1.0D).div(scale)).warp(Domain.warp(warp, warp, warpStrength));
	}
	
	private static Noise createRegionEdge(Noise warp, Noise warpStrength, Noise scale) {
		return new RegionEdge(0.0F, 0.5F, EdgeFunction.DISTANCE_2_DIV, DistanceFunction.NATURAL, 0.7F).freq(Source.constant(1.0D).div(scale), Source.constant(1.0D).div(scale)).warp(Domain.warp(warp, warp, warpStrength));
	}
	
	private static WeightMap<Noise> createTerrain(BootstapContext<Noise> ctx, Noise ground, Noise regionScale) {
		List<WeightedLandForm> landForms = new ArrayList<>();
		landForms.add(new WeightedLandForm(1.0F, new LandForm(ground, RTFLandForms.steppe())));
		landForms.add(new WeightedLandForm(2.0F, new LandForm(ground, RTFLandForms.plains())));
		landForms.add(new WeightedLandForm(1.5F, new LandForm(ground, RTFLandForms.dales())));
		landForms.add(new WeightedLandForm(2.0F, new LandForm(ground, RTFLandForms.hills1())));
		landForms.add(new WeightedLandForm(2.0F, new LandForm(ground, RTFLandForms.hills2())));
		landForms.add(new WeightedLandForm(2.0F, new LandForm(ground, RTFLandForms.torridonian())));
		landForms.add(new WeightedLandForm(1.5F, new LandForm(ground, RTFLandForms.plateau())));
		landForms.add(new WeightedLandForm(1.0F, new LandForm(ground, RTFLandForms.badlands())));
		landForms = combine(landForms, (tp1, tp2) -> combine(ground, tp1, tp2, regionScale));
		landForms.add(new WeightedLandForm(1.0F, new LandForm(ground, RTFLandForms.badlands())));
		landForms.add(new WeightedLandForm(2.5F, new LandForm(ground, RTFLandForms.mountains())));
		landForms.add(new WeightedLandForm(2.5F, new LandForm(ground, RTFLandForms.mountains2())));
		landForms.add(new WeightedLandForm(2.5F, new LandForm(ground, RTFLandForms.mountains3())));
		return WeightedLandForm.build(landForms);
	}
	
	private static WeightedLandForm combine(Noise ground, final WeightedLandForm tp1, final WeightedLandForm tp2, final Noise scale) {
		final Noise combined = Source.builder().octaves(1).perlin().freq(Source.constant(1.0F).div(scale), Source.constant(1.0F).div(scale))
				.warp(
					Source.builder().octaves(2).perlin().freq(Source.constant(1.0F).div(scale.div(Source.constant(2))), Source.constant(1.0F).div(scale.div(Source.constant(2)))),
					Source.builder().octaves(2).perlin().freq(Source.constant(1.0F).div(scale.div(Source.constant(2))), Source.constant(1.0F).div(scale.div(Source.constant(2)))).shift(1),
					scale.div(Source.constant(2.0D))
				).blend(tp1.terrain(), tp2.terrain(), 0.5, 0.25).clamp(0.0, 1.0);
		final float weight = (tp1.weight() + tp2.weight()) / 2.0F;
		return new WeightedLandForm(weight, new LandForm(ground, combined));
	}
	
	private record WeightedLandForm(float weight, Noise terrain) {
		
		public static WeightMap<Noise> build(List<WeightedLandForm> terrainTypes) {
			WeightMap.Builder<Noise> builder = new WeightMap.Builder<>();
			for(WeightedLandForm terrain : terrainTypes) {
				builder.entry(terrain.weight(), terrain.terrain());
			}
			return builder.build();
		}
	}
	
	private static <T> List<T> combine(final List<T> input, final BiFunction<T, T, T> operator) {
        int length = input.size();
        for (int i = 1; i < input.size(); ++i) {
            length += input.size() - i;
        }
        final List<T> result = new ArrayList<T>(length);
        for (int j = 0; j < length; ++j) {
            result.add(null);
        }
        int j = 0;
        int k = input.size();
        while (j < input.size()) {
            final T t1 = input.get(j);
            result.set(j, t1);
            for (int l = j + 1; l < input.size(); ++l, ++k) {
                final T t2 = input.get(l);
                final T t3 = operator.apply(t1, t2);
                result.set(k, t3);
            }
            ++j;
        }
        return result;
    }
	
	private static Noise register(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise value) {
		return new HolderNoise(ctx.register(key, value));
	}
	
	private static ResourceKey<Noise> resolve(String path) {
		return ReTerraForged.resolve(RTFRegistries.NOISE, "terrain/" + path);
	}
}
