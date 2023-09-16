package raccoonman.reterraforged.common.asm.mixin; 

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.RandomStateExtension;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;

@Mixin(RandomState.class)
@Implements(@Interface(iface = RandomStateExtension.class, prefix = ReTerraForged.MOD_ID + "$RandomStateExtension$"))
class MixinRandomState {
	private DensityFunction.Visitor visitor;
	
	public DensityFunction.Visitor reterraforged$RandomStateExtension$visitor() {
		return this.visitor;
	}
	
	@Redirect(
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouter;"
		),
		method = "<init>",
		require = 1
	)
	private NoiseRouter RandomState(NoiseRouter router, DensityFunction.Visitor visitor, NoiseGeneratorSettings settings, HolderGetter<NormalNoise.NoiseParameters> params, final long seed) {
		this.visitor = (function) -> {
			if(function instanceof NoiseWrapper.Marker marker) {
				return visitor.apply(new NoiseWrapper(marker.noise(), (int) seed)); // we cast to int here instead of using Long.hashCode() to keep compatibility with 1.16 seeds
			}
			return function.mapAll(visitor);
		};
		return router.mapAll(this.visitor);
	}
}
