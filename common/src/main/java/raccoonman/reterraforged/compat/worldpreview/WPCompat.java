package raccoonman.reterraforged.compat.worldpreview;

import raccoonman.reterraforged.platform.ModLoaderUtil;

public class WPCompat {
	
	public static boolean isEnabled() {
		return ModLoaderUtil.isLoaded("world_preview");
	}
}
