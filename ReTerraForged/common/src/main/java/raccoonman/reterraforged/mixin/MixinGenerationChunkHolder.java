package raccoonman.reterraforged.mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.Util;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ChunkResult;
import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkPyramid;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFGenerationChunkHolder;
import raccoonman.reterraforged.extensions.RTFRandomState;
import raccoonman.reterraforged.world.worldgen.LayerData;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;
import raccoonman.reterraforged.world.worldgen.layer.RequiredLayer;

@Mixin(GenerationChunkHolder.class)
public class MixinGenerationChunkHolder implements RTFGenerationChunkHolder {
	@Shadow
	@Final
	private ChunkPos pos;
	
	private LayerData layerData;
	
	@Override
	public LayerData getLayerData() {
		return this.layerData;
	}
	
	@Override
	public void setLayerData(LayerData layerData) {
		this.layerData = layerData;
	}
	
	@Inject(
		method = "rescheduleChunkTask", 
		at = @At("HEAD")
	)
	private void rescheduleChunkTask(ChunkMap chunkMap, @Nullable ChunkStatus chunkStatus, CallbackInfo callback) {
		if(chunkStatus != null && this.layerData == null) {
			this.layerData = this.scheduleLayerData(chunkMap);
		}
	}

//	@Inject(
//		method = "updateHighestAllowedStatus", 
//		at = @At("HEAD")
//	)
//	private void updateHighestAllowedStatus(CallbackInfo callback) {
//
//	}
	
	private LayerData scheduleLayerData(ChunkMap chunkMap) {
		RTFRandomState rtfRandomState = ExtensionUtil.cast(chunkMap.randomState);

		Set<RequiredLayer> requiredLayers = rtfRandomState.getRequiredLayers();

		int cacheRadius = ChunkPyramid.GENERATION_PYRAMID.getStepTo(ChunkStatus.FEATURES).getAccumulatedRadiusOf(ChunkStatus.STRUCTURE_STARTS) + 8; //12;
		int minX = this.pos.x - cacheRadius;
		int minZ = this.pos.z - cacheRadius;
		int maxX = this.pos.x + cacheRadius;
		int maxZ = this.pos.z + cacheRadius;
		ChunkPos min = new ChunkPos(minX, minZ);
		ChunkPos max = new ChunkPos(maxX, maxZ);
		int minBlockX = min.getMinBlockX();
		int minBlockZ = min.getMinBlockZ();
		int maxBlockX = max.getMaxBlockX();
		int maxBlockZ = max.getMaxBlockZ();
		
		Executor executor = Util.backgroundExecutor();//(task) -> chunkMap.worldgenTaskDispatcher.submit();
		List<CompletableFuture<?>> futures = new ArrayList<>();
		List<Reference<?>> references = new ArrayList<>();
		for(RequiredLayer requiredLayer : requiredLayers) {
			Layer<Reference<DensityFunction>> layer = requiredLayer.layer();
			int minLayerX = layer.blockToLayer(minBlockX);
			int minLayerY = layer.blockToLayer(minBlockZ);
			int maxLayerX = layer.blockToLayer(maxBlockX);
			int maxLayerY = layer.blockToLayer(maxBlockZ);
			for(int layerX = minLayerX; layerX <= maxLayerX; layerX++) {
				for(int layerY = minLayerY; layerY <= maxLayerY; layerY++) {
					Reference<DensityFunction> reference = layer.provide(layerX, layerY, executor);
					reference.claim();
					references.add(reference);
					
					CompletableFuture<DensityFunction> future = reference.future();
					if(!future.isDone()) {
						futures.add(future);
					}
				}
			}
		}
		CompletableFuture<ChunkResult<ChunkAccess>> resourceFuture = CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).thenApply((v) -> ChunkResult.of(null));
		return new LayerData(resourceFuture, references);
	}
}
