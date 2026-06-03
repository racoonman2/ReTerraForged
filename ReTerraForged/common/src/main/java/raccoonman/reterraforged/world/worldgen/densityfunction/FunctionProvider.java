package raccoonman.reterraforged.world.worldgen.densityfunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseRouter;
import raccoonman.reterraforged.world.worldgen.layer.LayerCache;

@Deprecated
public class FunctionProvider implements AutoCloseable {
	public static final DensityFunction.Visitor PASSTHROUGH = (f) -> f;
	public static final DensityFunction.Visitor localVisitor(int minBlockX, int minBlockZ) {
		return (f) -> {
			if(f instanceof Cache2d cache && !cache.local()) {
				return Cache2d.makeLocal(cache.function());
			}
			
			if(f instanceof FlatCache2.Marker marker) {
				DensityFunction cached = marker.function();
				int size = marker.size();
				float[] cache = new float[size * size];
				MutableFunctionContext ctx = new MutableFunctionContext();
				for(int localX = 0; localX < size; localX++) {
					for(int localZ = 0; localZ < size; localZ++) {
						int index = localX * size + localZ;
						ctx.at(minBlockX + localX, 0, minBlockZ + localZ);
						double value = cached.compute(ctx);
						cache[index] = (float) value;
					}
				}
				return new FlatCache2(cached, cache, minBlockX, minBlockZ, size);
			}
			return f;
		};
	}
	
	private Map<DensityFunction, DensityFunction> mappedFunctions;
	private List<CompletableFuture<LayerCache<DensityFunction>>> cacheFutures;
	
	public FunctionProvider() {
		this.mappedFunctions = new HashMap<>();
		this.cacheFutures = new ArrayList<>();
	}

	public CompletableFuture<DensityFunction> provide(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, DensityFunction function, DensityFunction.Visitor visitor, Executor executor) {
		return this.provide(minBlockX, minBlockZ, maxBlockX, maxBlockZ, DensityFunction::mapAll, function, visitor, executor);
	}
	
	public CompletableFuture<NoiseRouter> provide(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, NoiseRouter router, DensityFunction.Visitor visitor, Executor executor) {
		return this.provide(minBlockX, minBlockZ, maxBlockX, maxBlockZ, NoiseRouter::mapAll, router, visitor, executor);
	}

	public CompletableFuture<Climate.Sampler> provide(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, Climate.Sampler sampler, DensityFunction.Visitor visitor, Executor executor) {
		return this.provide(minBlockX, minBlockZ, maxBlockX, maxBlockZ, (input, v) -> {
			DensityFunction temperature = input.temperature().mapAll(v);
			DensityFunction humidity = input.humidity().mapAll(v);
			DensityFunction continentalness = input.continentalness().mapAll(v);
			DensityFunction erosion = input.erosion().mapAll(v);
			DensityFunction depth = input.depth().mapAll(v);
			DensityFunction weirdness = input.weirdness().mapAll(v);
			return new Climate.Sampler(temperature, humidity, continentalness, erosion, depth, weirdness, input.spawnTarget());
		}, sampler, visitor, executor);
	}
	
	private <A> CompletableFuture<A> provide(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, BiFunction<A, DensityFunction.Visitor, A> mapper, A input, DensityFunction.Visitor visitor, Executor executor) {
		List<CompletableFuture<?>> futures = new ArrayList<>();
		A mapped = mapper.apply(input, (function) -> this.mappedFunctions.computeIfAbsent(function, (v) -> {
			return visitor.apply(this.mapFunction(minBlockX, minBlockZ, maxBlockX, maxBlockZ, function, executor, futures));
		}));
		return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenApply((v) -> {
			return mapped;
		});
	}
	
	@Override
	public void close() {
		this.mappedFunctions.clear();
		this.cacheFutures.clear();
	}

	private DensityFunction mapFunction(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, DensityFunction function, Executor executor, List<CompletableFuture<?>> futures) {
//		if(function instanceof LayerFunction layerFunction) {
//			Layer<CompletableFuture<DensityFunction>> layer = layerFunction.getLayer();
//			CompletableFuture<LayerCache<DensityFunction>> cacheFuture = provideCache(minBlockX, minBlockZ, maxBlockX, maxBlockZ, layer, DensityFunction[]::new, executor);
//			LayerCacheFunction mappedFunction = new LayerCacheFunction(layerFunction, layerFunction.getFallback());
//			if(cacheFuture.isDone()) {
//				mappedFunction.complete(cacheFuture.resultNow());
//			} else {
//				futures.add(cacheFuture.thenAccept(mappedFunction::complete));
//			}
//			this.cacheFutures.add(cacheFuture);
//			return mappedFunction;
//		}
		return function;
	}
//
//	private static <A> CompletableFuture<LayerCache<A>> provideCache(int minBlockX, int minBlockZ, int maxBlockX, int maxBlockZ, Layer<CompletableFuture<A>> layer, IntFunction<A[]> arrayFactory, Executor executor) {
//		int minLayerX = layer.blockToLayer(minBlockX);
//		int minLayerY = layer.blockToLayer(minBlockZ);
//		int maxLayerX = layer.blockToLayer(maxBlockX);
//		int maxLayerY = layer.blockToLayer(maxBlockZ);
//		int layerCountX = maxLayerX - minLayerX;
//		int layerCountY = maxLayerY - minLayerY;
//		int totalChunks = layerCountX * layerCountY;
//		A[] values = arrayFactory.apply(totalChunks);
//		CompletableFuture<?>[] futures = new CompletableFuture[totalChunks];
//		for(int offsetX = 0; offsetX < layerCountX; offsetX++) {
//			for(int offsetY = 0; offsetY < layerCountY; offsetY++) {
//				int index = offsetY * layerCountX + offsetX;
//				futures[index] = layer.provide(minLayerX + offsetX, minLayerY + offsetY, executor).thenAccept((value) -> {
//					values[index] = value;
//				});
//			}
//		}
//		return CompletableFuture.allOf(futures).thenApply((_) -> new LayerCache<>(layer, minLayerX, minLayerY, layerCountX, values));
//	}
}
