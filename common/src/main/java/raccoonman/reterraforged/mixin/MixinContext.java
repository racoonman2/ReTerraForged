package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.levelgen.SurfaceRules.Context;

@Mixin(Context.class)
public class MixinContext {
	
//	@ModifyVariable(
//		at = @At("HEAD"),
//		method = "<init>",
//		index = 4,
//		ordinal = 0,
//		name = "function",
//		argsOnly = true,
//		require = 1
//	)
//	private static Function<BlockPos, Holder<Biome>> Context(Function<BlockPos, Holder<Biome>> lookup, SurfaceSystem surfaceSystem, RandomState randomState, ChunkAccess chunkAccess, NoiseChunk noiseChunk, Function<BlockPos, Holder<Biome>> function, Registry<Biome> registry, WorldGenerationContext worldGenerationContext) {
//		return (pos) -> {
//			
//		};
//	}
}
