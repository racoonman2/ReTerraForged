package raccoonman.reterraforged.mixin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.compat.terrablender.TBCompat;
import raccoonman.reterraforged.world.worldgen.surface.RTFSurfaceSystem;
import raccoonman.reterraforged.world.worldgen.surface.rule.StrataRule.Strata;
import terrablender.worldgen.surface.NamespacedSurfaceRuleSource;

@Mixin(SurfaceSystem.class)
@Implements(@Interface(iface = RTFSurfaceSystem.class, prefix = RTFCommon.MOD_ID + "$RTFSurfaceSystem$"))
class MixinSurfaceSystem {
	private static final ResourceLocation STRATA_RANDOM = RTFCommon.location("strata");
	private RandomSource strataRandom;
	private Map<ResourceLocation, List<Strata>> strata;
	
	@Inject(
		at = @At("TAIL"),
		method = "<init>"
	)
    public void SurfaceSystem(RandomState randomState, BlockState blockState, int i, PositionalRandomFactory positionalRandomFactory, CallbackInfo callback) {
    	this.strataRandom = randomState.random.fromHashOf(STRATA_RANDOM);
    	this.strata = new ConcurrentHashMap<>();
	}
	
	@ModifyVariable(
		at = @At("HEAD"),
		method = "buildSurface",
		name = "ruleSource",
		ordinal = 0,
		index = 7,
		argsOnly = true
	)
	public SurfaceRules.RuleSource buildSurface(SurfaceRules.RuleSource source) {
		// let our own surface api handle this instead
//		if(TBIntegration.isEnabled() && source instanceof NamespacedSurfaceRuleSource namespacedRule) {
//			return namespacedRule.base();
//		}	
		return source;
	}

	public List<Strata> reterraforged$RTFSurfaceSystem$getOrCreateStrata(ResourceLocation cacheId, Function<RandomSource, List<Strata>> factory) {
		return this.strata.computeIfAbsent(cacheId, (k) -> {
			return factory.apply(this.strataRandom.fork());
		});
	}
}
