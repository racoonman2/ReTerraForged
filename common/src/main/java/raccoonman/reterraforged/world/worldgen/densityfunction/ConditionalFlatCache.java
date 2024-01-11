package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.Arrays;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record ConditionalFlatCache(DensityFunction function) implements MarkerFunction {
	public static final Codec<ConditionalFlatCache> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(ConditionalFlatCache::function)
	).apply(instance, ConditionalFlatCache::new));

	@Override
	public double compute(FunctionContext ctx) {
		return this.function.compute(ctx);
	}
	
	@Override
	public KeyDispatchDataCodec<ConditionalFlatCache> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new ConditionalFlatCache(this.function.mapAll(visitor)));
	}
	
	public class Cache implements MarkerFunction.Mapped {
		private int firstBlockX;
		private int firstBlockZ;
		private int width;
		private float[] cache;
		
		public Cache(int firstBlockX, int firstBlockZ, int width) {
			this.firstBlockX = firstBlockX;
			this.firstBlockZ = firstBlockZ;
			this.width = width;
			this.cache = new float[width * width];
			Arrays.fill(this.cache, Float.NaN);
		}
		
		@Override
		public double compute(FunctionContext ctx) {
            int blockX = ctx.blockX();
            int blockZ = ctx.blockZ();
            int cacheX = blockX - this.firstBlockX;
            int cacheZ = blockZ - this.firstBlockZ;
            if (cacheX >= 0 && cacheZ >= 0 && cacheX < this.width && cacheZ < this.width) {
            	int index = cacheX * this.width + cacheZ;
            	if(Float.isNaN(this.cache[index])) {
            		this.cache[index] = (float) ConditionalFlatCache.this.function.compute(ctx);
            	}
                return this.cache[index];
            }
            return ConditionalFlatCache.this.function.compute(ctx);
		}

		@Override
		public double minValue() {
			return ConditionalFlatCache.this.function.minValue();
		}

		@Override
		public double maxValue() {
			return ConditionalFlatCache.this.function.maxValue();
		}		
	}
}
