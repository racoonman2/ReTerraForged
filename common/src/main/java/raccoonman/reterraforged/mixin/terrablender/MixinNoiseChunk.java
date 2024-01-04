package raccoonman.reterraforged.mixin.terrablender;

import java.util.List;

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
import raccoonman.reterraforged.world.worldgen.biome.RTFClimateSampler;

@Mixin(NoiseChunk.class)
public class MixinNoiseChunk {
	private RandomState randomState;

	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
	private void NoiseChunk(int cellCount, RandomState randomState, int x, int z, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker beardifierOrMarker, NoiseGeneratorSettings noiseGeneratorSettings, Aquifer.FluidPicker fluidPicker, Blender blender, CallbackInfo callback) {
		this.randomState = randomState;
	}
	
	@Inject(
		at = @At("RETURN"),
		method = "cachedClimateSampler"
	)
	private void cachedClimateSampler(NoiseRouter noiseRouter, List<Climate.ParameterPoint> list, CallbackInfoReturnable<Climate.Sampler> callback) {
    	if((Object) callback.getReturnValue() instanceof RTFClimateSampler cachedSampler && (Object) this.randomState.sampler() instanceof RTFClimateSampler globalSampler) {
    		DensityFunction uniqueness = globalSampler.getUniqueness();

    		if(uniqueness != null) {
    			cachedSampler.setUniqueness(this.wrap(uniqueness));
    		}
    	}
    }

	@Shadow
    private DensityFunction wrap(DensityFunction densityFunction) {
		throw new IllegalStateException();
    }
}
