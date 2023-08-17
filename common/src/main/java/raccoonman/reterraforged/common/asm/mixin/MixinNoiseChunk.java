package raccoonman.reterraforged.common.asm.mixin;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
import raccoonman.reterraforged.common.asm.extensions.TerrainHolder;
import raccoonman.reterraforged.common.level.levelgen.noise.density.LazyDensityFunction;
import raccoonman.reterraforged.common.level.levelgen.terrain.Terrain;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;

@Implements({
	@Interface(iface = TerrainHolder.class, prefix = ReTerraForged.MOD_ID + "$TerrainHolder$"),
})
@Mixin(NoiseChunk.class)
class MixinNoiseChunk {
	private Terrain terrain;
	
	public void reterraforged$TerrainHolder$setTerrain(Terrain terrain) {
		this.terrain = terrain;
	}
	
	public Terrain reterraforged$TerrainHolder$getTerrain() {
		return this.terrain;
	}
	
	@ModifyVariable(
		at = @At(
			value = "INVOKE",
			shift = Shift.AFTER,
			desc = @Desc(
				value = "<init>",
				owner = Object.class
			),
			opcode = Opcodes.INVOKESPECIAL
		),
		index = 7,
		ordinal = 0,
		name = "fluidPicker",
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
		),
		argsOnly = true
	)
	public Aquifer.FluidPicker NoiseChunk(Aquifer.FluidPicker old) {
		return old;
	}

	@ModifyConstant(
		target = @Desc(
			value = "computePreliminarySurfaceLevel",
			args = long.class,
			ret = int.class
		),
		require = 1,
		constant = @Constant(doubleValue = 0.390625D)
	)
	private double computePreliminarySurfaceLevel(double old) { 
		return this.terrain != null ? 0.35D : old;
	}
	
	private DensityFunction cachedTerrainFunction;
	
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
		if(densityFunction instanceof TerrainNoise marker) {
			if(this.cachedTerrainFunction == null) {
				this.cachedTerrainFunction = new LazyDensityFunction(() -> {
					return this.terrain;
				}, marker.minValue(), marker.maxValue());
			}
			
			callback.setReturnValue(this.cachedTerrainFunction);
		}
	}
}
