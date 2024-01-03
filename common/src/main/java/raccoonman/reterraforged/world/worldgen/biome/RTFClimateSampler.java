package raccoonman.reterraforged.world.worldgen.biome;

import org.jetbrains.annotations.Nullable;

import net.minecraft.world.level.levelgen.DensityFunction;

public interface RTFClimateSampler {
	void setUniqueness(DensityFunction function);
	
	@Nullable
	DensityFunction getUniqueness();
}
