
package raccoonman.reterraforged.mixin.compat;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.biome.Climate;
import raccoonman.reterraforged.extensions.compat.TBTargetPoint;

@Mixin(Climate.TargetPoint.class)
class MixinTBTargetPoint implements TBTargetPoint	{
	private double uniqueness = Double.NaN;

	@Override
	public double getUniqueness() {
		return this.uniqueness;
	}

	@Override
	public void setUniqueness(double uniqueness) {
		this.uniqueness = uniqueness;
	}
}
