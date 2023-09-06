package raccoonman.reterraforged.platform.config.fabric;

import java.nio.file.Path;

import net.fabricmc.loader.api.FabricLoader;

public final class ConfigImpl {

	public static Path getConfigPath() {
		return FabricLoader.getInstance().getConfigDir();
	}
}
