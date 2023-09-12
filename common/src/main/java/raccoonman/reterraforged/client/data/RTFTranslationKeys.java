package raccoonman.reterraforged.client.data;

import raccoonman.reterraforged.common.ReTerraForged;

public final class RTFTranslationKeys {
	public static final String METADATA_DESCRIPTION = resolve("metadata.description");
	public static final String PRESET_METADATA_DESCRIPTION = resolve("preset.metadata.description");
	public static final String NO_ERROR_MESSAGE = resolve("error.noMessage");

	public static final String GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS = resolve("gui.selectPreset.missingLegacyPresets");
	public static final String GUI_SELECT_PRESET_TITLE = resolve("gui.selectPreset.title");

	private static String resolve(String key) {
		return ReTerraForged.MOD_ID + "." + key;
	}
}