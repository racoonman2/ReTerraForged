package raccoonman.reterraforged.client.data;

import raccoonman.reterraforged.client.gui.Tooltips;
import raccoonman.reterraforged.common.ReTerraForged;

public final class RTFTranslationKeys {
	public static final String METADATA_DESCRIPTION = resolve("metadata.description");
	public static final String PRESET_METADATA_DESCRIPTION = resolve("preset.metadata.description");
	public static final String TOASTS_NO_ERROR_MESSAGE = resolve("toasts.error.noMessage");
	public static final String PRESET_CONFIG_APPLY_PRESET = resolve("presetConfig.applyPreset");
	public static final String PRESET_CONFIG_PAGE_PRESETS_LOAD_FAIL = Tooltips.failTranslationKey(resolve("presetConfig.page.presets.load"));
	public static final String PRESET_CONFIG_PAGE_PRESETS_TITLE = resolve("presetConfig.page.presets.title");
	public static final String PRESET_CONFIG_PAGE_PRESETS_CREATE = resolve("presetConfig.page.presets.create");
	public static final String PRESET_CONFIG_PAGE_PRESETS_SAVE = resolve("presetConfig.page.presets.save");
	public static final String PRESET_CONFIG_PAGE_PRESETS_EXPORT = resolve("presetConfig.page.presets.export");
	public static final String PRESET_CONFIG_PAGE_PRESETS_EXPORT_SUCCESS = resolve("presetConfig.page.presets.export.success");
	public static final String PRESET_CONFIG_PAGE_PRESETS_COPY = resolve("presetConfig.page.presets.copy");
	public static final String PRESET_CONFIG_PAGE_PRESETS_DELETE = resolve("presetConfig.page.presets.delete");
	public static final String PRESET_CONFIG_PAGE_PRESETS_IMPORT_LEGACY = resolve("presetConfig.page.presets.importLegacy");
	public static final String PRESET_CONFIG_PAGE_PRESETS_IMPORT_LEGACY_DISABLED = resolve("presetConfig.page.presets.importLegacy.missing");
	public static final String PRESET_CONFIG_PAGE_WORLD_SETTINGS_TITLE = resolve("presetConfig.page.worldSettings.title");
	public static final String PRESET_CONFIG_LABEL_CONTINENT = resolve("presetConfig.label.continent");
	public static final String PRESET_CONFIG_LABEL_CONTROL_POINTS = resolve("presetConfig.label.controlPoints");
	public static final String PRESET_CONFIG_LABEL_PROPERTIES = resolve("presetConfig.label.properties");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_TYPE = resolve("presetConfig.setting.continentType");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_SHAPE = resolve("presetConfig.setting.continentShape");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_SCALE = resolve("presetConfig.setting.continentScale");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_JITTER = resolve("presetConfig.setting.continentJitter");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_SKIPPING = resolve("presetConfig.setting.continentSkipping");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_SIZE_VARIANCE = resolve("presetConfig.setting.sizeVariance");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_NOISE_OCTAVES = resolve("presetConfig.setting.noiseOctaves");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_NOISE_GAIN = resolve("presetConfig.setting.noiseGain");
	public static final String PRESET_CONFIG_SETTING_CONTINENT_NOISE_LACUNARITY = resolve("presetConfig.setting.noiseLacunarity");
	public static final String PRESET_CONFIG_SETTING_DEEP_OCEAN = resolve("presetConfig.setting.deepOcean");
	public static final String PRESET_CONFIG_SETTING_SHALLOW_OCEAN = resolve("presetConfig.setting.shallowOcean");
	public static final String PRESET_CONFIG_SETTING_BEACH = resolve("presetConfig.setting.beach");
	public static final String PRESET_CONFIG_SETTING_COAST = resolve("presetConfig.setting.coast");
	public static final String PRESET_CONFIG_SETTING_INLAND = resolve("presetConfig.setting.inland");
	public static final String PRESET_CONFIG_SETTING_SPAWN_TYPE = resolve("presetConfig.setting.spawnType");
	public static final String PRESET_CONFIG_SETTING_WORLD_HEIGHT = resolve("presetConfig.setting.worldHeight");
	public static final String PRESET_CONFIG_SETTING_SEA_LEVEL = resolve("presetConfig.setting.seaLevel");
	
	private static String resolve(String key) {
		return ReTerraForged.MOD_ID + "." + key;
	}
}