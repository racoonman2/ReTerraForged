package raccoonman.reterraforged.extensions.compat;

import net.minecraft.world.level.biome.Climate;

@Deprecated
public interface TBTargetPoint {
	double getUniqueness();
	
	void setUniqueness(double uniqueness);
	
	public static void copy(Climate.TargetPoint source, Climate.TargetPoint dest) {
		if((Object) source instanceof TBTargetPoint tbSource && (Object) dest instanceof TBTargetPoint tbDest) {
			tbDest.setUniqueness(tbSource.getUniqueness());
		}
	}
}