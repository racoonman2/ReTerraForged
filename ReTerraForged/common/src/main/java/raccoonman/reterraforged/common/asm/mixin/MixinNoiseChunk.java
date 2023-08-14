package raccoonman.reterraforged.common.asm.mixin;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.TerrainDataHolder;
import raccoonman.reterraforged.common.level.levelgen.densityfunctions.HeightDensityFunction;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainData;

@Implements({
	@Interface(iface = TerrainDataHolder.class, prefix = ReTerraForged.MOD_ID + "$terrainDataHolder$"),
})
@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	private TerrainData terrainData;
	private NoiseGeneratorSettings settings;
	
	public void reterraforged$terrainDataHolder$setTerrainData(TerrainData terrainData) {
		this.terrainData = terrainData;
	}
	
	public TerrainData reterraforged$terrainDataHolder$getTerrainData() {
		return this.terrainData;
	}
	
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
		if(densityFunction instanceof HeightDensityFunction.Marker) {
			callback.setReturnValue(new HeightDensityFunction(() -> this.terrainData, () -> this.settings));
		}
	}
	
	@Inject(
		at = @At("TAIL"),
		target = @Desc(
			value = "<init>",
			args = {
				int.class,
				RandomState.class,
				int.class,
				int.class,
				NoiseSettings.class,
				DensityFunctions.BeardifierOrMarker.class,
				NoiseGeneratorSettings.class,
				Aquifer.FluidPicker.class,
				Blender.class
			}
		)
	)
	public void _init_(int cellCountXZ, RandomState rand, int x, int z, NoiseSettings noiseSettings, DensityFunctions.BeardifierOrMarker marker, NoiseGeneratorSettings settings, Aquifer.FluidPicker fluidPicker, Blender blender, CallbackInfo callback) {
		this.settings = settings;
	}
}
