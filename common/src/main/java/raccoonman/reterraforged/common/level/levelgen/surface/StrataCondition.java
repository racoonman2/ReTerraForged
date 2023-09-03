package raccoonman.reterraforged.common.level.levelgen.surface;

import net.minecraft.world.level.levelgen.SurfaceRules;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;

public record StrataCondition(Noise selector) implements SurfaceRules.Condition {

	@Override
	public boolean test() {
		return false;
	}
}
