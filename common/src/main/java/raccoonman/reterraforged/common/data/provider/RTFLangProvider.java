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

		// TODO make the ordering of these consistent with RTFTranslationKeys
		@Override
		protected void addTranslations() {
			this.add(RTFTranslationKeys.METADATA_DESCRIPTION, "ReTerraForged resources");
			this.add(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION, "ReTerraForged preset");
			this.add(RTFTranslationKeys.NO_ERROR_MESSAGE, "{No error message}");
			
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS, "Couldn't find any legacy presets");
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_TITLE, "Presets & Defaults");
			this.add(RTFTranslationKeys.GUI_BEAUTIFUL_PRESET_NAME, "ReTerraForged - Beautiful");
			this.add(RTFTranslationKeys.GUI_WORLD_SETTINGS_TITLE, "World Settings");
			this.add(RTFTranslationKeys.GUI_CLIMATE_SETTINGS_TITLE, "Climate Settings");
			this.add(RTFTranslationKeys.GUI_TERRAIN_SETTINGS_TITLE, "Terrain Settings");
			this.add(RTFTranslationKeys.GUI_RIVER_SETTINGS_TITLE, "River Settings");
			this.add(RTFTranslationKeys.GUI_FILTER_SETTINGS_TITLE, "Filter Settings");

			this.add(RTFTranslationKeys.GUI_BUTTON_TRUE, "true");
			this.add(RTFTranslationKeys.GUI_BUTTON_FALSE, "false");
			this.add(RTFTranslationKeys.GUI_BUTTON_CREATE, "Create");
			this.add(RTFTranslationKeys.GUI_BUTTON_COPY, "Copy");
			this.add(RTFTranslationKeys.GUI_BUTTON_DELETE, "Delete");
			this.add(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY, "Import Legacy");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE, "Continent Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE, "Continent Shape");
			this.add(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE, "Spawn Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET, "Seed Offset");
			this.add(RTFTranslationKeys.GUI_BUTTON_BIOME_EDGE_TYPE, "Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_TERRAIN_SEED_OFFSET, "Terrain Seed Offset");
			this.add(RTFTranslationKeys.GUI_BUTTON_FANCY_MOUNTAINS, "Fancy Mountains");
			
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE, "Continent Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER, "Continent Jitter");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING, "Continent Skipping");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE, "Continent Size Variance");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES , "Continent Noise Octaves");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN, "Continent Noise Gain");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY, "Continent Noise Lacunarity");
			this.add(RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN, "Deep Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN, "Shallow Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_BEACH, "Beach Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_COAST, "Coast Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_INLAND, "Inland Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT, "World Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_MIN_Y, "Min Y Level");
			this.add(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL, "Sea Level");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_SCALE, "Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_FALLOFF, "Falloff");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MIN, "Min");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MAX, "Max");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_BIAS, "Bias");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_SCALE, "Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_FALLOFF, "Falloff");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MIN, "Min");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MAX, "Max");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_BIAS, "Bias");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_SIZE, "Biome Size");
			this.add(RTFTranslationKeys.GUI_SLIDER_MACRO_NOISE_SIZE, "Macro Noise Size");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_SCALE, "Biome Warp Size");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_STRENGTH, "Biome Warp Strength");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_SCALE, "Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_OCTAVES, "Octaves");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_GAIN, "Gain");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_LACUNARITY, "Lacunarity");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_STRENGTH, "Strength");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_REGION_SIZE, "Terrain Region Size");
			this.add(RTFTranslationKeys.GUI_SLIDER_GLOBAL_VERTICAL_SCALE, "Global Vertical Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_GLOBAL_HORIZONTAL_SCALE, "Global Horizontal Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, "Weight");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, "Base Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, "Vertical Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, "Horizontal Scale");

			this.add(RTFTranslationKeys.GUI_LABEL_CONTINENT, "Continent");
			this.add(RTFTranslationKeys.GUI_LABEL_CONTROL_POINTS, "Control Points");
			this.add(RTFTranslationKeys.GUI_LABEL_PROPERTIES, "Properties");
			this.add(RTFTranslationKeys.GUI_LABEL_TEMPERATURE, "Temperature");
			this.add(RTFTranslationKeys.GUI_LABEL_MOISTURE, "Moisture");
			this.add(RTFTranslationKeys.GUI_LABEL_BIOME_SHAPE, "Biome Shape");
			this.add(RTFTranslationKeys.GUI_LABEL_BIOME_EDGE_SHAPE, "Biome Edge Shape");
			this.add(RTFTranslationKeys.GUI_LABEL_GENERAL, "General");
			this.add(RTFTranslationKeys.GUI_LABEL_STEPPE, "Steppe");
			this.add(RTFTranslationKeys.GUI_LABEL_PLAINS, "Plains");
			this.add(RTFTranslationKeys.GUI_LABEL_HILLS, "Hills");
			this.add(RTFTranslationKeys.GUI_LABEL_DALES, "Dales");
			this.add(RTFTranslationKeys.GUI_LABEL_PLATEAU, "Plateau");
			this.add(RTFTranslationKeys.GUI_LABEL_BADLANDS, "Badlands");
			this.add(RTFTranslationKeys.GUI_LABEL_TORRIDONIAN, "Torridonian");
			this.add(RTFTranslationKeys.GUI_LABEL_MOUNTAINS, "Mountains");
			this.add(RTFTranslationKeys.GUI_LABEL_VOLCANO, "Volcano");
			
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_CREATE), "Failed to create preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_COPY), "Failed to copy preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_DELETE), "Failed to delete preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY), "Failed to import legacy presets");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE), "Controls the continent generator type");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE), "Controls how continent shapes are calculated. You may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE), "Set whether spawn should be close to x=0,z=0 or the centre of the nearest continent");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET), "A seed offset used to randomise climate distribution");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_BIOME_EDGE_TYPE), "The noise type");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_TERRAIN_SEED_OFFSET), "A seed offset used to randomise terrain distribution");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_FANCY_MOUNTAINS), "Carries out extra processing on mountains to make them look even nicer. Can be disabled to improve performance slightly.");
			
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE), "Controls the size of continents. You may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER), "Controls how much continent centers are offset from the underlying noise grid.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING), "Reduces the number of continents to create more vast oceans.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE), "Increases the variance of continent sizes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES), "The number of octaves of noise used to distort the continent.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN), "The contribution strength of each noise octave.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY), "The frequency multiplier for each noise octave.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN), "Controls the point above which deep oceans transition into shallow oceans. The greater the gap to the shallow ocean slider, the more gradual the transition.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN), "Controls the point above which shallow oceans transition into coastal terrain. The greater the gap to the coast slider, the more gradual the transition.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BEACH), "Controls how much of the coastal terrain is assigned to beach biomes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_COAST), "Controls the size of coastal regions and is also the point below which inland terrain transitions into oceans. Certain biomes such as Mushroom Fields only generate in coastal areas.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_INLAND), "Controls the overall transition from ocean to inland terrain.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT), "Controls the world height");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MIN_Y), "Controls the minimum y level");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL), "Controls the sea level");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_SCALE), "The horizontal scale");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_FALLOFF), "How quickly values transition from an extremity");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MIN), "The lower limit of the range");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MAX), "The upper limit of the range");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_BIAS), "The bias towards either end of the range");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_SCALE), "The horizontal scale");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_FALLOFF), "How quickly values transition from an extremity");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MIN), "The lower limit of the range");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MAX), "The upper limit of the range");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_BIAS), "The bias towards either end of the range");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_SIZE), "Controls the size of individual biomes");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MACRO_NOISE_SIZE), "Macro noise is used to group large areas of biomes into a single type (such as deserts)");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_SCALE), "Controls the scale of shape distortion for biomes");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_STRENGTH), "Controls the strength of shape distortion for biomes");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_SCALE), "Controls the scale of the noise");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_OCTAVES), "Controls the number of noise octaves");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_GAIN), "Controls the gain subsequent noise octaves");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_LACUNARITY), "Controls the lacunarity of subsequent noise octaves");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_STRENGTH), "Controls the strength of the noise");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_REGION_SIZE), "Controls the size of terrain regions");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_GLOBAL_VERTICAL_SCALE), "Globally controls the vertical scaling of terrain");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_GLOBAL_HORIZONTAL_SCALE), "Globally controls the horizontal scaling of terrain");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT), "Controls how common this terrain type is");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE), "Controls the base height of this terrain");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE), "Stretches or compresses the terrain vertically");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE), "Stretches or compresses the terrain horizontally");
		}
	}
}
