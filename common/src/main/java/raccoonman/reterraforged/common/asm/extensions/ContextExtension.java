package raccoonman.reterraforged.common.asm.extensions;

import org.jetbrains.annotations.Nullable;

import raccoonman.reterraforged.common.level.levelgen.surface.rule.ErosionRule;

public interface ContextExtension {
	void setErosionRule(@Nullable ErosionRule rule);
	
	@Nullable
	ErosionRule getErosionRule();
}
