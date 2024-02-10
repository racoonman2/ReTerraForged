package raccoonman.reterraforged.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;

public class ModLoaderUtilImpl {
	
	public static boolean isLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}
