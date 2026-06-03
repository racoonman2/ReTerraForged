package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.QuartPos;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

// computes the data of all flat caches in the chunk at the same time
// allows 2d noise data to be reused instead of being computed per cache
public class FastFlatCache implements MappedFunction {
    private DensityFunction function;
	private boolean fullResolution;
	private int firstX, firstZ;
	private int size;
	private double[] values;

    public FastFlatCache(DensityFunction function, int firstNoiseX, int firstNoiseZ, int noiseSize, boolean fullResolution) {
        this.function = function;
        this.fullResolution = fullResolution;
        this.firstX = this.scaleWithResolution(firstNoiseX);
        this.firstZ = this.scaleWithResolution(firstNoiseZ);
        this.size = this.scaleWithResolution(noiseSize);
        this.values = new double[this.size * this.size];
    }
    
    @Override
    public double compute(DensityFunction.FunctionContext functionContext) {
        int x = functionContext.blockX();
        int z = functionContext.blockZ();
        if(!this.fullResolution) {
        	x = QuartPos.fromBlock(x);
        	z = QuartPos.fromBlock(z);
        }
        
        int localX = x - this.firstX;
        int localZ = z - this.firstZ;
        int index = this.index(localX, localZ);
        if (index >= 0 && index < this.values.length) {
            return this.values[index];
        }
        return this.function.compute(functionContext);
    }

    @Override
    public void fillArray(double[] ds, DensityFunction.ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(ds, this);
    }

	@Override
	public DensityFunction mapAll(DensityFunction.Visitor visitor) {
		return visitor.apply(this);
	}

	@Override
	public double minValue() {
		return this.function.minValue();
	}

	@Override
	public double maxValue() {
		return this.function.maxValue();
	}
	
	private int index(int x, int z) {
		return z * this.size + x;
	}
	
	public static void initialize(Iterable<FastFlatCache> caches, int firstNoiseX, int firstNoiseZ, int size) {
		MutableFunctionContext functionCtx = new MutableFunctionContext();
        int firstBlockX = QuartPos.toBlock(firstNoiseX);
        int firstBlockZ = QuartPos.toBlock(firstNoiseZ);
        int blockSize = QuartPos.toBlock(size);
        for(int x = 0; x < blockSize; x++) {
        	for(int z = 0; z < blockSize; z++) {
        		int blockX = firstBlockX + x;
        		int blockZ = firstBlockZ + z;
        		int quartX = QuartPos.fromBlock(blockX);
        		int quartZ = QuartPos.fromBlock(blockZ);
        		
        		functionCtx.at(blockX, 0, blockZ);
                for(FastFlatCache cache : caches) {
                	if(!cache.fullResolution && blockX != quartX && z != quartZ) {
                		continue;
                	}
                	int scaledX = cache.fullResolution ? x : QuartPos.fromBlock(x);
                	int scaledZ = cache.fullResolution ? z : QuartPos.fromBlock(z);
                    cache.values[cache.index(scaledX, scaledZ)] = cache.function.compute(functionCtx);
                }
            }
        }
	}
	
	private int scaleWithResolution(int value) {
		return this.fullResolution ? QuartPos.toBlock(value) : value;
	}
	
	public record Marker(DensityFunction function, boolean fullResolution) implements DensityFunction {
		public static final MapCodec<FastFlatCache.Marker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(Marker::function),
			Codec.BOOL.fieldOf("full_resolution").forGetter(Marker::fullResolution)
		).apply(instance, Marker::new));

		@Override
		public double compute(FunctionContext ctx) {
			return this.function.compute(ctx);
		}

		@Override
		public void fillArray(double[] array, ContextProvider ctxProvider) {
			this.function.fillArray(array, ctxProvider);
		}

		@Override
		public DensityFunction mapAll(DensityFunction.Visitor visitor) {
			return visitor.apply(new Marker(this.function.mapAll(visitor), this.fullResolution));
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
		public KeyDispatchDataCodec<FastFlatCache.Marker> codec() {
			return KeyDispatchDataCodec.of(CODEC);
		}
	}
}
