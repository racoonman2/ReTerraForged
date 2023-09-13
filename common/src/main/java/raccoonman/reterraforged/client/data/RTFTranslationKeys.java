package raccoonman.reterraforged.client.data;

import raccoonman.reterraforged.common.ReTerraForged;

public final class RTFTranslationKeys {
	public static final String METADATA_DESCRIPTION = resolve("metadata.description");
	public static final String PRESET_METADATA_DESCRIPTION = resolve("preset.metadata.description");
	public static final String NO_ERROR_MESSAGE = resolve("error.noMessage");

	public static final String GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS = resolve("gui.selectPreset.missingLegacyPresets");
	public static final String GUI_SELECT_PRESET_TITLE = resolve("gui.selectPreset.title");
	public static final String GUI_BEAUTIFUL_PRESET_NAME = resolve("gui.preset.defaultName");
	public static final String GUI_WORLD_SETTINGS_TITLE = resolve("gui.worldSettings.title");

	public static final String GUI_BUTTON_CREATE = resolve("gui.button.create");
	public static final String GUI_BUTTON_COPY = resolve("gui.button.copy");
	public static final String GUI_BUTTON_DELETE = resolve("gui.button.delete");
	public static final String GUI_BUTTON_IMPORT_LEGACY = resolve("gui.button.importLegacy");
	public static final String GUI_BUTTON_CONTINENT_TYPE = resolve("gui.button.continentType");
	public static final String GUI_BUTTON_CONTINENT_SHAPE = resolve("gui.button.continentShape");
	public static final String GUI_BUTTON_SPAWN_TYPE = resolve("gui.button.spawnType");

	public static final String GUI_SLIDER_CONTINENT_SCALE = resolve("gui.slider.continentScale");
	public static final String GUI_SLIDER_CONTINENT_JITTER = resolve("gui.slider.continentJitter");
	public static final String GUI_SLIDER_CONTINENT_SKIPPING = resolve("gui.slider.continentSkipping");
	public static final String GUI_SLIDER_CONTINENT_SIZE_VARIANCE = resolve("gui.slider.continentSizeVariance");
	public static final String GUI_SLIDER_CONTINENT_NOISE_OCTAVES = resolve("gui.slider.continentNoiseOctaves");
	public static final String GUI_SLIDER_CONTINENT_NOISE_GAIN = resolve("gui.slider.continentNoiseGain");
	public static final String GUI_SLIDER_CONTINENT_NOISE_LACUNARITY = resolve("gui.slider.continentNoisLacunarity");
	public static final String GUI_SLIDER_DEEP_OCEAN = resolve("gui.slider.deepOcean");
	public static final String GUI_SLIDER_SHALLOW_OCEAN = resolve("gui.slider.shallowOcean");
	public static final String GUI_SLIDER_BEACH = resolve("gui.slider.beach");
	public static final String GUI_SLIDER_COAST = resolve("gui.slider.coast");
	public static final String GUI_SLIDER_INLAND = resolve("gui.slider.inland");
	public static final String GUI_SLIDER_WORLD_HEIGHT = resolve("gui.slider.worldHeight");
	public static final String GUI_SLIDER_MIN_Y = resolve("gui.slider.minY");
	public static final String GUI_SLIDER_SEA_LEVEL = resolve("gui.slider.seaLevel");
	
	public static final String GUI_LABEL_CONTINENT = resolve("gui.label.continent");
	public static final String GUI_LABEL_CONTROL_POINTS = resolve("gui.label.controlPoints");
	public static final String GUI_LABEL_PROPERTIES = resolve("gui.label.properties");
	
	private static String resolve(String key) {
		return ReTerraForged.MOD_ID + "." + key;
	}
}