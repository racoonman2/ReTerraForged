package raccoonman.reterraforged.mixin;

import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.storage.ServerLevelData;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.RTFChunkGenerator;
import raccoonman.reterraforged.world.PointFinder;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
	
//	@Redirect(
//		method = "setInitialSpawn",
//		at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/world/level/biome/Climate$Sampler;findSpawnPosition()Lnet/minecraft/core/BlockPos;"
//		)
//	)
//    private static BlockPos setInitialSpawn(Climate.Sampler sampler, ServerLevel serverLevel, ServerLevelData serverLevelData, boolean bl, boolean bl2) {
//		ChunkGenerator generator = serverLevel.getChunkSource().getGenerator();
//		RTFChunkGenerator rtfChunkGenerator = ExtensionUtil.cast(generator);
//		
//		@Nullable
//		PointFinder spawnFinder = rtfChunkGenerator.getSpawnFinder();
//		if(spawnFinder == null) {
//			return sampler.findSpawnPosition();
//		}
//
//		RTFCommon.LOGGER.info("Searching for spawn point");
//		Optional<BlockPos> spawn = spawnFinder.findPoint(sampler.spawnTarget(), sampler);
//		if(spawn.isPresent()) {
//			BlockPos pos = spawn.get();
//			RTFCommon.LOGGER.info("Found spawn point at x:{}, z:{}", pos.getX(), pos.getZ());
//			return pos;
//		} else {
//			RTFCommon.LOGGER.info("Couldn't find spawn point");
//			return BlockPos.ZERO;
//		}
//    }
}
