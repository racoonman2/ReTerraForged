package raccoonman.reterraforged.common.asm.mixin;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.core.QuartPos;
import net.minecraft.world.level.levelgen.Aquifer;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.DensityFunctions;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseSettings;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import raccoonman.reterraforged.common.level.levelgen.noise.density.Cache2DFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.density.MutablePointContext;

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
//
//	@ModifyConstant(
//		target = @Desc(
//			value = "computePreliminarySurfaceLevel",
//			args = long.class,
//			ret = int.class
//		), 
//		require = 1,
//		constant = @Constant(doubleValue = 0.390625D)
//	)
//	private double computePreliminarySurfaceLevel(double old) { 
//		return this.functionCache != null ? 0.35D : old;
//	}
	
	private Map<DensityFunction, DensityFunction> cache = new HashMap<>();
	
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
		if(densityFunction instanceof Cache2DFunction.Marker function) {
			MutablePointContext ctx = new MutablePointContext();
			DensityFunction source = function.function();
			callback.setReturnValue(this.cache.computeIfAbsent(function, (k) -> {
				int width = QuartPos.toBlock(this.noiseSizeXZ);
				int startX = QuartPos.toBlock(this.firstNoiseX);
				int startZ = QuartPos.toBlock(this.firstNoiseZ);
				float[] cache = new float[width * width];
				for(int x = 0; x < width; x++) {
					for(int z = 0; z < width; z++) {
						ctx.x = startX + x;
						ctx.z = startZ + z;
						cache[(z * width + x)] = (float) source.compute(ctx);
					}
				}
				return new Cache2DFunction(startX, startZ, width, (float) source.minValue(), (float) source.maxValue(), cache, source);
			}));
		}
	}
}
