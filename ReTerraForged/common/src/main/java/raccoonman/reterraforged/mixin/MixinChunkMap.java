package raccoonman.reterraforged.mixin;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ChunkGenerationTask;
import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFGenerationChunkHolder;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.Ticking;
import raccoonman.reterraforged.world.worldgen.LayerData;

@Mixin(ChunkMap.class)
public class MixinChunkMap {
	@Shadow
	@Final
	ServerLevel level;
	@Shadow
	@Final
	public RandomState randomState;
	@Shadow
	@Final
	private BlockableEventLoop<Runnable> mainThreadExecutor;
	@Shadow
	@Final
	private List<ChunkGenerationTask> pendingGenerationTasks;

	@Overwrite
	public void runGenerationTasks() {
		Iterator<ChunkGenerationTask> iterator = this.pendingGenerationTasks.iterator();
		while(iterator.hasNext()) {
			ChunkGenerationTask chunkGenerationTask = iterator.next();
			GenerationChunkHolder generationChunkHolder = chunkGenerationTask.getCenter();
			RTFGenerationChunkHolder rtfGenerationChunkHolder = ExtensionUtil.cast(generationChunkHolder);
			LayerData layerData = rtfGenerationChunkHolder.getLayerData();
			CompletableFuture<?> future = layerData.future();
			if(!future.isDone()) {
				continue;
			}
			iterator.remove();
			this.runGenerationTask(chunkGenerationTask);
		}
	}
	
	@Inject(
		method = "tick", 
		at = @At("TAIL")
	)
	protected void tick(CallbackInfo callback) {
		RTFRandomState rtfRandomState = ExtensionUtil.cast(this.randomState);
		MinecraftServer server = this.level.getServer();
		int tickCount = server.getTickCount();
		for(Ticking managedLayer : rtfRandomState.getManagedLayers()) {
			managedLayer.tick(tickCount);
		}
	}
	
	@Redirect(
		method = "method_60440",
		at = @At(
			value = "INVOKE",
			target = "Lit/unimi/dsi/fastutil/longs/Long2ObjectLinkedOpenHashMap;remove(JLjava/lang/Object;)Z"
		)
	)
	private boolean remove(Long2ObjectLinkedOpenHashMap<ChunkHolder> pendingUnloads, long chunkPos, Object o) {
		ChunkHolder chunkHolder = ExtensionUtil.cast(o);
		if(!pendingUnloads.remove(chunkPos, chunkHolder)) {
			return false;
		}
		
		RTFGenerationChunkHolder rtfGenerationChunkHolder = ExtensionUtil.cast(chunkHolder);
		LayerData layerData;
		if((layerData = rtfGenerationChunkHolder.getLayerData()) != null) {
			CompletableFuture<?> future = layerData.future();
			if(future.isDone()) {
				layerData.close();
			} else {
				future.thenRunAsync(layerData::close, this.mainThreadExecutor);
			}
		}
		return true;
	}
	
	@Shadow
	private void runGenerationTask(ChunkGenerationTask chunkGenerationTask) {
		throw new UnsupportedOperationException();
	}
}
