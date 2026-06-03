package raccoonman.reterraforged.world.worldgen.layer.terrain;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunction.FunctionContext;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.world.Ticking;
import raccoonman.reterraforged.world.worldgen.densityfunction.MultiFunction;
import raccoonman.reterraforged.world.worldgen.densityfunction.MutableFunctionContext;
import raccoonman.reterraforged.world.worldgen.layer.CacheLayer;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.ManagedPool;
import raccoonman.reterraforged.world.worldgen.layer.Reference;

public class TerrainLayer extends CacheLayer<Terrain> implements Ticking {
	private static final int INITIAL_DATA_POOL_SIZE = 256;
	
	private int batchCount;
	private int batchSize;
	private List<SampledFunction> sampledFunctions;
	private ManagedPool<Terrain.Data> dataPool;
	
	public TerrainLayer(int size, int batchCount, List<SampledFunction> sampledFunctions) {
		super(size);
		this.batchCount = batchCount;
		this.batchSize = size / batchCount;
		this.sampledFunctions = sampledFunctions;
		this.initializePool(sampledFunctions);
	}
	
	@Override
	public void tick(int currentTick) {
		this.dataPool.tick(currentTick);
	}

	@Override
	public void addDebugScreenInfo(DebugScreenDisplayer display, ResourceLocation layerName, RandomState randomState, BlockPos pos) {
		super.addDebugScreenInfo(display, layerName, randomState, pos);
		this.dataPool.addDebugScreenInfo(display, layerName);
	}
	
	@Override
	protected CompletableFuture<Terrain> provideUncached(int layerX, int layerY, Executor executor) {
		Terrain terrain = new Terrain(this.size, 1, this.dataPool.take());

		MappedFunction[] functions = this.sampledFunctions.stream().map((sampledFunction) -> {
			float[] output = terrain.provide(sampledFunction.dataType);
			return new MappedFunction(sampledFunction.function.value(), output);
		}).toArray(MappedFunction[]::new);
		
		int layerCellX = this.layerToBlock(layerX);
		int layerCellY = this.layerToBlock(layerY);
		CompletableFuture<?>[] futures = new CompletableFuture[this.batchCount * this.batchCount];
		for(int batchY = 0; batchY < this.batchCount; batchY++) {
			for(int batchX = 0; batchX < this.batchCount; batchX++) {
				int minCellX = batchX * this.batchSize;
				int minCellY = batchY * this.batchSize;
				int maxCellX = (batchX + 1) * this.batchSize;
				int maxCellY = (batchY + 1) * this.batchSize;
				futures[batchY * this.batchCount + batchX] = CompletableFuture.runAsync(() -> {
					this.generateBatch(terrain, functions, minCellX, minCellY, maxCellX, maxCellY, layerCellX, layerCellY);
				}, executor);
			}
		}
		return CompletableFuture.allOf(futures).thenApply((v) -> terrain);
	}
	
	private void generateBatch(Terrain terrain, MappedFunction[] functions, int minCellX, int minCellY, int maxCellX, int maxCellY, int layerCellX, int layerCellY) {
		DensityFunction.Visitor localVisitor = localVisitor();
		MappedFunction[] localFunctions = Arrays.stream(functions).map((function) -> function.mapAll(localVisitor)).toArray(MappedFunction[]::new);
	
		MutableFunctionContext ctx = new MutableFunctionContext();
		for(int cellY = minCellY; cellY < maxCellY; cellY++) {
			for(int cellX = minCellX; cellX < maxCellX; cellX++) {
				int cellIndex = terrain.cellIndex(cellX, cellY);
				ctx.at(layerCellX + cellX, layerCellY + cellY);
				for(MappedFunction function : localFunctions) {
					function.compute(cellIndex, ctx);
				}
			}
		}
	}
	
	private static DensityFunction.Visitor localVisitor() {
		Map<ResourceLocation, MultiFunction.Data> multiFunctionData = new HashMap<>();
		Map<DensityFunction, DensityFunction> cache = new HashMap<>();
		return (function) -> cache.computeIfAbsent(function, (v) -> {
			if(function instanceof MultiFunction multiFunction) {
				ResourceLocation cacheId = multiFunction.cacheId();
				MultiFunction.Data data = multiFunctionData.computeIfAbsent(cacheId, (j) -> multiFunction.createData());
				return multiFunction.withData(data);
			}
			return function;
		});
	}
	
	private void initializePool(List<SampledFunction> sampledFunctions) {
		this.dataPool = new ManagedPool<>(INITIAL_DATA_POOL_SIZE, () -> new Terrain.Data(this.dataPool::add));
		
		Terrain.Data[] array = new Terrain.Data[INITIAL_DATA_POOL_SIZE];
		Terrain mockTerrain = new Terrain(this.size, 1, null);
		for(int i = 0; i < array.length; i++) {
			Terrain.Data data = this.dataPool.take();
			for(SampledFunction function : sampledFunctions) {
				data.map.computeIfAbsent(function.dataType, (v) -> function.dataType.apply(mockTerrain));
			}
			array[i] = data;
		}
		
		for(Terrain.Data data : array) {
			this.dataPool.add(data);
		}
	}
	
	private record MappedFunction(DensityFunction function, float[] output) {
		
		public void compute(int cellIndex, FunctionContext ctx) {
			this.output[cellIndex] = (float) this.function.compute(ctx);
		}
		
		public MappedFunction mapAll(DensityFunction.Visitor visitor) {
			return new MappedFunction(this.function.mapAll(visitor), this.output);
		}
	}
	
	public record SampledFunction(DataType<float[]> dataType, Holder<DensityFunction> function) {
		public static final Codec<SampledFunction> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			DataType.<float[]>codec().fieldOf("data_type").forGetter(TerrainLayer.SampledFunction::dataType),
			DensityFunction.CODEC.fieldOf("function").forGetter(TerrainLayer.SampledFunction::function)
		).apply(instance, SampledFunction::new));

		public TerrainLayer.SampledFunction mapAll(Layer.Visitor visitor) {
			return new TerrainLayer.SampledFunction(this.dataType, Layer.Factory.mapFunction(this.function, visitor));
		}
	}
	
	public record Factory(int layerSize, int batchCount, List<TerrainLayer.SampledFunction> sampledFunctions) implements Layer.Factory<Reference<Terrain>> {
		public static final MapCodec<TerrainLayer.Factory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.INT.fieldOf("layer_size").forGetter(TerrainLayer.Factory::layerSize),
			Codec.INT.fieldOf("batch_count").forGetter(TerrainLayer.Factory::batchCount),
			TerrainLayer.SampledFunction.CODEC.listOf().fieldOf("sampled_functions").forGetter(TerrainLayer.Factory::sampledFunctions)
		).apply(instance, TerrainLayer.Factory::new));
		
		@Override
		public TerrainLayer createLayer(Layer.Provider provider) {
			return new TerrainLayer(this.layerSize, this.batchCount, this.sampledFunctions);
		}

		@Override
		public MapCodec<TerrainLayer.Factory> codec() {
			return CODEC;
		}

		@Override
		public Layer.Factory<Reference<Terrain>> mapAll(Layer.Visitor visitor) {
			return visitor.apply(new TerrainLayer.Factory(this.layerSize, this.batchCount, this.sampledFunctions.stream().map((sampledFunction) -> sampledFunction.mapAll(visitor)).toList()));
		}
	}
}
