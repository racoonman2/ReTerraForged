package raccoonman.reterraforged.extensions.compat;

import net.minecraft.world.level.biome.Climate;

@Deprecated
public interface TBParameterList {
	int getIndex(Climate.TargetPoint point, int x, int y, int z);
}
