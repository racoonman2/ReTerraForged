package raccoonman.reterraforged.common.level.levelgen.noise.density;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public class FlatCache implements DensityFunction.SimpleFunction {
	private final FunctionContext cellPos;
    private final DensityFunction filler;
    private final int width;
    private final double[] values;

    public FlatCache(FunctionContext cellPos, DensityFunction filler, int width) {
    	this.cellPos = cellPos;
    	this.filler = filler;
    	this.width = width;
    	this.values = new double[width * width];
    }
    
    public void fillCache(int cellStartBlockX, int cellStartBlockZ) {
    	MutableContext ctx = new MutableContext();
		ctx.y = 0;
		for(int x = 0; x < this.width; x++) {
			for(int z = 0; z < this.width; z++) {
				ctx.x = cellStartBlockX + x;
				ctx.z = cellStartBlockZ + z;
    			this.values[x * this.width + z] = this.filler.compute(ctx);
    		}
		}
    }

    @Override
    public double compute(DensityFunction.FunctionContext functionContext) {
        int x = this.cellPos.blockX();
        int z = this.cellPos.blockZ();
        if (x >= 0 && z >= 0 && x < this.width && z < this.width) {
            return this.values[x * this.width + z];
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
	public KeyDispatchDataCodec<? extends DensityFunction> codec() {
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
		public static final Codec<FlatCache.Marker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(FlatCache.Marker::function)
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
            return visitor.apply(new Marker(this.function.mapAll(visitor)));
        }

		@Override
		public KeyDispatchDataCodec<FlatCache.Marker> codec() {
			return new KeyDispatchDataCodec<>(CODEC);
		}
	}
}