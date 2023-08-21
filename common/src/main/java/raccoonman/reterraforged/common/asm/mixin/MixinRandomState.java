package raccoonman.reterraforged.common.asm.mixin; 

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Desc;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
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
	private NoiseRouter RandomState(NoiseRouter router, DensityFunction.Visitor visitor, NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> params, final long seed) {
		return router.mapAll((function) -> {
			if(function instanceof NoiseDensityFunction.Marker marker) {
				return visitor.apply(new NoiseDensityFunction(marker.noise(), Long.hashCode(seed)));
			}
			return function.mapAll(visitor);
		});
	}
}
