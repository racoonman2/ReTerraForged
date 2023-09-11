package raccoonman.reterraforged.platform.config;

import java.nio.file.Path;

import dev.architectury.injectables.annotations.ExpectPlatform;

public final class ConfigUtil {
	
	@ExpectPlatform
	public static Path getConfigPath() {
		throw new UnsupportedOperationException("Missing platform implementation");
	}
}
