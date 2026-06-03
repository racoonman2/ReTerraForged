package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

// we can't add the additional border quart to FastFlatCache without causing other functions that use it to oversample
// TODO merge with FastFlastCache
public class FlatCache2 implements MappedFunction {
	private DensityFunction function;
	private float[] cache;
	private int minX;
	private int minZ;
	private int size;
	
	public FlatCache2(DensityFunction function, float[] cache, int minX, int minZ, int size) {
		this.function = function;
		this.cache = cache;
		this.minX = minX;
		this.minZ = minZ;
		this.size = size;
	}
	
	@Override
	public double compute(FunctionContext ctx) {
		int blockX = ctx.blockX();
		int blockZ = ctx.blockZ();
		int index = (blockX - this.minX) * this.size + (blockZ - this.minZ);
		if(index >= 0 && index < this.cache.length) {
			return this.cache[index];
		}
		return this.function.compute(ctx);
	}

	@Override
	public void fillArray(double[] ds, ContextProvider contextProvider) {
		contextProvider.fillAllDirectly(ds, this);
	}

	@Override
	public DensityFunction mapAll(DensityFunction.Visitor visitor) {
		return visitor.apply(this);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}
	
	public record Marker(DensityFunction function, int size) implements MappedFunction.Marker {
		public static final MapCodec<FlatCache2.Marker> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(FlatCache2.Marker::function),
			Codec.INT.fieldOf("size").forGetter(FlatCache2.Marker::size)
		).apply(instance, FlatCache2.Marker::new));

		@Override
		public double compute(FunctionContext ctx) {
			return this.function.compute(ctx);
		}

		@Override
		public void fillArray(double[] ds, ContextProvider contextProvider) {
			contextProvider.fillAllDirectly(ds, this.function);
		}
		
		@Override
		public DensityFunction mapAll(DensityFunction.Visitor visitor) {
			return visitor.apply(new FlatCache2.Marker(this.function.mapAll(visitor), this.size));
		}

		@Override
		public KeyDispatchDataCodec<FlatCache2.Marker> codec() {
			return KeyDispatchDataCodec.of(CODEC);
		}		
	}
}
