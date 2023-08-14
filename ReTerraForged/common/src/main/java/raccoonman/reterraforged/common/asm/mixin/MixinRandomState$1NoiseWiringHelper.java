package raccoonman.reterraforged.common.asm.mixin;

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.densityfunctions.NoiseCompat;

@Mixin(targets = "net.minecraft.world.level.levelgen.RandomState$1NoiseWiringHelper")
class MixinRandomState$1NoiseWiringHelper {
	
	@Inject(
		at = @At("HEAD"),
		target = @Desc(
			value = "wrapNew",
			args = DensityFunction.class,
			ret = DensityFunction.class
		),
		cancellable = true,
		locals = LocalCapture.CAPTURE_FAILHARD
	)
	private void wrapNew(DensityFunction function, CallbackInfoReturnable<DensityFunction> callback) {
		long seed = this.extractSeed();

		if(function instanceof NoiseCompat.ModuleFactory factory) {
			callback.setReturnValue(factory.forSeed((int) seed));
		}
	}

	// this kinda sucks but oh well
	private static Field seed = null;
	private long extractSeed() {
		try {
			if(seed == null) {
				for(Field field : this.getClass().getDeclaredFields()) {
					if(field.isSynthetic() && field.getType().equals(long.class)) {
						seed = field;
					}
				}
			}
			if(seed != null) {
				return (long) seed.get(this);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException("Couldn't find seed");
	}
}
