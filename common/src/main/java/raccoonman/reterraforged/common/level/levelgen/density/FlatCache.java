package raccoonman.reterraforged.common.level.levelgen.density;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.ReTerraForged;

public class FlatCache implements DensityFunction.SimpleFunction {
    private DensityFunction filler;
    private int width;
    private int padding;
    private int offsetX, offsetZ;
    private Supplier<double[]> values;

    public FlatCache(DensityFunction filler, int width, int padding, int offsetX, int offsetZ) {
    	this.filler = filler;
    	this.width = width + padding * 2;
    	this.padding = padding;
    	this.offsetX = offsetX;
    	this.offsetZ = offsetZ;
    	this.values = Suppliers.memoize(() -> {
    		double[] values = new double[this.width * this.width];
        	MutableFunctionContext ctx = new MutableFunctionContext();
    		for(int x = -this.padding; x < this.width - this.padding; x++) {
    			for(int z = -this.padding; z < this.width - this.padding; z++) {
    				ctx.blockX = this.offsetX + x;
    				ctx.blockZ = this.offsetZ + z;
        			values[this.indexOf(ctx.blockX, ctx.blockZ)] = this.filler.compute(ctx);
        		}
    		}
    		return values;
    	});
    }

    private int indexOf(int x, int z) {
    	return (x - this.offsetX + this.padding) * this.width + (z - this.offsetZ + this.padding);
    }

    @Override
    public double compute(DensityFunction.FunctionContext functionContext) {
    	int blockX = functionContext.blockX();
    	int blockZ = functionContext.blockZ();
    	
        int index = this.indexOf(blockX, blockZ);
        double[] values = this.values.get();
        if (index >= 0 && index < values.length) {
            return values[index];
        }
        ReTerraForged.LOGGER.warn("cache miss! cache width: {}, padding: {}, blockX: {}, blockZ: {}", this.width, this.padding, blockX - this.offsetX + this.padding, blockZ - this.offsetZ + this.padding);
        return this.filler.compute(functionContext);
    }

    @Override
    public void fillArray(double[] ds, DensityFunction.ContextProvider contextProvider) {
//    	throw new UnsupportedOperationException();
    }

	@Override
	public double minValue() {
		return this.filler.minValue();
	}

	@Override
	public double maxValue() {
		return this.filler.maxValue();
	}

	@Override
	public KeyDispatchDataCodec<FlatCache> codec() {
		throw new UnsupportedOperationException();
	}
	
	public record Marker(DensityFunction function, int padding) implements DensityFunction.SimpleFunction {
		public static final Codec<FlatCache.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(FlatCache.Marker::function),
			Codec.INT.fieldOf("padding").forGetter(FlatCache.Marker::padding)
		).apply(instance, FlatCache.Marker::new));
		
		@Override
		public double compute(FunctionContext ctx) {
			return this.function.compute(ctx);
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
        public DensityFunction mapAll(Visitor visitor) {
            return visitor.apply(new Marker(this.function.mapAll(visitor), this.padding));
        }

		@Override
		public KeyDispatchDataCodec<FlatCache.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}