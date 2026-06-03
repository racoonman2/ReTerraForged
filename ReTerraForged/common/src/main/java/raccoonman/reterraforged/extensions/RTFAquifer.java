package raccoonman.reterraforged.extensions;

import org.apache.commons.lang3.mutable.MutableObject;

import net.minecraft.world.level.levelgen.DensityFunction;

public interface RTFAquifer {
	void setWaterLevel(MutableObject<DensityFunction> waterLevel);
}
