package raccoonman.reterraforged.world.worldgen.layer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.VisibleForDebug;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.world.worldgen.PosUtil;

public abstract class CacheLayer<A> extends Layer<Reference<A>> {
	@VisibleForDebug
	public Long2ObjectMap<Reference<A>> cache;
	
	public CacheLayer(int size) {
		super(size);
		this.cache = new Long2ObjectArrayMap<>();
	}
	
	protected abstract CompletableFuture<A> provideUncached(int layerX, int layerY, Executor executor);
	
	protected void addDebugScreenInfo(DebugScreenDisplayer display, ResourceLocation layerName, RandomState randomState, BlockPos pos, A chunk) {
	}
	
	protected Reference<A> provideParent(Layer<Reference<A>> parentLayer, int layerX, int layerY, Executor executor) {
		int parentLayerSize = parentLayer.size() / this.size();
		int parentLayerX = Math.floorDiv(layerX, parentLayerSize);
		int parentLayerY = Math.floorDiv(layerY, parentLayerSize);
		Reference<A> reference = parentLayer.provide(parentLayerX, parentLayerY, executor);
		reference.claim();
		return reference;
	}

	@Override
	public Reference<A> provide(int layerX, int layerY, Executor executor) {
		long key = layerKey(layerX, layerY);
		return this.cache.computeIfAbsent(key, (v) -> {
			CompletableFuture<A> value = this.provideUncached(layerX, layerY, executor).handle((t, e) -> {
				if(e != null) {
					e.printStackTrace();
				}
				return t;
			});
			return new ValueReference<>(value, () -> this.cache.remove(key));
		});
	}
	
	@Override
	public void addDebugScreenInfo(DebugScreenDisplayer display, ResourceLocation layerName, RandomState randomState, BlockPos pos) {
		super.addDebugScreenInfo(display, layerName, randomState, pos);
		display.addToGroup(layerName, "Active layer count: " + this.cache.size());
	
		int blockX = pos.getX();
		int blockZ = pos.getZ();
		int layerX = this.blockToLayer(blockX);
		int layerY = this.blockToLayer(blockZ);
		long layerKey = layerKey(layerX, layerY);
		Reference<A> reference = this.cache.get(layerKey);
		
		A value;
		if(reference != null && (value = reference.future().getNow(null)) != null) {
			reference.addDebugScreenInfo(display, layerName);
			this.addDebugScreenInfo(display, layerName, randomState, pos, value);
		}
	}
	
	@VisibleForDebug
	public static long layerKey(int layerX, int layerY) {
		return PosUtil.pack(layerX, layerY);
	}

	public static <A> A provideResultNow(Layer<Reference<A>> layer, int layerX, int layerY) {
		CompletableFuture<A> future = Layer.provideResult(layer, layerX, layerY).future();
		return future.resultNow();
	}
}
