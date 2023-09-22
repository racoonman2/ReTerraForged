package raccoonman.reterraforged.common.worldgen.data.noise;

import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.MultiContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.MultiImprovedContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.SingleContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.Continent;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings.ControlPoints;

public final class RTFContinentNoise {
	public static final ResourceKey<Noise> MULTI = createKey("multi");
	public static final ResourceKey<Noise> SINGLE = createKey("single");
	public static final ResourceKey<Noise> MULTI_IMPROVED = createKey("multi_improved");
	public static final ResourceKey<Noise> EXPERIMENTAL = createKey("experimental");
	
	public static void bootstrap(BootstapContext<Noise> ctx, WorldSettings settings, Seed seed) {
		ctx.register(MULTI, createMulti(settings.continent, settings.controlPoints, seed));
		ctx.register(MULTI_IMPROVED, createMultiImproved(settings.continent, settings.controlPoints, seed));
		ctx.register(SINGLE, createSingle(settings.continent, settings.controlPoints, seed));
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
	
	private static Noise createExperimental() {
		throw new UnsupportedOperationException(); //TODO
	}
	
	private static ResourceKey<Noise> createKey(String string) {
		return RTFNoiseData.createKey("continent/" + string);
	}
}
