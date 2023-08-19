package raccoonman.reterraforged.common.asm.mixin; 

import java.lang.reflect.Field;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.common.level.levelgen.noise.density.NoiseDensityFunction;

@Mixin(RandomState.class)
public class MixinRandomState {
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			desc = @Desc(
				value = "mapAll",
				args = DensityFunction.Visitor.class,
				ret = NoiseRouter.class,
				owner = NoiseRouter.class
			)			
		),
		method = "<init>",
		require = 1
	)
	private NoiseRouter RandomState(NoiseRouter router, DensityFunction.Visitor visitor, NoiseGeneratorSettings settings) {
		return router.mapAll((function) -> {
			if(function instanceof NoiseDensityFunction.Marker marker) {
				return new NoiseDensityFunction(marker.noise().value(), findField(visitor, long.class).intValue());
			}
			return function;
		});
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T findField(Object instance, Class<T> type) {
		try {
			Field target = null;
			for(Field field : instance.getClass().getDeclaredFields()) {
				if(field.getType().equals(type)) {
					target = field;
				}
			}
			if(target != null) {
				return (T) target.get(instance);
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		throw new RuntimeException("Couldn't find field of type: " + type);
	}
}
