package raccoonman.reterraforged.platform.neoforge;

import net.neoforged.fml.loading.FMLLoader;

public class ModLoaderUtilImpl {
	
	public static boolean isLoaded(String modId) {
		return FMLLoader.getCurrent().getLoadingModList().getModFileById(modId) != null;
	}
}