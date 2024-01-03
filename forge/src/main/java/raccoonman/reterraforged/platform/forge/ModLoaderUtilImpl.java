package raccoonman.reterraforged.platform.forge;

import net.minecraftforge.fml.ModList;

public final class ModLoaderUtilImpl {
	
	public static boolean isLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}
}
