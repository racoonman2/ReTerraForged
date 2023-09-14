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
	public static final String GUI_CLIMATE_SETTINGS_TITLE = resolve("gui.climateSettings.title");
	public static final String GUI_TERRAIN_SETTINGS_TITLE = resolve("gui.terrainSettings.title");
	public static final String GUI_RIVER_SETTINGS_TITLE = resolve("gui.riverSettings.title");
	public static final String GUI_FILTER_SETTINGS_TITLE = resolve("gui.filterSettings.title");

	public static final String GUI_BUTTON_TRUE = resolve("gui.button.true");
	public static final String GUI_BUTTON_FALSE = resolve("gui.button.false");
	public static final String GUI_BUTTON_CREATE = resolve("gui.button.create");
	public static final String GUI_BUTTON_COPY = resolve("gui.button.copy");
	public static final String GUI_BUTTON_DELETE = resolve("gui.button.delete");
	public static final String GUI_BUTTON_IMPORT_LEGACY = resolve("gui.button.importLegacy");
	public static final String GUI_BUTTON_CONTINENT_TYPE = resolve("gui.button.continentType");
	public static final String GUI_BUTTON_CONTINENT_SHAPE = resolve("gui.button.continentShape");
	public static final String GUI_BUTTON_SPAWN_TYPE = resolve("gui.button.spawnType");
	public static final String GUI_BUTTON_CLIMATE_SEED_OFFSET = resolve("gui.button.climateSeedOffset");
	public static final String GUI_BUTTON_BIOME_EDGE_TYPE = resolve("gui.button.biomeEdgeType");
	public static final String GUI_BUTTON_TERRAIN_SEED_OFFSET = resolve("gui.button.terrainSeedOffset");
	public static final String GUI_BUTTON_FANCY_MOUNTAINS = resolve("gui.button.fancyMountains");
	
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
	public static final String GUI_SLIDER_TEMPERATURE_SCALE = resolve("gui.slider.temperatureScale");
	public static final String GUI_SLIDER_TEMPERATURE_FALLOFF = resolve("gui.slider.temperatureFalloff");
	public static final String GUI_SLIDER_TEMPERATURE_MIN = resolve("gui.slider.temperatureMin");
	public static final String GUI_SLIDER_TEMPERATURE_MAX = resolve("gui.slider.temperatureMax");
	public static final String GUI_SLIDER_TEMPERATURE_BIAS = resolve("gui.slider.temperatureBias");
	public static final String GUI_SLIDER_MOISTURE_SCALE = resolve("gui.slider.moistureScale");
	public static final String GUI_SLIDER_MOISTURE_FALLOFF = resolve("gui.slider.moistureFalloff");
	public static final String GUI_SLIDER_MOISTURE_MIN = resolve("gui.slider.moistureMin");
	public static final String GUI_SLIDER_MOISTURE_MAX = resolve("gui.slider.moistureMax");
	public static final String GUI_SLIDER_MOISTURE_BIAS = resolve("gui.slider.moistureBias");
	public static final String GUI_SLIDER_BIOME_SIZE = resolve("gui.slider.biomeSize");
	public static final String GUI_SLIDER_MACRO_NOISE_SIZE = resolve("gui.slider.macroNoiseSize");
	public static final String GUI_SLIDER_BIOME_WARP_SCALE = resolve("gui.slider.biomeWarpScale");
	public static final String GUI_SLIDER_BIOME_WARP_STRENGTH = resolve("gui.slider.biomeWarpStrength");
	public static final String GUI_SLIDER_BIOME_EDGE_SCALE = resolve("gui.slider.biomeEdgeScale");
	public static final String GUI_SLIDER_BIOME_EDGE_OCTAVES = resolve("gui.slider.biomeEdgeOctaves");
	public static final String GUI_SLIDER_BIOME_EDGE_GAIN = resolve("gui.slider.biomeEdgeGain");
	public static final String GUI_SLIDER_BIOME_EDGE_LACUNARITY = resolve("gui.slider.biomeEdgeLacunarity");
	public static final String GUI_SLIDER_BIOME_EDGE_STRENGTH = resolve("gui.slider.biomeEdgeStrength");
	public static final String GUI_SLIDER_TERRAIN_REGION_SIZE = resolve("gui.slider.terrainRegionSize");
	public static final String GUI_SLIDER_GLOBAL_VERTICAL_SCALE = resolve("gui.slider.globalVerticalScale");
	public static final String GUI_SLIDER_GLOBAL_HORIZONTAL_SCALE = resolve("gui.slider.globalHorizontalScale");
	public static final String GUI_SLIDER_TERRAIN_WEIGHT = resolve("gui.slider.terrain.weight");
	public static final String GUI_SLIDER_TERRAIN_BASE_SCALE = resolve("gui.slider.terrain.baseScale");
	public static final String GUI_SLIDER_TERRAIN_VERTICAL_SCALE = resolve("gui.slider.terrain.verticalScale");
	public static final String GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE = resolve("gui.slider.terrain.horizontalScale");
	
	public static final String GUI_LABEL_CONTINENT = resolve("gui.label.continent");
	public static final String GUI_LABEL_CONTROL_POINTS = resolve("gui.label.controlPoints");
	public static final String GUI_LABEL_PROPERTIES = resolve("gui.label.properties");
	public static final String GUI_LABEL_TEMPERATURE = resolve("gui.label.temperature");
	public static final String GUI_LABEL_MOISTURE = resolve("gui.label.moisture");
	public static final String GUI_LABEL_BIOME_SHAPE = resolve("gui.label.biomeShape");
	public static final String GUI_LABEL_BIOME_EDGE_SHAPE = resolve("gui.label.biomeEdgeShape");
	public static final String GUI_LABEL_GENERAL = resolve("gui.label.general");
	public static final String GUI_LABEL_STEPPE = resolve("gui.label.steppe");
	public static final String GUI_LABEL_PLAINS = resolve("gui.label.plains");
	public static final String GUI_LABEL_HILLS = resolve("gui.label.hills");
	public static final String GUI_LABEL_DALES = resolve("gui.label.dales");
	public static final String GUI_LABEL_PLATEAU = resolve("gui.label.plateau");
	public static final String GUI_LABEL_BADLANDS = resolve("gui.label.badlands");
	public static final String GUI_LABEL_TORRIDONIAN = resolve("gui.label.torridonian");
	public static final String GUI_LABEL_MOUNTAINS = resolve("gui.label.mountains");
	public static final String GUI_LABEL_VOLCANO = resolve("gui.label.volcano");
	
	private static String resolve(String key) {
		return ReTerraForged.MOD_ID + "." + key;
	}
}