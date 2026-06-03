package raccoonman.reterraforged.mixin.compat;

import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import raccoonman.reterraforged.extensions.ExtensionUtil;
import raccoonman.reterraforged.extensions.compat.TBClimateSampler;

@Mixin(NoiseChunk.class)
class MixinTBNoiseChunk {
	@Nullable
	private DensityFunction uniqueness;

	@Inject(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;initialDensityWithoutJaggedness()Lnet/minecraft/world/level/levelgen/DensityFunction;"
		),
		method = "<init>"
	)
	private void init(int cellCount, RandomState randomState, int x, int z, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker, NoiseGeneratorSettings noiseGeneratorSettings, Aquifer.FluidPicker fluidPicker, Blender blender, CallbackInfo callback) {
		DensityFunction uniqueness = ExtensionUtil.<TBClimateSampler>cast(randomState.sampler()).getUniqueness();
		if(uniqueness != null) {
			this.uniqueness = uniqueness.mapAll(this::wrap);
    	}
	}
	
	@Inject(
		at = @At("RETURN"),
		method = "cachedClimateSampler"
	)
	private void cachedClimateSampler(NoiseRouter noiseRouter, List<Climate.ParameterPoint> list, CallbackInfoReturnable<Climate.Sampler> callback) {
		ExtensionUtil.<TBClimateSampler>cast(callback.getReturnValue()).setUniqueness(this.uniqueness);
    }

	@Shadow
    private DensityFunction wrap(DensityFunction densityFunction) {
		throw new IllegalStateException();
    }
}