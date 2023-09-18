package raccoonman.reterraforged.common.asm.extensions;

import java.util.List;

import raccoonman.reterraforged.common.level.levelgen.surface.extension.ExtensionRuleSource;

public interface ContextExtension {
	List<ExtensionRuleSource.Extension> extensions();
}
