package raccoonman.reterraforged.common.asm.extensions;

import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseChunk;

public interface RandomStateExtension {
	DensityFunction.Visitor visitor();
	
	default DensityFunction cache(DensityFunction function, NoiseChunk noiseChunk) {
		return noiseChunk.wrap(function.mapAll(this.visitor()));
	}
}
