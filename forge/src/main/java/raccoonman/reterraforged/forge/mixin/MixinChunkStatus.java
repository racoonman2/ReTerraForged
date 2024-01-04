package raccoonman.reterraforged.forge.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.RTFRandomState;

@Mixin(ChunkStatus.class)
public class MixinChunkStatus {

	@Inject(
		at = @At("HEAD"),
		method = { "lambda$static$2", "m_304610_" },
		remap = false,
		require = 0
	)
	private static void lambda$static$2(ChunkStatus status, Executor executor, ServerLevel level, ChunkGenerator generator, StructureTemplateManager templateManager, ThreadedLevelLightEngine lightEngine, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> chunkLookup, List<ChunkAccess> regionChunks, ChunkAccess centerChunk, CallbackInfoReturnable<CompletableFuture<ChunkAccess>> callback) {
		RandomState randomState = level.getChunkSource().randomState();
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
			ChunkPos chunkPos = centerChunk.getPos();
			@Nullable
			GeneratorContext context = rtfRandomState.generatorContext();
			
			if(context != null) {
				context.cache.queueAtChunk(chunkPos.x, chunkPos.z);
			}
		}
	}
	
	@Inject(
		at = @At("TAIL"),
		method = { "lambda$static$11", "m_279978_" },
		remap = false,
		require = 0
	)
	private static void lambda$static$11(ChunkStatus status, ServerLevel level, ChunkGenerator generator, List<ChunkAccess> chunks, ChunkAccess centerChunk, CallbackInfo callback) {
		RandomState randomState = level.getChunkSource().randomState();
		if((Object) randomState instanceof RTFRandomState rtfRandomState) {
			ChunkPos chunkPos = centerChunk.getPos();
			@Nullable
			GeneratorContext context = rtfRandomState.generatorContext();
			
			if(context != null) {
				context.cache.dropAtChunk(chunkPos.x, chunkPos.z);
			}
		}
	}
}
