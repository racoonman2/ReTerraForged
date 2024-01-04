package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

public record ConditionalArrayCache(DensityFunction function) implements MarkerFunction {
	public static final Codec<ConditionalArrayCache> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		DensityFunction.HOLDER_HELPER_CODEC.fieldOf("function").forGetter(ConditionalArrayCache::function)
	).apply(instance, ConditionalArrayCache::new));

	@Override
	public double compute(FunctionContext ctx) {
		return this.function.compute(ctx);
	}
	
	@Override
	public KeyDispatchDataCodec<ConditionalArrayCache> codec() {
		return new KeyDispatchDataCodec<>(CODEC);
	}

	@Override
	public DensityFunction mapAll(Visitor visitor) {
		return visitor.apply(new ConditionalArrayCache(this.function.mapAll(visitor)));
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
			MutableFunctionContext ctx = new MutableFunctionContext();
			for(int x = 0; x < width; x++) {
				for(int z = 0; z < width; z++) {
					this.cache[x * width + z] = (float) ConditionalArrayCache.this.function.compute(ctx.at(firstBlockX + x, 0, firstBlockZ + z));
				}
			}
		}
		
		@Override
		public double compute(FunctionContext ctx) {
            int blockX = ctx.blockX();
            int blockZ = ctx.blockZ();
            int cacheX = blockX - this.firstBlockX;
            int cacheZ = blockZ - this.firstBlockZ;
            if (cacheX >= 0 && cacheZ >= 0 && cacheX < this.width && cacheZ < this.width) {
                return this.cache[cacheX * this.width + cacheZ];
            }
            return ConditionalArrayCache.this.function.compute(ctx);
		}

		@Override
		public double minValue() {
			return ConditionalArrayCache.this.function.minValue();
		}

		@Override
		public double maxValue() {
			return ConditionalArrayCache.this.function.maxValue();
		}		
	}
}
