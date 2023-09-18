package raccoonman.reterraforged.common.level.levelgen.density;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

//TODO add padding
public class FlatCache implements DensityFunction.SimpleFunction {
    private DensityFunction filler;
    private int width;
    private int offsetX, offsetZ;
    private Supplier<double[]> values;

    public FlatCache(DensityFunction filler, int width, int padding, int offsetX, int offsetZ) {
    	this.filler = filler;
    	this.width = width;
    	this.offsetX = offsetX;
    	this.offsetZ = offsetZ;
    	this.values = Suppliers.memoize(() -> {
    		double[] values = new double[this.width * this.width];
        	MutableFunctionContext ctx = new MutableFunctionContext();
    		for(int x = 0; x < this.width; x++) {
    			for(int z = 0; z < this.width; z++) {
    				ctx.blockX = this.offsetX + x;
    				ctx.blockZ = this.offsetZ + z;
        			values[this.indexOf(ctx.blockX, ctx.blockZ)] = this.filler.compute(ctx);
        		}
    		}
    		return values;
    	});
    }

    private int indexOf(int x, int z) {
    	return this.width * (z - this.offsetZ) + (x - this.offsetX);
    }

    @Override
    public double compute(DensityFunction.FunctionContext functionContext) {
    	int blockX = functionContext.blockX();
    	int blockZ = functionContext.blockZ();
    	
        int index = this.indexOf(blockX, blockZ);
        double[] values = this.values.get();
        if (blockX >= this.offsetX && blockZ >= this.offsetZ && blockX < this.offsetX + this.width && blockZ < this.offsetZ + this.width) {
            return values[index];
        }
//        ReTerraForged.LOGGER.warn("cache miss! cache width: {}, padding: {}, blockX: {}, blockZ: {}", this.width, this.padding, blockX, blockZ);
        return this.filler.compute(functionContext);
    }

    @Override
    public void fillArray(double[] ds, DensityFunction.ContextProvider contextProvider) {
    	// NOOP
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

		@Override		public KeyDispatchDataCodec<FlatCache.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}