package raccoonman.reterraforged.common.worldgen.data.preset;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.MultiContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.MultiImprovedContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.SingleContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

public enum ContinentType implements StringRepresentable {
    MULTI {
        
		@Override
		public Noise create(WorldSettings settings, Seed seed) {
            int tectonicScale = settings.continent.continentScale * 4;
            int continentScale = settings.continent.continentScale / 2;
            DistanceFunction distanceFunc = settings.continent.continentShape;
            float frequency = 1.0F / tectonicScale;
            float clampMin = 0.2F;
            float clampMax = 1.0F;
            float offsetAlpha = settings.continent.continentJitter;
            Domain warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, seed.next(), continentScale, 3, continentScale));
            Noise shape = Source.simplex(seed.next(), settings.continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
    		return new MultiContinent(warp, frequency, offsetAlpha, distanceFunc, clampMin, clampMax, settings.controlPoints.inland, shape);
		}
    }, 
    SINGLE {

		@Override
		public Noise create(WorldSettings settings, Seed seed) {
	        int tectonicScale = settings.continent.continentScale * 4;
	        int continentScale = settings.continent.continentScale / 2;
	        DistanceFunction distanceFunc = settings.continent.continentShape;
	        float frequency = 1.0F / tectonicScale;
	        float clampMin = 0.2F;
	        float clampMax = 1.0F;
	        float offsetAlpha = settings.continent.continentJitter;
	        Domain warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, seed.next(), continentScale, 3, continentScale));
	        Noise shape = Source.simplex(seed.next(), settings.continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
			return new SingleContinent(warp, frequency, offsetAlpha, distanceFunc, clampMin, clampMax, settings.controlPoints.inland, shape);
		}
    }, 
    MULTI_IMPROVED {

    	@Override
		public Noise create(WorldSettings settings, Seed seed) {
	        int tectonicScale = settings.continent.continentScale * 4;
	        float frequency = 1.0F / tectonicScale;
	        int baseSeed = seed.next();
	        int skippingSeed = seed.next();
	        int varianceSeed = seed.next();
	        float variance = settings.continent.continentSizeVariance;
	        int warpScale = NoiseUtil.round(tectonicScale * 0.225F);
	        double warpStrength = NoiseUtil.round(tectonicScale * 0.33F);
	        Domain warp = Domain.warp(
	        	Source.build(seed.next(), warpScale, settings.continent.continentNoiseOctaves)
	        	.gain(settings.continent.continentNoiseGain)
	        	.lacunarity(settings.continent.continentNoiseLacunarity)
	        	.build(Source.PERLIN2),
	        	Source.build(seed.next(), warpScale, settings.continent.continentNoiseOctaves)
	        	.gain(settings.continent.continentNoiseGain)
	        	.lacunarity(settings.continent.continentNoiseLacunarity)
	        	.build(Source.PERLIN2), 
	        	Source.constant(warpStrength)
	        );
	        Noise cliffNoise = Source.build(seed.next(), settings.continent.continentScale / 2, 2).build(Source.SIMPLEX2).clamp(0.1, 0.25).map(0.0, 1.0).freq(1.0F / frequency, 1.0F / frequency);
	        Noise bayNoise = Source.simplex(seed.next(), 100, 1).scale(0.1).bias(0.9).freq(1.0F / frequency, 1.0F / frequency);
	        return new MultiImprovedContinent(warp, frequency, variance, settings.continent.continentJitter, settings.continent.continentSkipping, settings.controlPoints.shallowOcean, settings.controlPoints.inland, cliffNoise, bayNoise, baseSeed, varianceSeed, skippingSeed);
		}
    };
	
	public static final Codec<ContinentType> CODEC = StringRepresentable.fromEnum(ContinentType::values);

    public abstract Noise create(WorldSettings settings, Seed seed);

	@Override
	public String getSerializedName() {
		return this.name();
	}
}
