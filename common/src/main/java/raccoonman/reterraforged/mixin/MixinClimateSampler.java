package raccoonman.reterraforged.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.Climate.TargetPoint;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.world.worldgen.biome.RTFClimateSampler;
import raccoonman.reterraforged.world.worldgen.biome.RTFTargetPoint;

@Mixin(Climate.Sampler.class)
@Implements(@Interface(iface = RTFClimateSampler.class, prefix = RTFCommon.MOD_ID + "$RTFClimateSampler$"))
class MixinClimateSampler {
	@Nullable
	private DensityFunction uniqueness;
	
	@Inject(
		at = @At("RETURN"), 
		method = "sample",
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void sample(int i, int j, int k, CallbackInfoReturnable<TargetPoint> callback, int l, int m, int n, DensityFunction.SinglePointContext ctx) {
		if(this.uniqueness != null && (Object) callback.getReturnValue() instanceof RTFTargetPoint rtfTargetPoint) {
			rtfTargetPoint.setUniqueness(this.uniqueness.compute(ctx));
		}
	}
	
	public void reterraforged$RTFClimateSampler$setUniqueness(DensityFunction uniqueness) {
		this.uniqueness = uniqueness;
	}
	
	public DensityFunction reterraforged$RTFClimateSampler$getUniqueness() {
		return this.uniqueness;
	}
}
