package raccoonman.reterraforged.common.registries.data.noise;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.Blender;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.AdvancedContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper2;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.ContinentLerper3;
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
	
	public static final ResourceKey<Noise> DEEP_OCEAN_POINT = resolve("continent/points/deep_ocean");
	public static final ResourceKey<Noise> SHALLOW_OCEAN_POINT = resolve("continent/points/shallow_ocean");
	public static final ResourceKey<Noise> COAST_POINT = resolve("continent/points/coast");
	public static final ResourceKey<Noise> INLAND_POINT = resolve("continent/points/inland");

	public static final ResourceKey<Noise> Y_SCALE = resolve("y_scale");
	public static final ResourceKey<Noise> SEA_LEVEL = resolve("sea_level");
	public static final ResourceKey<Noise> GROUND = resolve("ground");
	public static final ResourceKey<Noise> REGION_WARP_SCALE = resolve("region_warp_scale");
	public static final ResourceKey<Noise> REGION_WARP = resolve("region_warp");
	public static final ResourceKey<Noise> REGION_WARP_STRENGTH = resolve("region_warp_strength");
	public static final ResourceKey<Noise> REGION_SCALE = resolve("region_scale");
	public static final ResourceKey<Noise> REGION = resolve("region");
	public static final ResourceKey<Noise> REGION_EDGE = resolve("region_edge");
	public static final ResourceKey<Noise> REGION_SELECTOR = resolve("region_selector");
	public static final ResourceKey<Noise> REGION_LERPER = resolve("region_lerper");
	public static final ResourceKey<Noise> MOUNTAIN_SHAPE = resolve("mountain_shape");
	public static final ResourceKey<Noise> LAND = resolve("land");
	public static final ResourceKey<Noise> DEEP_OCEAN = resolve("deep_ocean");
	public static final ResourceKey<Noise> SHALLOW_OCEAN = resolve("shallow_ocean");
	public static final ResourceKey<Noise> COAST = resolve("coast");
	public static final ResourceKey<Noise> OCEANS = resolve("oceans");
	public static final ResourceKey<Noise> ROOT = resolve("root");
	public static final ResourceKey<Noise> HEIGHT = resolve("height");
	
	public static void register(BootstapContext<Noise> ctx) {
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		
		final int continentScale = 3000;
		final int tectonicScale = continentScale * 4;
		Noise continentFrequency = register(ctx, CONTINENT_FREQUENCY, Source.constant(1.0F / tectonicScale));
		Noise continentCliff = register(ctx, CONTINENT_CLIFF, createContinentCliff(continentScale, continentFrequency));
		Noise continentBay = register(ctx, CONTINENT_BAY, createContinentBay(continentFrequency));
		Noise continent = register(ctx, CONTINENT, createContinent(continentCliff, continentBay, tectonicScale, NoiseUtil.round(tectonicScale * 0.33F), continentFrequency));
		
		Noise deepOceanPoint = register(ctx, DEEP_OCEAN_POINT, Source.constant(0.1F));
		Noise shallowOceanPoint = register(ctx, SHALLOW_OCEAN_POINT, Source.constant(0.25F));
		Noise coastPoint = register(ctx, COAST_POINT, Source.constant(0.448F));
		Noise inlandPoint = register(ctx, INLAND_POINT, Source.constant(0.502F));
		
		Noise worldHeight = register(ctx, Y_SCALE, Source.constant(256));
		Noise seaLevel = register(ctx, SEA_LEVEL, Source.constant(63));

		Noise regionWarpScale = register(ctx, REGION_WARP_SCALE, Source.constant(400.0D));
		Noise regionWarp = register(ctx, REGION_WARP, createRegionWarp(regionWarpScale));
		Noise regionWarpStrength = register(ctx, REGION_WARP_STRENGTH, Source.constant(200.0D));
		Noise regionScale = register(ctx, REGION_SCALE, Source.constant(1200));
		Noise region = register(ctx, REGION, createRegion(regionWarp, regionWarpStrength, regionScale));
		Noise regionEdge = register(ctx, REGION_EDGE, createRegionEdge(regionWarp, regionWarpStrength, regionScale));
		Noise regionSelector = register(ctx, REGION_SELECTOR, new RegionSelector(region, createTerrain(ctx, regionScale)));
		Noise regionLerper = register(ctx, REGION_LERPER, new RegionLerper(regionEdge, new HolderNoise(noise.getOrThrow(PLAINS)), regionSelector));
		Noise mountainShape = register(ctx, MOUNTAIN_SHAPE, createMountainShape());
		Noise land = register(ctx, LAND, new Blender(mountainShape, regionLerper, new HolderNoise(noise.getOrThrow(MOUNTAINS)), 0.3f, 0.8f, 0.575f, Interpolation.LINEAR));
		Noise deepOcean = register(ctx, DEEP_OCEAN, RTFLandForms.deepOcean(seaLevel.div(worldHeight)));
		Noise shallowOcean = register(ctx, SHALLOW_OCEAN, seaLevel.min(Source.constant(7)).div(worldHeight));
		Noise coast = register(ctx, COAST, seaLevel.div(worldHeight));
		Noise oceans = register(ctx, OCEANS, new ContinentLerper3(continent, deepOcean, shallowOcean, coast, deepOceanPoint, shallowOceanPoint, coastPoint, Interpolation.CURVE3));
        Noise root = register(ctx, ROOT, new ContinentLerper2(continent, oceans, land, shallowOceanPoint, inlandPoint, Interpolation.LINEAR));

		//TODO this needs to be scaled correctly
		Noise height = register(ctx, HEIGHT, root.mul(worldHeight));
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
	
	private static final ResourceKey<Noise> STEPPE = resolve("land_forms/steppe");
	private static final ResourceKey<Noise> PLAINS = resolve("land_forms/plains");
	private static final ResourceKey<Noise> DALES = resolve("land_forms/dales");
	private static final ResourceKey<Noise> HILLS_1 = resolve("land_forms/hills_1");
	private static final ResourceKey<Noise> HILLS_2 = resolve("land_forms/hills_2");
	private static final ResourceKey<Noise> TORRIDONIAN = resolve("land_forms/torridonian");
	private static final ResourceKey<Noise> PLATEAU = resolve("land_forms/plateau");
	private static final ResourceKey<Noise> BADLANDS = resolve("land_forms/badlands");
	private static final ResourceKey<Noise> MOUNTAINS = resolve("land_forms/mountains");
	private static final ResourceKey<Noise> MOUNTAINS_2 = resolve("land_forms/mountains_2");
	private static final ResourceKey<Noise> MOUNTAINS_3 = resolve("land_forms/mountains_3");
	private static WeightMap<Noise> createTerrain(BootstapContext<Noise> ctx, Noise regionScale) {
		Noise ground = register(ctx, GROUND, Source.constant(63.0F / 256.0F));
		List<WeightedLandForm> landForms = new ArrayList<>();
		landForms.add(new WeightedLandForm(1.0F, register(ctx, STEPPE, new LandForm(ground, RTFLandForms.steppe()))));
		landForms.add(new WeightedLandForm(2.0F, register(ctx, PLAINS, new LandForm(ground, RTFLandForms.plains()))));
		landForms.add(new WeightedLandForm(1.5F, register(ctx, DALES, new LandForm(ground, RTFLandForms.dales()))));
		landForms.add(new WeightedLandForm(2.0F, register(ctx, HILLS_1, new LandForm(ground, RTFLandForms.hills1()))));
		landForms.add(new WeightedLandForm(2.0F, register(ctx, HILLS_2, new LandForm(ground, RTFLandForms.hills2()))));
		landForms.add(new WeightedLandForm(2.0F, register(ctx, TORRIDONIAN, new LandForm(ground, RTFLandForms.torridonian()))));
		landForms.add(new WeightedLandForm(1.5F, register(ctx, PLATEAU, new LandForm(ground, RTFLandForms.plateau()))));
		Noise badlands = register(ctx, BADLANDS, new LandForm(ground, RTFLandForms.badlands()));
		landForms.add(new WeightedLandForm(1.0F, badlands));
		landForms = combine(landForms, (tp1, tp2) -> combine(ground, tp1, tp2, regionScale));
		landForms.add(new WeightedLandForm(1.0F, badlands));
		landForms.add(new WeightedLandForm(2.5F, register(ctx, MOUNTAINS, new LandForm(ground, RTFLandForms.mountains()))));
		landForms.add(new WeightedLandForm(2.5F, register(ctx, MOUNTAINS_2, new LandForm(ground, RTFLandForms.mountains2()))));
		landForms.add(new WeightedLandForm(2.5F, register(ctx, MOUNTAINS_3, new LandForm(ground, RTFLandForms.mountains3()))));
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
