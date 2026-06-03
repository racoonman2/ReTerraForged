package raccoonman.reterraforged.mixin.compat;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.compat.TBClimateSampler;

@Mixin(Climate.Sampler.class)
class MixinTBClimateSampler implements TBClimateSampler {
	@Nullable
	private DensityFunction uniqueness;

	@Inject(
		at = @At("RETURN"), 
		method = "sample",
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void sample(int i, int j, int k, CallbackInfoReturnable<TargetPoint> callback, int l, int m, int n, DensityFunction.SinglePointContext ctx) {
		if(this.uniqueness != null) {
			TBClimateSampler.sample(ExtensionUtil.cast(this), callback.getReturnValue(), ctx);
		}
	}

	@Override
	public void setUniqueness(DensityFunction uniqueness) {
		this.uniqueness = uniqueness;
	}
	
	@Override
	public DensityFunction getUniqueness() {
		return this.uniqueness;
	}
}