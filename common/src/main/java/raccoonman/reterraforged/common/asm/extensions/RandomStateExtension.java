package raccoonman.reterraforged.common.asm.extensions;

import net.minecraft.world.level.levelgen.DensityFunction;

public interface RandomStateExtension {
	DensityFunction.Visitor visitor();
}
