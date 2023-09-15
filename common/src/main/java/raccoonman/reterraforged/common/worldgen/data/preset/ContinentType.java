package raccoonman.reterraforged.common.worldgen.data.preset;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.continent.MultiImprovedContinent;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

@Deprecated //TODO: remove this and replace the setting with a noise resource key
public enum ContinentType implements StringRepresentable {
    MULTI {
        
		@Override
		public Noise create(WorldSettings settings, Seed seed) {
			throw new UnsupportedOperationException("TODO");
		}
    }, 
    SINGLE {

		@Override
		public Noise create(WorldSettings settings, Seed seed) {
			throw new UnsupportedOperationException("TODO");
		}
    }, 
    MULTI_IMPROVED {

    	@Override
		public Noise create(WorldSettings settings, Seed seed) {
            final int tectonicScale = settings.continent.continentScale * 4;
            final int continentScale = settings.continent.continentScale / 2;
            Domain warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, seed.next(), continentScale, 3, continentScale));
            float frequency = 1.0f / tectonicScale;
            float clampMin = 0.2f;
            float clampMax = 1.0f;
            float jitter = settings.continent.continentJitter;
            Noise shape = Source.simplex(seed.next(), settings.continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
			return new MultiImprovedContinent(warp, frequency, settings.continent.continentShape, jitter, clampMin, clampMax, settings.controlPoints.inland, shape);
		}
    }, 
    EXPERIMENTAL {
        
		@Override
		public Noise create(WorldSettings settings, Seed seed) {
			throw new UnsupportedOperationException("TODO");
		}
    };
	
	public static final Codec<ContinentType> CODEC = StringRepresentable.fromEnum(ContinentType::values);

    public abstract Noise create(WorldSettings settings, Seed seed);

	@Override
	public String getSerializedName() {
		return this.name();
	}
}
