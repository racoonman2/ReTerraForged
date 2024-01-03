package raccoonman.reterraforged.platform.fabric;

import net.fabricmc.loader.api.FabricLoader;

public final class ModLoaderUtilImpl {
	
	public static boolean isLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}
}
