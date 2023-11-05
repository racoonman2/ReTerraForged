package raccoonman.reterraforged.platform.forge;

import java.nio.file.Path;

import net.minecraftforge.fml.loading.FMLPaths;

public final class ConfigUtilImpl {

	public static Path getConfigPath() {
		return FMLPaths.CONFIGDIR.get();
	}
}
