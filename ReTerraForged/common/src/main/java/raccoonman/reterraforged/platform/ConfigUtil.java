package raccoonman.reterraforged.platform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import dev.architectury.injectables.annotations.ExpectPlatform;
import raccoonman.reterraforged.RTFCommon;

public class ConfigUtil {
	public static final Path RTF_CONFIG_PATH = getRootConfigPath().resolve(RTFCommon.MOD_ID);
	public static final Path PRESET_PATH = RTF_CONFIG_PATH.resolve("presets");
	public static final Path EXPORT_PATH = RTF_CONFIG_PATH.resolve("exports");
	public static final Path DEBUG_PATH = RTF_CONFIG_PATH.resolve("debug");
	public static final Path SETTINGS_PATH = RTF_CONFIG_PATH.resolve("settings.json");
	
	public static boolean makeChildDirectories(Path... children) throws IOException {
		boolean result = makeDirectory(RTF_CONFIG_PATH);
		for(Path child : children) {
			result |= makeDirectory(child);
		}
		return result;
	}

	public static boolean makeChildFiles(Path... children) throws IOException {
		boolean result = makeDirectory(RTF_CONFIG_PATH);
		for(Path child : children) {
			result |= makeFile(child);
		}
		return result;
	}
	
	private static boolean makeDirectory(Path path) throws IOException {
		if(!Files.exists(path)) {
			Files.createDirectory(path);
			return true;
		}
		return false;
	}

	private static boolean makeFile(Path path) throws IOException {
		if(!Files.exists(path)) {
			Files.createFile(path);
			return true;
		}
		return false;
	}
	
	@ExpectPlatform
	public static Path getRootConfigPath() {
		throw new IllegalStateException();
	}
}
