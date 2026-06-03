package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import raccoonman.reterraforged.extensions.RTFWorldGenerationContext;

@Mixin(WorldGenerationContext.class)
public class MixinWorldGenerationContext implements RTFWorldGenerationContext {
	private ChunkGenerator chunkGenerator;
	
	@Inject(
		method = "<init>", 
		at = @At("TAIL")
	)
	public void init(ChunkGenerator chunkGenerator, LevelHeightAccessor levelHeightAccessor, CallbackInfo callback) {
		this.chunkGenerator = chunkGenerator;
	}

	@Override
	public ChunkGenerator getChunkGenerator() {
		return this.chunkGenerator;
	}
}
