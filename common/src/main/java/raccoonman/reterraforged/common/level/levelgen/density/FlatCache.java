package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.ReTerraForged;

public class FlatCache implements DensityFunction.SimpleFunction {
	private FunctionContext cellPos;
    private DensityFunction filler;
    private int width;
    private int padding;
    private double[] values;

    public FlatCache(FunctionContext cellPos, DensityFunction filler, int width, int padding) {
    	this.cellPos = cellPos;
    	this.filler = filler;
    	this.width = width + padding * 2;
    	this.padding = padding;
    	this.values = new double[this.width * this.width];
    }
    
    public void fillCache(int offsetX, int offsetZ) {
    	MutableContext ctx = new MutableContext();
		for(int x = -this.padding; x < this.width - this.padding; x++) {
			for(int z = -this.padding; z < this.width - this.padding; z++) {
				ctx.x = offsetX + x;
				ctx.z = offsetZ + z;
    			this.values[this.indexOf(x, z)] = this.filler.compute(ctx);
    		}
		}
    }
    
    private int indexOf(int x, int z) {
    	return (x + this.padding) * this.width + (z + this.padding);
    }

    @Override
    public double compute(DensityFunction.FunctionContext functionContext) {
    	int cellX = this.cellPos.blockX();
    	int cellZ = this.cellPos.blockZ();
    	
        int index = this.indexOf(cellX, cellZ);
        if (index >= 0 && index < this.values.length) {
            return this.values[index];
        }
//        ReTerraForged.LOGGER.warn("cache miss! cache width: {}, padding: {}, cellX: {}, cellZ: {}", this.width, this.padding, cellX, cellZ);
        return this.filler.compute(new FunctionContext() {
			
			@Override
			public int blockZ() {
				return functionContext.blockX() + 1;
			}
			
			@Override
			public int blockY() {
				return 0;
			}
			
			@Override
			public int blockX() {
				return functionContext.blockZ() + 1;
			}
		});
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
	
	class MutableContext implements DensityFunction.FunctionContext {
		public int x;
		public int y;
		public int z;
		
		@Override
		public int blockX() {
			return this.x;
		}

		@Override
		public int blockY() {
			return this.y;
		}

		@Override
		public int blockZ() {
			return this.z;
		}
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