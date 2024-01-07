package raccoonman.reterraforged.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.SpawnFinder.Result;

@Mixin(Climate.SpawnFinder.class)
class MixinSpawnFinder {

//
//	@Redirect(
//		at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/world/level/biome/Climate$SpawnFinder;getSpawnPositionAndFitness(Ljava/util/List;Lnet/minecraft/world/level/biome/Climate$Sampler;II)Lnet/minecraft/world/level/biome/Climate$SpawnFinder$Result;"
//		),
//		method = "<init>",
//		require = 1
//	)
//	Result SpawnFinder(List<ParameterPoint> list, Climate.Sampler sampler, int i, int j) {
//		int centerX = 0;
//		int centerZ = 0;
//		RTFCommon.LOGGER.info("Marker");
//		if((Object) sampler instanceof RTFClimateSampler rtfClimateSampler) {
//			BlockPos center = rtfClimateSampler.getSpawnSearchCenter();
//			RTFCommon.LOGGER.info("Setting spawn search center {}", center);
//			centerX = center.getX();
//			centerZ = center.getZ();
//		}
//		return getSpawnPositionAndFitness(list, sampler, centerX, centerZ);
//    }
	
//	@ModifyArg(
//		at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/world/level/biome/Climate$SpawnFinder;getSpawnPositionAndFitness(Ljava/util/List;Lnet/minecraft/world/level/biome/Climate$Sampler;II)Lnet/minecraft/world/level/biome/Climate$SpawnFinder$Result;"
//		),
//		method = "<init>",
//		index = 2,
//		require = 1
//	)
//	int modifyX(List<ParameterPoint> points, Climate.Sampler sampler, int x, int z) {
//		if((Object) sampler instanceof RTFClimateSampler rtfClimateSampler) {
//			BlockPos center = rtfClimateSampler.getSpawnSearchCenter();
//			return center != null ? center.getX() : 0;
//		} else {
//			return 0;
//		}
//	}
//	
//	@ModifyArg(
//		at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/world/level/biome/Climate$SpawnFinder;getSpawnPositionAndFitness(Ljava/util/List;Lnet/minecraft/world/level/biome/Climate$Sampler;II)Lnet/minecraft/world/level/biome/Climate$SpawnFinder$Result;"
//		),
//		method = "<init>",
//		index = 3,
//		require = 1
//	)
//	int modifyZ(List<ParameterPoint> points, Climate.Sampler sampler, int x, int z) {
//		if((Object) sampler instanceof RTFClimateSampler rtfClimateSampler) {
//			BlockPos center = rtfClimateSampler.getSpawnSearchCenter();
//			return center != null ? center.getZ() : 0;
//		} else {
//			return 0;
//		}
//	}
//	
    @Shadow
    private static Result getSpawnPositionAndFitness(List<ParameterPoint> points, Climate.Sampler sampler, int x, int z) {
    	throw new IllegalStateException();
    }
}