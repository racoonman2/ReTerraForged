package raccoonman.reterraforged.common.asm.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.levelgen.SurfaceRules.Context;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.asm.extensions.ContextExtension;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.ErosionRule;

@Mixin(Context.class)
@Implements(@Interface(iface = ContextExtension.class, prefix = ReTerraForged.MOD_ID + "$ContextExtension$"))
class MixinContext {
	@Nullable
	private ErosionRule.Rule erosionRule;
	
	public void reterraforged$ContextExtension$setErosionRule(@Nullable ErosionRule.Rule rule) {
		this.erosionRule = rule;
	}
	
	@Nullable
	public ErosionRule.Rule reterraforged$ContextExtension$getErosionRule() {
		return this.erosionRule;
	}
}
