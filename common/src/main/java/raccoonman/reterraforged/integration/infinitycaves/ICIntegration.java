package raccoonman.reterraforged.integration.infinitycaves;

import raccoonman.reterraforged.platform.ModLoaderUtil;

//infinity cave compat
public class ICIntegration {
	
	public static boolean isEnabled() {
		return ModLoaderUtil.isLoaded("infinity_cave");
	}
}
