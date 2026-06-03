package raccoonman.reterraforged.platform.neoforge;

import java.nio.file.Path;

import net.neoforged.fml.loading.FMLPaths;

public class ConfigUtilImpl {

	public static Path getRootConfigPath() {
		return FMLPaths.CONFIGDIR.get();
	}
}