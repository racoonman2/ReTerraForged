package raccoonman.reterraforged.common.asm.extensions;

import java.util.List;

import raccoonman.reterraforged.common.level.levelgen.surface.filter.FilterSurfaceRuleSource;

public interface ContextExtension {
	List<FilterSurfaceRuleSource.Filter> filters();
}
