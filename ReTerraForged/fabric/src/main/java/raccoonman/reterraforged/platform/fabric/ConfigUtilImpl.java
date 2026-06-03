package raccoonman.reterraforged.platform.fabric;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigUtilImpl {

	public static Path getRootConfigPath() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
