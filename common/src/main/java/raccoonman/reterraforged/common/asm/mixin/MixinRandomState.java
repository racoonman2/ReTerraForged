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
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

@Mixin(RandomState.class)
@Implements(@Interface(iface = RandomStateExtension.class, prefix = ReTerraForged.MOD_ID + "$RandomStateExtension$"))
class MixinRandomState {
	private DensityFunction.Visitor visitor;
	private int seed;
	
	public Noise shift(Noise noise) {
		return noise.shift(this.seed);
	}
	
	public DensityFunction reterraforged$RandomStateExtension$wrap(DensityFunction input) {
		return input.mapAll(this.visitor);
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
		this.seed = (int) seed;
		this.visitor = (function) -> {
			if(function instanceof NoiseWrapper.Marker marker) {
				return new NoiseWrapper(marker.noise(), this.seed);
			}
			return function.mapAll(visitor);
		};
		return router.mapAll(this.visitor);
	}
}
