package raccoonman.reterraforged.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.biome.Climate;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.world.worldgen.biome.RTFTargetPoint;

@Mixin(Climate.TargetPoint.class)
@Implements(@Interface(iface = RTFTargetPoint.class, prefix = RTFCommon.MOD_ID + "$RTFTargetPoint$"))
class MixinTargetPoint {
	private double uniqueness = Double.NaN;
	
	public void reterraforged$RTFTargetPoint$setUniqueness(double uniqueness) {
		this.uniqueness = uniqueness;
	}
	
	@Nullable
	public double reterraforged$RTFTargetPoint$getUniqueness() {
		return this.uniqueness;
	}
}
