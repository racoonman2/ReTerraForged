package raccoonman.reterraforged.common.asm.mixin;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;
import raccoonman.reterraforged.common.level.levelgen.noise.density.FlatCache;

@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	@Shadow
	@Final
	private int firstNoiseX;
	@Shadow
	@Final
	private int firstNoiseZ;
	@Shadow
	@Final
	private int	noiseSizeXZ;
	@Shadow
	@Final
    private DensityFunction initialDensityNoJaggedness;
	@Shadow
	@Final
	private int cellStartBlockX, cellStartBlockZ;
	@Shadow
	@Final
    private int cellWidth;
	@Shadow
	private int inCellX;
	@Shadow
	private int inCellY;
	@Shadow
	private int inCellZ;
	private List<FlatCache> flatCellCaches = new ArrayList<>();
	
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
					return MixinNoiseChunk.this.inCellX;
				}
				
				@Override
				public int blockY() {
					return MixinNoiseChunk.this.inCellY;
				}
				
				@Override
				public int blockZ() {
					return MixinNoiseChunk.this.inCellZ;
				}
			}, function.function(), this.cellWidth);
			this.flatCellCaches.add(cache);
			callback.setReturnValue(cache);
		}
	}
	

	@Inject(
		at = @At(
			value = "FIELD",
			shift = Shift.AFTER,
			ordinal = 0,
			opcode = Opcodes.IINC,
			desc = @Desc(
				value = "arrayInterpolationCounter",
				ret = long.class
			)
		),
		target = @Desc(
			value = "selectCellYZ",
			args = { int.class, int.class }
		)
	)
    public void selectCellYZ(int i, int j, CallbackInfo callback) {
    	for(FlatCache cache : this.flatCellCaches) {
    		cache.fillCache(this.cellStartBlockX, this.cellStartBlockZ);
    	}
    }
	
	@Shadow
	private DensityFunction wrapNew(DensityFunction densityFunction) {
		throw new UnsupportedOperationException();
    }
}	