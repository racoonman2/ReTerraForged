package raccoonman.reterraforged.platform.forge;

import net.minecraftforge.fml.loading.FMLLoader;

public class ModLoaderUtilImpl {
	
	public static boolean isLoaded(String modId) {
		return FMLLoader.getLoadingModList().getModFileById(modId) != null;
	}
}
