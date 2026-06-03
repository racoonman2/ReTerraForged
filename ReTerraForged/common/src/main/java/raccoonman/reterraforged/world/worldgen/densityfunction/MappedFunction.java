package raccoonman.reterraforged.world.worldgen.densityfunction;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public interface MappedFunction extends DensityFunction {

	@Override
	default KeyDispatchDataCodec<? extends Marker> codec() {
		throw new UnsupportedOperationException();
	}
	
	public interface Marker extends DensityFunction {

		@Override
		default double compute(FunctionContext ctx) {
			throw this.makeException();
		}
		
		@Override
		default void fillArray(double[] array, ContextProvider provider) {
			throw this.makeException();
		}
		
		@Override
		default double minValue() {
			return Double.NEGATIVE_INFINITY;
		}

		@Override
		default double maxValue() {
			return Double.POSITIVE_INFINITY;
		}
		
		private UnsupportedOperationException makeException() {
			return new UnsupportedOperationException(this.getClass().getName());
		}
	}
}
