package raccoonman.reterraforged.common.asm.extensions;

import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.common.level.levelgen.geology.Geology;
import raccoonman.reterraforged.common.level.levelgen.geology.Strata;

public interface SurfaceSystemExtension {
	Geology getOrCreateGeology(Strata strata, RandomState randomState);
}
