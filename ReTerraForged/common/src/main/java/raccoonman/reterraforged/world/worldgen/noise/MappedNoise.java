package raccoonman.reterraforged.world.worldgen.noise;

import com.mojang.serialization.MapCodec;

public interface MappedNoise extends Noise {
	
	@Override
	default MapCodec<? extends Marker> codec() {
		throw new UnsupportedOperationException();
	}

	@Override
	default Noise mapAll(Visitor visitor) {
		return visitor.apply(this);
	}
	
	public interface Marker extends Noise {

		@Override
		default float compute(float x, float z, int seed) {
			throw new UnsupportedOperationException();
		}
			
		@Override
		default float minValue() {
			return Float.NEGATIVE_INFINITY;
		}

		@Override
		default float maxValue() {
			return Float.POSITIVE_INFINITY;
		}
	}
}
