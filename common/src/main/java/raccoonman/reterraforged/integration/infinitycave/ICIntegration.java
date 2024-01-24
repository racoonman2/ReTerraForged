package raccoonman.reterraforged.integration.infinitycave;

import raccoonman.reterraforged.platform.ModLoaderUtil;

//infinity cave compat
public class ICIntegration {
	
	public static boolean isEnabled() {
		return ModLoaderUtil.isLoaded("infinity_cave");
	}
}
