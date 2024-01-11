package raccoonman.reterraforged.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.ParameterPoint;
import net.minecraft.world.level.biome.Climate.Sampler;
import raccoonman.reterraforged.world.worldgen.cell.biome.spawn.SpawnFinderFix;

// FIXME the mixin that we should actually be using just refuses to work for some reason

//@Mixin(targets = "net.minecraft.world.level.biome.Climate$SpawnFinder")
@Mixin(Climate.class)
class MixinSpawnFinder {

	@Inject(at = @At("HEAD"), method = "findSpawnPosition", cancellable = true)
    private static void findSpawnPosition(List<ParameterPoint> list, Sampler sampler, CallbackInfoReturnable<BlockPos> callback) {
    	callback.setReturnValue(new SpawnFinderFix(list, sampler).result.location());
    }
//	@ModifyArg(
//		at = @At(
//			value = "INVOKE",
//			target = "Lnet/minecraft/world/level/biome/Climate$SpawnFinder;getSpawnPositionAndFitness(Ljava/util/List;Lnet/minecraft/world/level/biome/Climate$Sampler;II)Lnet/minecraft/world/level/biome/Climate$SpawnFinder$Result;"
//		),
//		method = "<init>",
//		index = 2,
//		require = 1
//	)
//	private static int modifyX(List<ParameterPoint> points, Climate.Sampler sampler, int x, int z) {
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
//	private static int modifyZ(List<ParameterPoint> points, Climate.Sampler sampler, int x, int z) {
//		if((Object) sampler instanceof RTFClimateSampler rtfClimateSampler) {
//			BlockPos center = rtfClimateSampler.getSpawnSearchCenter();
//			return center != null ? center.getZ() : 0;
//		} else {
//			return 0;
//		}
//	}
}