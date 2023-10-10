package raccoonman.reterraforged.common.asm.extensions;

import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public interface RandomStateExtension {
	Noise shift(Noise noise);
	
	DensityFunction wrap(DensityFunction input);
}
