package raccoonman.reterraforged.common.level.levelgen.densityfunctions;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record LazyDensityFunction(Supplier<DensityFunction> delegate) implements DensityFunction {

	public LazyDensityFunction {
		delegate = Suppliers.memoize(delegate::get);
	}
	
	@Override
	public double compute(FunctionContext var1) {
		return this.delegate.get().compute(var1);
	}

	@Override
	public void fillArray(double[] var1, ContextProvider var2) {
		this.delegate.get().fillArray(var1, var2);
	}

	@Override
	public DensityFunction mapAll(Visitor var1) {
		return new LazyDensityFunction(() -> {
			return this.delegate.get().mapAll(var1);
		});
	}

	@Override
	public double minValue() {
		return this.delegate.get().minValue();
	}

	@Override
	public double maxValue() {
		return this.delegate.get().maxValue();
	}

	@Override
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
		return this.delegate.get().codec();
	}
}
