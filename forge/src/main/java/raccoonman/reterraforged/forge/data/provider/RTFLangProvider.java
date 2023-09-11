package raccoonman.reterraforged.forge.data.provider;

import net.minecraft.data.PackOutput;
import raccoonman.reterraforged.client.data.RTFTranslationKeys;
import raccoonman.reterraforged.client.gui.Tooltips;
import raccoonman.reterraforged.common.ReTerraForged;

// TODO add some more languages
public final class RTFLangProvider {
	
	public static final class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output) {
			super(output, ReTerraForged.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add(RTFTranslationKeys.METADATA_DESCRIPTION, "ReTerraForged resources");
			this.add(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION, "ReTerraForged preset");
			this.add(RTFTranslationKeys.TOASTS_NO_ERROR_MESSAGE, "{No error message}");
			this.add(RTFTranslationKeys.PRESET_CONFIG_APPLY_PRESET, "Apply");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_TITLE, "Presets & Defaults");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_CREATE, "Create");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_SAVE, "Save");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_EXPORT, "Export");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_EXPORT_SUCCESS, "Exported Preset");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_COPY, "Copy");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_DELETE, "Delete");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_IMPORT_LEGACY, "Import Legacy");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_IMPORT_LEGACY_DISABLED, "Couldn't find legacy preset directory");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_CREATE), "Failed to create preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_SAVE), "Failed to save preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_EXPORT), "Failed to export preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_COPY), "Failed to copy preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_DELETE), "Failed to delete preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_IMPORT_LEGACY), "Failed to import preset");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_WORLD_SETTINGS_TITLE, "World Settings");
			this.add(RTFTranslationKeys.PRESET_CONFIG_LABEL_CONTINENT, "Continent");
			this.add(RTFTranslationKeys.PRESET_CONFIG_LABEL_CONTROL_POINTS, "Control Points");
			this.add(RTFTranslationKeys.PRESET_CONFIG_LABEL_PROPERTIES, "Properties");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_TYPE, "Continent Type");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SHAPE, "Continent Shape");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SCALE, "Continent Scale");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_JITTER, "Continent Jitter");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SKIPPING, "Continent Skipping");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SIZE_VARIANCE, "Continent Size Variance");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_NOISE_OCTAVES, "Continent Noise Octaves");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_NOISE_GAIN, "Continent Noise Gain");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_NOISE_LACUNARITY, "Continent Noise Lacunarity");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_DEEP_OCEAN, "Deep Ocean");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_SHALLOW_OCEAN, "Shallow Ocean");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_BEACH, "Beach");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_COAST, "Coast");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_INLAND, "Inland");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_SPAWN_TYPE, "Spawn Type");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_WORLD_HEIGHT, "World Height");
			this.add(RTFTranslationKeys.PRESET_CONFIG_SETTING_SEA_LEVEL, "Sea Level");
			this.add(RTFTranslationKeys.PRESET_CONFIG_PAGE_PRESETS_LOAD_FAIL, "Failed to load preset");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_TYPE), "Controls the continent generator type");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SHAPE), "Controls how continent shapes are calculated.\nYou may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SCALE), "Controls the size of continents.\nYou may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_JITTER), "Controls how much continent centers are offset from the underlying noise grid.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SKIPPING), "Reduces the number of continents to create more vast oceans.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_SIZE_VARIANCE), "Increases the variance of continent sizes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_NOISE_OCTAVES), "The number of octaves of noise used to distort the continent.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_NOISE_GAIN), "The contribution strength of each noise octave.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_CONTINENT_NOISE_LACUNARITY), "The frequency multiplier for each noise octave.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_DEEP_OCEAN), "Controls the point above which deep oceans transition into shallow oceans.\nThe greater the gap to the shallow ocean slider, the more gradual the transition.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_SHALLOW_OCEAN), "Controls the point above which shallow oceans transition into coastal terrain.\nThe greater the gap to the coast slider, the more gradual the transition.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_BEACH), "Controls how much of the coastal terrain is assigned to beach biomes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_COAST), "Controls the size of coastal regions and is also the point below\nwhich inland terrain transitions into oceans. Certain biomes such\nas Mushroom Fields only generate in coastal areas.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_INLAND), "Controls the overall transition from ocean to inland terrain.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_SPAWN_TYPE), "Set whether spawn should be close to x=0,z=0 or the centre of the nearest continent");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_WORLD_HEIGHT), "Controls the world height");
			this.add(Tooltips.translationKey(RTFTranslationKeys.PRESET_CONFIG_SETTING_SEA_LEVEL), "Controls the sea level");
		}
	}
}
