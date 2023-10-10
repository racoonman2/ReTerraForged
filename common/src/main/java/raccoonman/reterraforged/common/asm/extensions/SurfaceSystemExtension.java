package raccoonman.reterraforged.common.asm.extensions;

import java.util.List;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import raccoonman.reterraforged.common.level.levelgen.surface.rule.StrataRule;

public interface SurfaceSystemExtension {
	List<List<StrataRule.Layer>> getOrCreateStrata(ResourceLocation name, Function<RandomSource, List<List<StrataRule.Layer>>> factory);
}
