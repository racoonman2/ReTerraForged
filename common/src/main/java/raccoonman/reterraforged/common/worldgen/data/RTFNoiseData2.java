package raccoonman.reterraforged.common.worldgen.data;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.HolderNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.MultiContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.MultiImprovedContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.SingleContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;
import raccoonman.reterraforged.common.worldgen.data.preset.TerrainSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.Continent;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.ControlPoints;

public final class RTFNoiseData2 {
	public static final ResourceKey<Noise> MULTI_CONTINENT = createKey("continent/multi");
	public static final ResourceKey<Noise> SINGLE_CONTINENT = createKey("continent/single");
	public static final ResourceKey<Noise> MULTI_IMPROVED_CONTINENT = createKey("continent/multi_improved");
	public static final ResourceKey<Noise> EXPERIMENTAL_CONTINENT = createKey("continent/experimental");
	
	public static void bootstrap(BootstapContext<Noise> ctx, Preset preset) {
		HolderGetter<Noise> noise = ctx.lookup(RTFRegistries.NOISE);
		
		Seed seed = new Seed(0);
		
		WorldSettings world = preset.world();
		ControlPoints controlPoints = world.controlPoints;
		
		TerrainSettings terrain = preset.terrain();
		Seed regionSeed = seed.offset(789124);
		Seed regionWarpSeed = seed.offset(8934);
		int regionWarpScale = 400;
		int regionWarpStrength = 200;
		Noise regionWarpX = Source.simplex(regionWarpSeed.next(), regionWarpScale, 1);
		Noise regionWarpZ = Source.simplex(regionWarpSeed.next(), regionWarpScale, 1);
		float terrainFrequency = 1.0F / terrain.general.globalHorizontalScale;
		
		registerContinents(ctx, preset, seed);
	}
	
	private static Noise createMulti(Continent continent, ControlPoints controlPoints, Seed seed) {
        int tectonicScale = continent.continentScale * 4;
        int continentScale = continent.continentScale / 2;
        DistanceFunction distanceFunc = continent.continentShape;
        float frequency = 1.0F / tectonicScale;
        float clampMin = 0.2F;
        float clampMax = 1.0F;
        float offsetAlpha = continent.continentJitter;
        Domain warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, seed.next(), continentScale, 3, continentScale));
        Noise shape = Source.simplex(seed.next(), continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
		return new MultiContinent(warp, frequency, offsetAlpha, distanceFunc, clampMin, clampMax, controlPoints.inland, shape);
	}
	
	private static Noise createMultiImproved(Continent continent, ControlPoints controlPoints, Seed seed) {
        int tectonicScale = continent.continentScale * 4;
        float frequency = 1.0F / tectonicScale;
        int baseSeed = seed.next();
        int skippingSeed = seed.next();
        int varianceSeed = seed.next();
        float variance = continent.continentSizeVariance;
        int warpScale = NoiseUtil.round(tectonicScale * 0.225F);
        double warpStrength = NoiseUtil.round(tectonicScale * 0.33F);
        Domain warp = Domain.warp(
        	Source.build(seed.next(), warpScale, continent.continentNoiseOctaves)
        	.gain(continent.continentNoiseGain)
        	.lacunarity(continent.continentNoiseLacunarity)
        	.build(Source.PERLIN2),
        	Source.build(seed.next(), warpScale, continent.continentNoiseOctaves)
        	.gain(continent.continentNoiseGain)
        	.lacunarity(continent.continentNoiseLacunarity)
        	.build(Source.PERLIN2), 
        	Source.constant(warpStrength)
        );
        Noise cliffNoise = Source.build(seed.next(), continent.continentScale / 2, 2).build(Source.SIMPLEX2).clamp(0.1, 0.25).map(0.0, 1.0).freq(1.0F / frequency, 1.0F / frequency);
        Noise bayNoise = Source.simplex(seed.next(), 100, 1).scale(0.1).bias(0.9).freq(1.0F / frequency, 1.0F / frequency);
        return new MultiImprovedContinent(warp, frequency, variance, continent.continentJitter, continent.continentSkipping, controlPoints.shallowOcean, controlPoints.inland, cliffNoise, bayNoise, baseSeed, varianceSeed, skippingSeed);
	}

	private static Noise createSingle(Continent continent, ControlPoints controlPoints, Seed seed) {
        int tectonicScale = continent.continentScale * 4;
        int continentScale = continent.continentScale / 2;
        DistanceFunction distanceFunc = continent.continentShape;
        float frequency = 1.0F / tectonicScale;
        float clampMin = 0.2F;
        float clampMax = 1.0F;
        float offsetAlpha = continent.continentJitter;
        Domain warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, seed.next(), continentScale, 3, continentScale));
        Noise shape = Source.simplex(seed.next(), continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
		return new SingleContinent(warp, frequency, offsetAlpha, distanceFunc, clampMin, clampMax, controlPoints.inland, shape);
	}
	
	private static void registerContinents(BootstapContext<Noise> ctx, Preset preset, Seed seed) {
		WorldSettings worldSettings = preset.world();
		ctx.register(MULTI_CONTINENT, createMulti(worldSettings.continent, worldSettings.controlPoints, seed));
		ctx.register(MULTI_IMPROVED_CONTINENT, createMultiImproved(worldSettings.continent, worldSettings.controlPoints, seed));
		ctx.register(SINGLE_CONTINENT, createSingle(worldSettings.continent, worldSettings.controlPoints, seed));
	}
	
	private static Noise getNoise(HolderGetter<Noise> getter, ResourceKey<Noise> key) {
		return new HolderNoise(getter.getOrThrow(key));
	}
	
	private static Noise register(BootstapContext<Noise> ctx, ResourceKey<Noise> key, Noise value) {
		return new HolderNoise(ctx.register(key, value));
	}
	
	private static ResourceKey<Noise> createKey(String string) {
        return ResourceKey.create(RTFRegistries.NOISE, ReTerraForged.resolve(string));
    }
	    
}
