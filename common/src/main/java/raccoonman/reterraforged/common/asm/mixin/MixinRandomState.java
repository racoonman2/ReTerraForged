package raccoonman.reterraforged.common.asm.mixin; 

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;

@Mixin(RandomState.class)
public class MixinRandomState {
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		),
		method = "<init>",
		require = 1
	)
	private NoiseRouter RandomState(NoiseRouter router, DensityFunction.Visitor visitor, NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> params, final long seed) {
		return router.mapAll((function) -> {
			if(function instanceof NoiseWrapper.Marker marker) {
//				TODO we can't use mapAll yet because equals() still uses identity for most Noise classes
//				return visitor.apply(new NoiseWrapper(marker.noise().value().mapAll((noise) -> {
//					if(noise instanceof WorldHeightMarker) {
//						return Source.constant(settings.noiseSettings().height());
//					}
//					
//					if(noise instanceof SeaLevelMarker) {
//						return Source.constant(settings.seaLevel());
//					}
//					return noise;
//				}), Long.hashCode(seed)));
				return visitor.apply(new NoiseWrapper(marker.noise().value(), Long.hashCode(seed)));
			}
			return function.mapAll(visitor);
		});
	}
}
