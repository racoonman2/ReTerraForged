package raccoonman.reterraforged.common.asm.mixin; 

import org.jetbrains.annotations.Nullable;
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
import raccoonman.reterraforged.common.level.levelgen.density.CellSampler;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseSampler;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.CacheManager;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.WorldLookup;

@Mixin(RandomState.class)
@Implements(@Interface(iface = RandomStateExtension.class, prefix = ReTerraForged.MOD_ID + "$RandomStateExtension$"))
class MixinRandomState {
	private int noiseSeed;
	private DensityFunction.Visitor visitor;
	private GeneratorContext ctx;
	
	@Nullable
	public TileProvider reterraforged$RandomStateExtension$tileCache() {
		return this.ctx != null ? this.ctx.cache.get() : null;
	}
	
	public Noise reterraforged$RandomStateExtension$shift(Noise noise) {
		return noise.shift(this.noiseSeed);
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
		this.noiseSeed = (int) seed;
		this.visitor = (function) -> {
			if(function instanceof NoiseSampler.Marker marker) {
				return new NoiseSampler(marker.noise(), this.noiseSeed);
			}
			if(function instanceof CellSampler.Marker marker) {
				if(this.ctx == null) {
		        	CacheManager.get().clear();
					this.ctx = new GeneratorContext(marker.preset().value(), seed);
				}
				return new CellSampler(marker.channel(), new WorldLookup(this.ctx));
			}
			return function.mapAll(visitor);
		};
		return router.mapAll(this.visitor);
	}
}
