package raccoonman.reterraforged.common.asm.mixin;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.mojang.datafixers.util.Either;

import net.minecraft.server.level.ChunkHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import raccoonman.reterraforged.common.asm.extensions.TerrainDataHolder;
import raccoonman.reterraforged.common.level.levelgen.generator.RTFChunkGenerator;

@Mixin(ChunkStatus.class)
class MixinChunkStatus {
	
	@Inject(
		at = @At("HEAD"),
		method = "method_39464",
		remap = false
	)
	private static void method_39464(ChunkStatus status, Executor executor, ServerLevel level, ChunkGenerator generator, StructureTemplateManager templateManager, ThreadedLevelLightEngine lightEngine, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> lookup, List<ChunkAccess> cache, ChunkAccess center, boolean retrogen, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> callback) {
		if(!center.getStatus().isOrAfter(ChunkStatus.FEATURES) && generator instanceof RTFChunkGenerator chunkGenerator && center instanceof TerrainDataHolder holder && holder.getTerrainData() == null) {
			ChunkPos pos = center.getPos();
			holder.setTerrainData(chunkGenerator.getTerrainGenerator().generate(pos.x, pos.z));
		}
	}
	
//	@Inject(
//		at = @At("HEAD"),
//		method = "method_38284",
//		remap = false,
//		cancellable = true
//	)
//	private static void method_38284(ChunkStatus var1, Executor var2, ServerLevel var3, ChunkGenerator var4, StructureTemplateManager var5, ThreadedLevelLightEngine var6, Function<ChunkAccess, CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> var7, List<ChunkAccess> var8, ChunkAccess var9, boolean var10, CallbackInfoReturnable<CompletableFuture<Either<ChunkAccess, ChunkHolder.ChunkLoadingFailure>>> callback) {
//		if(!var9.getStatus().isOrAfter(var1) && var4 instanceof RTFChunkGenerator chunkGenerator && var9 instanceof TerrainDataHolder holder) {
//			TerrainData terrainData = holder.getTerrainData();
//			ChunkPos pos = var9.getPos();
//			chunkGenerator.getErosionFilter().apply((int) var3.getSeed(), pos.x, pos.z, NoiseTileSize.DEFAULT, null, null, null);
//		}
//	}
}
