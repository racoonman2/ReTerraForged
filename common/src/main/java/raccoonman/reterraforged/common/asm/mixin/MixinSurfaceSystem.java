package raccoonman.reterraforged.common.asm.mixin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.SurfaceSystemExtension;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.StrataRule;

@Mixin(SurfaceSystem.class)
@Implements(@Interface(iface = SurfaceSystemExtension.class, prefix = ReTerraForged.MOD_ID + "$SurfaceSystemExtension$"))
class MixinSurfaceSystem {
	private static final ResourceLocation GEOLOGY_RANDOM = ReTerraForged.resolve("geology");
	private RandomState randomState;
	private Map<ResourceLocation, List<List<StrataRule.Layer>>> strata;
	
	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
    public void SurfaceSystem(RandomState randomState, BlockState blockState, int i, PositionalRandomFactory positionalRandomFactory, CallbackInfo callback) {
    	this.randomState = randomState;
    	this.strata = new ConcurrentHashMap<>();
	}
	
	public List<List<StrataRule.Layer>> reterraforged$SurfaceSystemExtension$getOrCreateStrata(ResourceLocation name, Function<RandomSource, List<List<StrataRule.Layer>>> strata) {
		return this.strata.computeIfAbsent(name, (k) -> {
			PositionalRandomFactory factory = this.randomState.getOrCreateRandomFactory(GEOLOGY_RANDOM);
			return strata.apply(factory.fromHashOf(k));
		});
	}
}
