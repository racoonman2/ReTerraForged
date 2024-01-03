package raccoonman.reterraforged.world.worldgen.densityfunction.tile.generation;

import net.minecraft.world.level.levelgen.DensityFunction;

public interface DerivativeFunction extends DensityFunction {
	DensityFunction source();
}
