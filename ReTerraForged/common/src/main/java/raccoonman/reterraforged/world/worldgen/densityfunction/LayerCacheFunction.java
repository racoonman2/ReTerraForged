package raccoonman.reterraforged.world.worldgen.densityfunction;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.SimpleFunction;
import raccoonman.reterraforged.world.worldgen.layer.CacheLayer;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.LayerCache;
import raccoonman.reterraforged.world.worldgen.layer.Reference;

public class LayerCacheFunction implements MappedFunction, SimpleFunction {
	private LayerCache<DensityFunction> cache;
	private DensityFunction fallback;

	public LayerCacheFunction(LayerCache<DensityFunction> cache, DensityFunction fallback) {
		this.cache = cache;
		this.fallback = fallback;
	}

	@Override
	public double compute(FunctionContext ctx) {
		int blockX = ctx.blockX();
		int blockZ = ctx.blockZ();
		DensityFunction function = this.cache.getAtBlock(blockX, blockZ);
		if(function != null) {
			return function.compute(ctx);
		}
		return this.fallback.compute(ctx);
	}

	@Override
	public double minValue() {
		return 0.0D;
	}

	@Override
	public double maxValue() {
		return 1.0D;
	}
	
	public static LayerCacheFunction of(Layer<Reference<DensityFunction>> layer, DensityFunction fallback, int minChunkX, int minChunkZ, int maxChunkX, int maxChunkZ) {
		ChunkPos min = new ChunkPos(minChunkX, minChunkZ);
		ChunkPos max = new ChunkPos(maxChunkX, maxChunkZ);
		
		int minBlockX = min.getMinBlockX();
		int minBlockZ = min.getMinBlockZ();
		int maxBlockX = max.getMaxBlockX();
		int maxBlockZ = max.getMaxBlockZ();
		int minLayerX = layer.blockToLayer(minBlockX);
		int minLayerY = layer.blockToLayer(minBlockZ);
		int maxLayerX = layer.blockToLayer(maxBlockX);
		int maxLayerY = layer.blockToLayer(maxBlockZ);
		int lengthX = maxLayerX - minLayerX + 1;
		int lengthY = maxLayerY - minLayerY + 1;
		int total = lengthX * lengthY;
	
		DensityFunction[] functions = new DensityFunction[total];
		LayerCache<DensityFunction> layerCache = new LayerCache<>(layer, minLayerX, minLayerY, lengthX, functions);
		for(int layerX = minLayerX; layerX <= maxLayerX; layerX++) {
			for(int layerY = minLayerY; layerY <= maxLayerY; layerY++) {
				int index = layerCache.index(layerX, layerY);
				DensityFunction result = CacheLayer.provideResultNow(layer, layerX, layerY);
				functions[index] = result;
			}
		}
		return new LayerCacheFunction(layerCache, fallback);
	}
}
