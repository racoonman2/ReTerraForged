package raccoonman.reterraforged.common.level.levelgen.noise.density;

import net.minecraft.world.level.levelgen.DensityFunction;

public record ModifyBeardifier(DensityFunction beardifier, DensityFunction modifier) {

	public record Marker() {
		
	}
}
