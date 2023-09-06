package raccoonman.reterraforged.platform.config.forge;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;

public final class ConfigImpl {

	public static Path getConfigPath() {
		return FMLPaths.CONFIGDIR.get();
	}
}
