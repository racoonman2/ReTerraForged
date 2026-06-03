package raccoonman.reterraforged.mixin;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.level.GenerationChunkHolder;
import net.minecraft.util.StaticCache2D;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatusTasks;
import net.minecraft.world.level.chunk.status.ChunkStep;
import net.minecraft.world.level.chunk.status.WorldGenContext;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFGenerationChunkHolder;
import raccoonman.reterraforged.world.worldgen.LayerData;

@Mixin(ChunkStatusTasks.class)
public class MixinChunkStatusTasks {

	@Inject(
		method = "full",
		at = @At("HEAD")
	)
	private static void full(WorldGenContext worldGenContext, ChunkStep chunkStep, StaticCache2D<GenerationChunkHolder> staticCache2D, ChunkAccess chunkAccess, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> future) {
		ChunkPos chunkPos = chunkAccess.getPos();
		GenerationChunkHolder holder = staticCache2D.get(chunkPos.x, chunkPos.z);
		RTFGenerationChunkHolder rtfHolder = ExtensionUtil.cast(holder);
		LayerData layerData = rtfHolder.getLayerData();
		if(layerData == null) {
			return;
		}
		
		Executor executor = worldGenContext.mainThreadExecutor();
		executor.execute(() -> {
			layerData.close();
			rtfHolder.setLayerData(null);
		});
	}
}
