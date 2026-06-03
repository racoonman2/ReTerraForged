package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.function.Function;
import java.util.function.Supplier;

import com.mojang.serialization.MapCodec;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.world.worldgen.PosUtil;

public record Cache2d(DensityFunction function, Function<DensityFunction, Supplier<Cache>> factory, Supplier<Cache> cache, boolean local) implements DensityFunction {
	public static final MapCodec<Cache2d> CODEC = DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").xmap(Cache2d::makeGlobal, Cache2d::function);
	
	public Cache2d(DensityFunction function, Function<DensityFunction, Supplier<Cache>> factory, boolean local) {
		this(function, factory, factory.apply(function), local);
	}
	
	@Override
	public double compute(FunctionContext ctx) {
		return this.cache.get().getAndUpdate(ctx);
	}

	@Override
	public double minValue() {
		return this.function.minValue();
	}

	@Override
	public double maxValue() {
		return this.function.maxValue();
	}

    @Override
    public void fillArray(double[] ds, ContextProvider contextProvider) {
    	this.function.fillArray(ds, contextProvider);
    }
	
    @Override
    public boolean equals(Object o) {
    	return o instanceof Cache2d cache && cache.function.equals(this.function);
    }
    
    @Override
    public int hashCode() {
    	return this.function.hashCode();
    }

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new Cache2d(this.function.mapAll(visitor), this.factory, this.local));
	}

	@Override
	public KeyDispatchDataCodec<Cache2d> codec() {
		return KeyDispatchDataCodec.of(CODEC);
	}
	
	public static Cache2d makeLocal(DensityFunction function) {
		return new Cache2d(function, (f) -> {
			return () -> new Cache(f);
		}, true);
	}
	
	public static Cache2d makeGlobal(DensityFunction function) {
		return new Cache2d(function, (f) -> {
			ThreadLocal<Cache> cache = ThreadLocal.withInitial(() -> new Cache(f));
			return cache::get;
		}, false);
	}
    
	private static class Cache {
		private DensityFunction function;
		
		private long lastXZ = Long.MIN_VALUE;
		private double lastValue;
		
		public Cache(DensityFunction function) {
			this.function = function;
		}
		
		public double getAndUpdate(FunctionContext context) {
			long pos = PosUtil.pack(context.blockX(), context.blockZ());
			if(pos != this.lastXZ) {
				this.lastXZ = pos;
				this.lastValue = this.function.compute(context);
			}
			return this.lastValue;
		}
	}
}
