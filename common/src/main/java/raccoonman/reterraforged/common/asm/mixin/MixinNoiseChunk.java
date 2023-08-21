package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.QuartPos;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import raccoonman.reterraforged.common.level.levelgen.noise.density.FlatCache;

@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	@Shadow
	@Final
    private int cellWidth;
	@Shadow
	@Final
	private int cellCountXZ;
	@Shadow
	@Final
	private int firstNoiseX;
	@Shadow
	@Final
	private int firstNoiseZ;
	
	@Inject(
		at = @At("HEAD"),
		cancellable = true,
		target = @Desc(
			value = "wrapNew",
			args = DensityFunction.class,
			ret = DensityFunction.class
		)
	)
	private void wrapNew(DensityFunction densityFunction, CallbackInfoReturnable<DensityFunction> callback) {
		if(densityFunction instanceof FlatCache.Marker function) {
			FlatCache cache = new FlatCache(new DensityFunction.FunctionContext() {
				
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
			}, function.function(), this.cellWidth * this.cellCountXZ + 1);
    		cache.fillCache(QuartPos.toBlock(this.firstNoiseX), QuartPos.toBlock(this.firstNoiseZ));
			callback.setReturnValue(cache);
		}
	}
	
	@ModifyConstant(
		constant = @Constant(doubleValue = 0.390625D),
		method = "computePreliminarySurfaceLevel",
		require = 1
	)
	private double computePreliminarySurfaceLevel(double old) {
		return 0.25D;
	}
}