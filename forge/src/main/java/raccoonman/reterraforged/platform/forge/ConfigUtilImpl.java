package raccoonman.reterraforged.platform.forge;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;

final class ConfigUtilImpl {

	public static Path getConfigPath() {
		return FMLPaths.CONFIGDIR.get();
	}
}
