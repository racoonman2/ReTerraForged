package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.QuartPos;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import raccoonman.reterraforged.common.level.levelgen.density.FlatCache;

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
	@Shadow
	@Final
    private DensityFunctions.BeardifierOrMarker beardifier;
	
	@Inject(
		at = @At("HEAD"),
		cancellable = true,
		method = "wrapNew"
	)
	private void wrapNew(DensityFunction densityFunction, CallbackInfoReturnable<DensityFunction> callback) {
		if(densityFunction instanceof DensityFunctions.BeardifierOrMarker marker) {
			//TODO make this configurable some how
			callback.setReturnValue(new DensityFunction.SimpleFunction() {
				
				@Override
				public double minValue() {
					return -1.0D;
				}
				
				@Override
				public double maxValue() {
					return 1.0D;
				}
				
				@Override
				public double compute(FunctionContext ctx) {
					return Math.max(0.0D, (float) MixinNoiseChunk.this.beardifier.compute(ctx) - 0.303125D);
				}
				
				@Override
				public KeyDispatchDataCodec<? extends DensityFunction> codec() {
					return null;
				}
			});
		}
		
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
	
//	@ModifyConstant(
//		constant = @Constant(doubleValue = 0.390625D),
//		target = @Desc(
//			value = "computePreliminarySurfaceLevel",
//			ret = int.class,
//			args = long.class
//		),
//		require = 1
//	)
//	private double computePreliminarySurfaceLevel(double old) {
//		// TODO don't hard code this
//		// obviously this won't make it into the final build
//		return 0.25D;
//	}
}