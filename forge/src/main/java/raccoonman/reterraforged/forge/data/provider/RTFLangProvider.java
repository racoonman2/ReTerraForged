package raccoonman.reterraforged.forge.data.provider;

import net.minecraft.data.PackOutput;
import raccoonman.reterraforged.common.client.data.RTFTranslationKeys;

// TODO add some more languages
public final class RTFLangProvider {
	
	public static final class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output, String modid) {
			super(output, modid, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_CREATE, "Create Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SAVE, "Save Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_RESET, "Reset Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_DELETE, "Delete Preset");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_PRESETS, "Presets & Defaults");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_WORLD, "World Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_CLIMATE, "Climate Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_TERRAIN, "Terrain Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_RIVER, "River Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_FILTERS, "Filter Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_STRUCTURES, "Structure Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_PAGE_MISCELLANEOUS, "Miscellaneous Settings");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_CONTINENT, "Continent");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_CONTROL_POINTS, "Control Points");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_PROPERTIES, "Properties");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_TEMPERATURE, "Temperature");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_MOISTURE, "Moisture");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BIOME_SHAPE, "Biome Shape");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BIOME_EDGE_SHAPE, "Biome Edge Shape");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_GENERAL, "General");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_STEPPE, "Steppe");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_PLAINS, "Plains");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_HILLS, "Hills");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_DALES, "Dales");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_PLATEAU, "Plateau");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BADLANDS, "Badlands");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_TORRIDONIAN, "Torridonian");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_MOUNTAINS, "Mountains");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_MAIN_RIVERS, "Main Rivers");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_BRANCH_RIVERS, "Branch Rivers");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_LAKES, "Lakes");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_WETLANDS, "Wetlands");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_EROSION, "Erosion");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_LABEL_SMOOTHING, "Smoothing");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_TYPE, "Continent Type");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SHAPE, "Continent Shape");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SCALE, "Continent Scale");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_JITTER, "Continent Jitter");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SKIPPING, "Continent Skipping");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_SIZE_VARIANCE, "Continent Size Variance");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_OCTAVES, "Continent Noise Octaves");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_GAIN, "Continent Noise Gain");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_CONTINENT_NOISE_LACUNARITY, "Continent Noise Lacunarity");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_DEEP_OCEAN_POINT, "Deep Ocean");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_SHALLOW_OCEAN_POINT, "Shallow Ocean");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_BEACH_POINT, "Beach");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_COAST_POINT, "Coast");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_INLAND_POINT, "Inland");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_WORLD_HEIGHT, "World Height");
			this.add(RTFTranslationKeys.CUSTOMIZE_WORLD_SETTINGS_SEA_LEVEL, "Sea Level");
		}
	}
}
