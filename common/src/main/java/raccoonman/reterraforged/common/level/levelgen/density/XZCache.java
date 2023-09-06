package raccoonman.reterraforged.common.level.levelgen.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class XZCache implements DensityFunction.SimpleFunction {
	private final FunctionContext cellPos;
    private final DensityFunction filler;
    private final int width;
    private final double[] values;

    public XZCache(FunctionContext cellPos, DensityFunction filler, int width) {
    	this.cellPos = cellPos;
    	this.filler = filler;
    	this.width = width;
    	this.values = new double[width * width];
    }
    
    public void fillCache(int offsetX, int offsetZ) {
    	MutableContext ctx = new MutableContext();
		ctx.y = 0;
		for(int x = 0; x < this.width; x++) {
			for(int z = 0; z < this.width; z++) {
				ctx.x = offsetX + x;
				ctx.z = offsetZ + z;
    			this.values[x * this.width + z] = this.filler.compute(ctx);
    		}
		}
    }

    @Override
    public double compute(DensityFunction.FunctionContext functionContext) {
        int index = this.cellPos.blockX() * this.width + this.cellPos.blockZ();
        if (index >= 0 && index < this.values.length) {
            return this.values[index];
        }
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
	public KeyDispatchDataCodec<XZCache> codec() {
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
	
	public record Marker(DensityFunction function) implements DensityFunction.SimpleFunction {
		public static final Codec<XZCache.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(XZCache.Marker::function)
		).apply(instance, XZCache.Marker::new));
		
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
            return visitor.apply(new Marker(this.function.mapAll(visitor)));
        }

		@Override
		public KeyDispatchDataCodec<XZCache.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}