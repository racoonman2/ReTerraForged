package raccoonman.reterraforged.compat.terrablender;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.DensityFunction;

public interface TBClimateSampler {
	void setSpawnSearchCenter(BlockPos center);
	
	void setUniqueness(DensityFunction function);
	
	@Nullable
	DensityFunction getUniqueness();
}
