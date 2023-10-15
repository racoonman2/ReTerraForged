package raccoonman.reterraforged.common.asm.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.ErosionRule;

@Mixin(Context.class)
@Implements(@Interface(iface = ContextExtension.class, prefix = ReTerraForged.MOD_ID + "$ContextExtension$"))
class MixinContext {
	@Nullable
	private ErosionRule erosionRule;
	
	public void reterraforged$ContextExtension$setErosionRule(@Nullable ErosionRule rule) {
		this.erosionRule = rule;
	}
	
	@Nullable
	public ErosionRule reterraforged$ContextExtension$getErosionRule() {
		return this.erosionRule;
	}
	
	@Inject(
		at = @At("HEAD"),
		method = "updateXZ"
	)
	public void updateXZ(int x, int z, CallbackInfo callback) {
		this.erosionRule = null;
	}
}
