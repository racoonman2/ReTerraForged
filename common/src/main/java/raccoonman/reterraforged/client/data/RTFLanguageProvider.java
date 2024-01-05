package raccoonman.reterraforged.client.data;

import net.minecraft.data.PackOutput;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.client.gui.Tooltips;

// TODO add some more languages
public final class RTFLanguageProvider {
	
	public static final class EnglishUS extends LanguageProvider {

		public EnglishUS(PackOutput output) {
			super(output, RTFCommon.MOD_ID, "en_us");
		}

		@Override
		protected void addTranslations() {
			this.add(RTFTranslationKeys.METADATA_DESCRIPTION, "ReTerraForged resources");
			this.add(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION, "ReTerraForged preset");
			this.add(RTFTranslationKeys.NO_ERROR_MESSAGE, "{No error message}");
			
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS, "Couldn't find any legacy presets");
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_TITLE, "Presets & Defaults");
			this.add(RTFTranslationKeys.GUI_DEFAULT_PRESET_NAME, "Default");
			this.add(RTFTranslationKeys.GUI_BEAUTIFUL_PRESET_NAME, "TerraForged - Beautiful (Legacy)");
			this.add(RTFTranslationKeys.GUI_HUGE_BIOMES_PRESET_NAME, "TerraForged - Huge Biomes (Legacy)");
			this.add(RTFTranslationKeys.GUI_LITE_PRESET_NAME, "TerraForged - Lite (Legacy)");
			this.add(RTFTranslationKeys.GUI_VANILLAISH_PRESET_NAME, "TerraForged - Vanilla-ish (Legacy)");
			this.add(RTFTranslationKeys.GUI_WORLD_SETTINGS_TITLE, "World Settings");
			this.add(RTFTranslationKeys.GUI_CAVE_SETTINGS_TITLE, "Cave Settings (Experimental)");
			this.add(RTFTranslationKeys.GUI_CLIMATE_SETTINGS_TITLE, "Climate Settings");
			this.add(RTFTranslationKeys.GUI_TERRAIN_SETTINGS_TITLE, "Terrain Settings");
			this.add(RTFTranslationKeys.GUI_RIVER_SETTINGS_TITLE, "River Settings");
			this.add(RTFTranslationKeys.GUI_FILTER_SETTINGS_TITLE, "Filter Settings");
			this.add(RTFTranslationKeys.GUI_STRUCTURE_SETTINGS_TITLE, "Structure Settings");
			this.add(RTFTranslationKeys.GUI_MISCELLANEOUS_SETTINGS_TITLE, "Miscellaneous Settings");

			this.add(RTFTranslationKeys.GUI_BUTTON_TRUE, "true");
			this.add(RTFTranslationKeys.GUI_BUTTON_FALSE, "false");
			this.add(RTFTranslationKeys.GUI_BUTTON_CREATE, "Create");
			this.add(RTFTranslationKeys.GUI_BUTTON_COPY, "Copy");
			this.add(RTFTranslationKeys.GUI_BUTTON_DELETE, "Delete");
			this.add(RTFTranslationKeys.GUI_BUTTON_OPEN_PRESET_FOLDER, "Open Preset Folder");
			this.add(RTFTranslationKeys.GUI_BUTTON_OPEN_DATAPACK_FOLDER, "Open Datapack Folder");
			this.add(RTFTranslationKeys.GUI_BUTTON_EXPORT_AS_DATAPACK, "Export As Datapack");
			this.add(RTFTranslationKeys.GUI_BUTTON_EXPORT_SUCCESS, "Exported Preset");
			this.add(RTFTranslationKeys.GUI_BUTTON_SEED, "Seed");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE, "Continent Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE, "Continent Shape");
			this.add(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE, "Spawn Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_LARGE_ORE_VEINS, "Large Ore Veins");
			this.add(RTFTranslationKeys.GUI_BUTTON_LEGACY_CARVER_DISTRIBUTION, "Legacy Carver Distribution");
			this.add(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET, "Seed Offset");
			this.add(RTFTranslationKeys.GUI_BUTTON_BIOME_EDGE_TYPE, "Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_TERRAIN_SEED_OFFSET, "Terrain Seed Offset");
			this.add(RTFTranslationKeys.GUI_BUTTON_FANCY_MOUNTAINS, "Fancy Mountains");
			this.add(RTFTranslationKeys.GUI_BUTTON_RIVER_SEED_OFFSET, "Seed Offset");
			this.add(RTFTranslationKeys.GUI_BUTTON_SALT, "Salt");
			this.add(RTFTranslationKeys.GUI_BUTTON_DISABLED, "Disabled");
			this.add(RTFTranslationKeys.GUI_BUTTON_SMOOTH_LAYER_DECORATOR, "Smooth Layer Decorator");
			this.add(RTFTranslationKeys.GUI_BUTTON_STRATA_DECORATOR, "Strata Decorator");
			this.add(RTFTranslationKeys.GUI_BUTTON_ORE_COMPATIBLE_STONE_ONLY, "Ore Compatible Stone Only");
			this.add(RTFTranslationKeys.GUI_BUTTON_EROSION_DECORATOR, "Erosion Decorator");
			this.add(RTFTranslationKeys.GUI_BUTTON_PLAIN_STONE_EROSION, "Plain Stone Erosion");
			this.add(RTFTranslationKeys.GUI_BUTTON_NATURAL_SNOW_DECORATOR, "Natural Snow Decorator");
			this.add(RTFTranslationKeys.GUI_BUTTON_CUSTOM_BIOME_FEATURES, "Custom Biome Features");
			this.add(RTFTranslationKeys.GUI_BUTTON_VANILLA_SPRINGS, "Vanilla Springs");
			this.add(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_LAKES, "Vanilla Lava Lakes");
			this.add(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_SPRINGS, "Vanilla Lava Springs");

			this.add(RTFTranslationKeys.GUI_SLIDER_ZOOM, "Zoom");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE, "Continent Scale");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER, "Continent Jitter");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING, "Continent Skipping");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE, "Continent Size Variance");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES , "Continent Noise Octaves");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN, "Continent Noise Gain");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY, "Continent Noise Lacunarity");
			this.add(RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN, "Deep Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN, "Shallow Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_BEACH, "Beach");
			this.add(RTFTranslationKeys.GUI_SLIDER_COAST, "Coast");
			this.add(RTFTranslationKeys.GUI_SLIDER_INLAND, "Inland");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT, "World Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_DEPTH, "World Depth");
			this.add(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL, "Sea Level");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAVA_LEVEL, "Lava Level");
			this.add(RTFTranslationKeys.GUI_SLIDER_ENTRANCE_CAVE_PROBABILITY, "Entrance Cave Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_CHEESE_CAVE_DEPTH_OFFSET, "Cheese Cave Depth Offset");
			this.add(RTFTranslationKeys.GUI_SLIDER_CHEESE_CAVE_PROBABILITY, "Cheese Cave Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_SPAGHETTI_CAVE_PROBABILITY, "Spaghetti Cave Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_NOODLE_CAVE_PROBABILITY, "Noodle Cave Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_CAVE_CARVER_PROBABILITY, "Cave Carver Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_DEEP_CAVE_CARVER_PROBABILITY, "Deep Cave Carver Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_RAVINE_CARVER_PROBABILITY, "Ravine Carver Probability");
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
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_COUNT, "River Count");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_DEPTH, "Bed Depth");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_MIN_BANK_HEIGHT, "Min Bank Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_MAX_BANK_HEIGHT, "Max Bank Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_WIDTH, "Bed width");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_BANK_WIDTH, "Bank width");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_FADE, "Fade");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_CHANCE, "Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_START_DISTANCE, "Min Start Distance");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_START_DISTANCE, "Max Start Distance");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_DEPTH, "Depth");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MIN, "Size Min");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MAX, "Size Max");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_BANK_HEIGHT, "Min Bank Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_BANK_HEIGHT, "Max Bank Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_WETLAND_CHANCE, "Chance");
			this.add(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MIN, "Size Min");
			this.add(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MAX, "Size Max");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLETS_PER_CHUNK, "Droplets Per Chunk");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_LIFETIME, "Droplet Lifetime");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VOLUME, "Droplet Volume");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VELOCITY, "Droplet Velocity");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_RATE, "Erosion Rate");
			this.add(RTFTranslationKeys.GUI_SLIDER_DEPOSITE_RATE, "Deposite Rate");
			this.add(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_ITERATIONS, "Smoothing Iterations");
			this.add(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RADIUS, "Smoothing Radius");
			this.add(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RATE, "Smoothing Rate");
			this.add(RTFTranslationKeys.GUI_SLIDER_SPACING, "Spacing");
			this.add(RTFTranslationKeys.GUI_SLIDER_SEPARATION, "Separation");
			this.add(RTFTranslationKeys.GUI_SLIDER_STRATA_REGION_SIZE, "Strata Region Size");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOUNTAIN_BIOME_USAGE, "Mountain Biome Usage");
			this.add(RTFTranslationKeys.GUI_SLIDER_VOLCANO_BIOME_USAGE, "Volcano Biome Usage");

			//TODO move the trailing colon and space to PresetEditorPage
			this.add(RTFTranslationKeys.GUI_LABEL_PREVIEW_AREA, "Area: ");
			this.add(RTFTranslationKeys.GUI_LABEL_PREVIEW_TERRAIN, "Terrain: ");
			this.add(RTFTranslationKeys.GUI_LABEL_PREVIEW_BIOME, "Biome: ");
			this.add(RTFTranslationKeys.GUI_LABEL_CONTINENT, "Continent");
			this.add(RTFTranslationKeys.GUI_LABEL_CONTROL_POINTS, "Control Points");
			this.add(RTFTranslationKeys.GUI_LABEL_PROPERTIES, "Properties");
			this.add(RTFTranslationKeys.GUI_LABEL_NOISE_CAVES, "Noise Caves");
			this.add(RTFTranslationKeys.GUI_LABEL_CARVERS, "Carvers");
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
			this.add(RTFTranslationKeys.GUI_LABEL_MAIN_RIVERS, "Main Rivers");
			this.add(RTFTranslationKeys.GUI_LABEL_BRANCH_RIVERS, "Branch Rivers");
			this.add(RTFTranslationKeys.GUI_LABEL_LAKES, "Lakes");
			this.add(RTFTranslationKeys.GUI_LABEL_WETLANDS, "Wetlands");
			this.add(RTFTranslationKeys.GUI_LABEL_EROSION, "Erosion");
			this.add(RTFTranslationKeys.GUI_LABEL_SMOOTHING, "Smoothing");
			
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_CREATE), "Failed to create preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_COPY), "Failed to copy preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_DELETE), "Failed to delete preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_EXPORT_AS_DATAPACK), "Failed to export preset");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SEED), "Controls the world seed");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE), "Controls the continent generator type");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE), "Controls how continent shapes are calculated. You may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE), "Set whether spawn should be close to x=0,z=0 or the centre of the nearest continent");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_LARGE_ORE_VEINS), "Set whether large ore veins spawn");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_LEGACY_CARVER_DISTRIBUTION), "Set whether carvers use 1.16 distribution");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET), "A seed offset used to randomise climate distribution");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_BIOME_EDGE_TYPE), "The noise type");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_TERRAIN_SEED_OFFSET), "A seed offset used to randomise terrain distribution");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_FANCY_MOUNTAINS), "Carries out extra processing on mountains to make them look even nicer. Can be disabled to improve performance slightly.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_RIVER_SEED_OFFSET), "A seed offset used to randomise river distribution");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SALT), "A random seed value for the structure.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_DISABLED), "Prevent this structure from generating.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SMOOTH_LAYER_DECORATOR), "Modifies layer block levels (ie snow) to fit the terrain");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_STRATA_DECORATOR), "Generates strata (rock layers) instead of just stone");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_ORE_COMPATIBLE_STONE_ONLY), "Only use stone types that ores can generate in");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_EROSION_DECORATOR), "Replace surface materials where erosion has occurred");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_PLAIN_STONE_EROSION), "Changes most exposed rock surfaces to plain stone");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_NATURAL_SNOW_DECORATOR), "Removes snow from the terrain where it shouldn't naturally settle");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CUSTOM_BIOME_FEATURES), "Use custom biome features in place of vanilla ones (such as trees)");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_VANILLA_SPRINGS), "Allow vanilla springs (water source blocks) to generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_LAKES), "Allow vanilla lava-lakes to generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_SPRINGS), "Allow vanilla springs (lava source blocks) to generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_ZOOM), "Controls the zoom level of the preview map");
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
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WORLD_DEPTH), "Controls the minimum y level");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL), "Controls the sea level");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAVA_LEVEL), "Controls the lava level.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_ENTRANCE_CAVE_PROBABILITY), "Controls the probability that an entrance cave will generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CHEESE_CAVE_DEPTH_OFFSET), "Controls the depth at which cheese caves start to generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CHEESE_CAVE_PROBABILITY), "Controls probability that a cheese cave will generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SPAGHETTI_CAVE_PROBABILITY), "Controls the probability that a spaghetti cave will generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_NOODLE_CAVE_PROBABILITY), "Controls probability that a noodle cave will generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CAVE_CARVER_PROBABILITY), "Controls the probability that a cave carver will generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_DEEP_CAVE_CARVER_PROBABILITY), "Controls the probability that a deep cave carver will generate");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RAVINE_CARVER_PROBABILITY), "Controls the probability that a ravine carver will generate");
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
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_COUNT), "Controls the number of main rivers per continent.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_DEPTH), "Controls the depth of the river");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_MIN_BANK_HEIGHT), "Controls the height of river banks");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_MAX_BANK_HEIGHT), "Controls the height of river banks");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_WIDTH), "Controls the river-bed width");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_BANK_WIDTH), "Controls the river-banks width");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_FADE), "Controls how much rivers taper");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_CHANCE), "Controls the chance of a lake spawning");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_START_DISTANCE), "The minimum distance along a river that a lake will spawn");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_START_DISTANCE), "The maximum distance along a river that a lake will spawn");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_DEPTH), "The max depth of the lake");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MIN), "The minimum size of the lake");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MAX), "The maximum size of the lake");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_BANK_HEIGHT), "The minimum bank height");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_BANK_HEIGHT), "The maximum bank height");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WETLAND_CHANCE), "Controls how common wetlands are");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MIN), "The minimum size of the wetlands");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MAX), "The maximum size of the wetlands");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLETS_PER_CHUNK), "The average number of water droplets to simulate per chunk");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_LIFETIME), "Controls the number of iterations that a single water droplet is simulated for");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VOLUME), "Controls the starting volume of water that a simulated water droplet carries");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VELOCITY), "Controls the starting velocity of the simulated water droplet");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_RATE), "Controls how quickly material dissolves (during erosion)");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_DEPOSITE_RATE), "Controls how quickly material is deposited (during erosion)");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_ITERATIONS), "Controls the number of smoothing iterations");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RADIUS), "Controls the smoothing radius");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RATE), "Controls how strongly smoothing is applied");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SPACING),
				  "Controls the size of the grid used to generate the structure. "
				+ "Structures will attempt to generate once per grid cell. Larger "
				+ "spacing values will make the structure appear less frequently."
			);
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SEPARATION),
				  "Controls the minimum distance between instances of the structure. "
				+ "Larger values guarantee larger distances between structures of the "
				+ "same type but cause them to generate more 'grid-aligned'. "
				+ "Lower values will produce a more random distribution but may allow "
				+ "instances to generate closer together."
			);
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOUNTAIN_BIOME_USAGE), "The probability that mountainous terrain will be set to a mountain biome type.\nThis may help improve compatibility with mods that rely exclusively on mountain biomes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_VOLCANO_BIOME_USAGE), "The probability that volcano terrain will be set to a volcano biome type.\nThis may help improve compatibility with mods that rely exclusively on volcano biomes.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_STRATA_REGION_SIZE), "Controls the size of strata regions");
		}
	}
}
