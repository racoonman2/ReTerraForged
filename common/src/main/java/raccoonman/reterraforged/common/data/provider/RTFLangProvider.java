package raccoonman.reterraforged.common.data.provider;

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
			this.add(RTFTranslationKeys.NO_ERROR_MESSAGE, "{No error message}");
			
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS, "Couldn't find any legacy presets");
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_TITLE, "Presets & Defaults");
			this.add(RTFTranslationKeys.GUI_BEAUTIFUL_PRESET_NAME, "ReTerraForged - Beautiful");

			this.add(RTFTranslationKeys.GUI_BUTTON_CREATE, "Create");
			this.add(RTFTranslationKeys.GUI_BUTTON_COPY, "Copy");
			this.add(RTFTranslationKeys.GUI_BUTTON_DELETE, "Delete");
			this.add(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY, "Import Legacy");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_CREATE), "Failed to create preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_COPY), "Failed to copy preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_DELETE), "Failed to delete preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY), "Failed to import legacy presets");

			this.add(RTFTranslationKeys.GUI_WORLD_SETTINGS_TITLE, "World Settings");
			
			this.add(RTFTranslationKeys.GUI_LABEL_CONTINENT, "Continent");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE, "Continent Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE, "Continent Shape");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE, "Continent Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER, "Continent Jitter");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING, "Continent Skipping");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE, "Continent Size Variance");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES , "Continent Noise Octaves");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN, "Continent Noise Gain");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY, "Continent Noise Lacunarity");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE), "Controls the continent generator type");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE), "Controls how continent shapes are calculated. You may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE), "Controls the size of continents. You may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER), "Controls how much continent centers are offset from the underlying noise grid.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING), "Reduces the number of continents to create more vast oceans.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE), "Increases the variance of continent sizes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES), "The number of octaves of noise used to distort the continent.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN), "The contribution strength of each noise octave.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY), "The frequency multiplier for each noise octave.");

			this.add(RTFTranslationKeys.GUI_LABEL_CONTROL_POINTS, "Control Points");
			this.add(RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN, "Deep Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN, "Shallow Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_BEACH, "Beach Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_COAST, "Coast Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_INLAND, "Inland Ocean");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN), "Controls the point above which deep oceans transition into shallow oceans. The greater the gap to the shallow ocean slider, the more gradual the transition.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN), "Controls the point above which shallow oceans transition into coastal terrain. The greater the gap to the coast slider, the more gradual the transition.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BEACH), "Controls how much of the coastal terrain is assigned to beach biomes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_COAST), "Controls the size of coastal regions and is also the point below which inland terrain transitions into oceans. Certain biomes such as Mushroom Fields only generate in coastal areas.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_INLAND), "Controls the overall transition from ocean to inland terrain.");
			
			this.add(RTFTranslationKeys.GUI_LABEL_PROPERTIES, "Properties");
			this.add(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE, "Spawn Type");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT, "World Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_MIN_Y, "Min Y Level");
			this.add(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL, "Sea Level");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE), "Set whether spawn should be close to x=0,z=0 or the centre of the nearest continent");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT), "Controls the world height");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MIN_Y), "Controls the minimum y level");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL), "Controls the sea level");
		}
	}
}
