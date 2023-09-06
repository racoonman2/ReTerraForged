package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.QuartPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import raccoonman.reterraforged.common.level.levelgen.density.XZCache;

@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	@Shadow
	@Final
	private int firstNoiseX, 
				firstNoiseZ,
				cellCountXZ,
				cellWidth;
	
	@Inject(
		at = @At("HEAD"),
		cancellable = true,
		method = "wrapNew"
	)
	private void wrapNew(DensityFunction densityFunction, CallbackInfoReturnable<DensityFunction> callback) {
		if(densityFunction instanceof XZCache.Marker function) {
			XZCache cache = new XZCache(new Context(), function.function(), this.cellWidth * this.cellCountXZ + 1);
    		cache.fillCache(QuartPos.toBlock(this.firstNoiseX), QuartPos.toBlock(this.firstNoiseZ));
			callback.setReturnValue(cache);
		}
	}
	
	private class Context implements DensityFunction.FunctionContext {

		@Override
		public int blockX() {
			return ((NoiseChunk) (Object) MixinNoiseChunk.this).blockX() - QuartPos.toBlock(MixinNoiseChunk.this.firstNoiseX);
		}

		@Override
		public int blockY() {
			return 0;
		}

		@Override
		public int blockZ() {
			return ((NoiseChunk) (Object) MixinNoiseChunk.this).blockZ() - QuartPos.toBlock(MixinNoiseChunk.this.firstNoiseZ);
		}
	}
}