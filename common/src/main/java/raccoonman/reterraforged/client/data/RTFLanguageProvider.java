package raccoonman.reterraforged.client.data;

import net.minecraft.data.PackOutput;
import raccoonman.reterraforged.client.gui.Tooltips;
import raccoonman.reterraforged.common.ReTerraForged;

// TODO add some more languages
public final class RTFLanguageProvider {
	
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
			this.add(RTFTranslationKeys.GUI_DEFAULT_PRESET_NAME, "ReTerraForged - Default");
			this.add(RTFTranslationKeys.GUI_WORLD_SETTINGS_TITLE, "World Settings");
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
			this.add(RTFTranslationKeys.GUI_BUTTON_EXPORT, "Export");
			this.add(RTFTranslationKeys.GUI_BUTTON_EXPORT_SUCCESS, "Exported Preset");
			this.add(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY, "Import Legacy");
			this.add(RTFTranslationKeys.GUI_BUTTON_OPEN_PRESET_FOLDER, "Open Preset Folder");
			this.add(RTFTranslationKeys.GUI_BUTTON_SEED, "Seed");
			this.add(RTFTranslationKeys.GUI_BUTTON_NOISE, "Noise");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE, "Continent Type");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE, "Continent Shape");
			this.add(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE, "Spawn Type");
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
			this.add(RTFTranslationKeys.GUI_SLIDER_BEACH, "Beach Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_COAST, "Coast Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_INLAND, "Inland Ocean");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT, "World Height");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_DEPTH, "World Depth");
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
			this.add(RTFTranslationKeys.GUI_LABEL_MAIN_RIVERS, "Main Rivers");
			this.add(RTFTranslationKeys.GUI_LABEL_BRANCH_RIVERS, "Branch Rivers");
			this.add(RTFTranslationKeys.GUI_LABEL_LAKES, "Lakes");
			this.add(RTFTranslationKeys.GUI_LABEL_WETLANDS, "Wetlands");
			this.add(RTFTranslationKeys.GUI_LABEL_EROSION, "Erosion");
			this.add(RTFTranslationKeys.GUI_LABEL_SMOOTHING, "Smoothing");
			
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_CREATE), "Failed to create preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_COPY), "Failed to copy preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_DELETE), "Failed to delete preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_EXPORT), "Failed to export preset");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY), "Failed to import legacy presets");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SEED), "Controls the world seed");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_NOISE), "Controls the output of the preview map");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE), "Controls the continent generator type");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE), "Controls how continent shapes are calculated. You may also need to adjust the transition points to ensure beaches etc still form.");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE), "Set whether spawn should be close to x=0,z=0 or the centre of the nearest continent");
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
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_STRATA_REGION_SIZE), "Controls the size of strata regions");
		}
	}
	public static final class ZHCN extends LanguageProvider {

		public ZHCN(PackOutput output) {
			super(output, ReTerraForged.MOD_ID, "zh_cn");
		}
        @Override
		protected void addTranslations() {
			this.add(RTFTranslationKeys.NO_ERROR_MESSAGE, "{No error message}");
			this.add(RTFTranslationKeys.GUI_BUTTON_BIOME_EDGE_TYPE, "类型");
			this.add(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET, "种子偏移量");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE, "大陆形状");
			this.add(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE, "大陆类型");
			this.add(RTFTranslationKeys.GUI_BUTTON_COPY, "复制");
			this.add(RTFTranslationKeys.GUI_BUTTON_CREATE, "创建");
			this.add(RTFTranslationKeys.GUI_BUTTON_CUSTOM_BIOME_FEATURES, "自定义生物群系特征");
			this.add(RTFTranslationKeys.GUI_BUTTON_DELETE, "删除");
			this.add(RTFTranslationKeys.GUI_BUTTON_DISABLED, "禁用");
			this.add(RTFTranslationKeys.GUI_BUTTON_EROSION_DECORATOR, "侵蚀装饰");
			this.add(RTFTranslationKeys.GUI_BUTTON_EXPORT, "导出");
			this.add(RTFTranslationKeys.GUI_BUTTON_EXPORT_SUCCESS, "成功导出预设");
			this.add(RTFTranslationKeys.GUI_BUTTON_FALSE, "否");
			this.add(RTFTranslationKeys.GUI_BUTTON_FANCY_MOUNTAINS, "华丽的山脉");
			this.add(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY, "导入旧版预设");
			this.add(RTFTranslationKeys.GUI_BUTTON_NATURAL_SNOW_DECORATOR, "自然雪层装饰");
			this.add(RTFTranslationKeys.GUI_BUTTON_NOISE, "噪声");
			this.add(RTFTranslationKeys.GUI_BUTTON_OPEN_PRESET_FOLDER, "打开预设文件夹");
			this.add(RTFTranslationKeys.GUI_BUTTON_ORE_COMPATIBLE_STONE_ONLY, "仅生成可产生矿石的石头");
			this.add(RTFTranslationKeys.GUI_BUTTON_RIVER_SEED_OFFSET, "种子偏移量");
			this.add(RTFTranslationKeys.GUI_BUTTON_SALT, "随机化");
			this.add(RTFTranslationKeys.GUI_BUTTON_SEED, "种子");
			this.add(RTFTranslationKeys.GUI_BUTTON_SMOOTH_LAYER_DECORATOR, "平滑的覆盖层装饰");
			this.add(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE, "出生点决定方式");
			this.add(RTFTranslationKeys.GUI_BUTTON_PLAIN_STONE_EROSION, "普通石头侵蚀");
			this.add(RTFTranslationKeys.GUI_BUTTON_STRATA_DECORATOR, "岩层装饰");
			this.add(RTFTranslationKeys.GUI_BUTTON_TERRAIN_SEED_OFFSET, "种子偏移量");
			this.add(RTFTranslationKeys.GUI_BUTTON_TRUE, "是");
			this.add(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_LAKES, "原版岩浆湖");
			this.add(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_SPRINGS, "原版岩浆泉");
			this.add(RTFTranslationKeys.GUI_BUTTON_VANILLA_SPRINGS, "原版泉水");
			this.add(RTFTranslationKeys.GUI_CLIMATE_SETTINGS_TITLE, "气候设置");
			this.add(RTFTranslationKeys.GUI_FILTER_SETTINGS_TITLE, "侵蚀与平滑化");
			this.add(RTFTranslationKeys.GUI_LABEL_BADLANDS, "恶地");
			this.add(RTFTranslationKeys.GUI_LABEL_BIOME_EDGE_SHAPE, "生物群系边缘");
			this.add(RTFTranslationKeys.GUI_LABEL_BIOME_SHAPE, "生物群系形状");
			this.add(RTFTranslationKeys.GUI_LABEL_CONTINENT, "大陆");
			this.add(RTFTranslationKeys.GUI_LABEL_CONTROL_POINTS, "调整 基准点");
			this.add(RTFTranslationKeys.GUI_LABEL_DALES, "山谷");
			this.add(RTFTranslationKeys.GUI_LABEL_GENERAL, "全局");
			this.add(RTFTranslationKeys.GUI_LABEL_HILLS, "山丘");
			this.add(RTFTranslationKeys.GUI_LABEL_MOISTURE, "湿度");
			this.add(RTFTranslationKeys.GUI_LABEL_MOUNTAINS, "山脉");
			this.add(RTFTranslationKeys.GUI_LABEL_PLAINS, "平原");
			this.add(RTFTranslationKeys.GUI_LABEL_PLATEAU, "高原");
			this.add(RTFTranslationKeys.GUI_LABEL_PROPERTIES, "世界性质");
			this.add(RTFTranslationKeys.GUI_LABEL_STEPPE, "草原");
			this.add(RTFTranslationKeys.GUI_LABEL_TEMPERATURE, "温度");
			this.add(RTFTranslationKeys.GUI_LABEL_TORRIDONIAN, "Torridonian");
			this.add(RTFTranslationKeys.GUI_LABEL_VOLCANO, "火山");
			this.add(RTFTranslationKeys.GUI_LABEL_BRANCH_RIVERS, "支流");
			this.add(RTFTranslationKeys.GUI_LABEL_EROSION, "侵蚀");
			this.add(RTFTranslationKeys.GUI_LABEL_LAKES, "湖泊");
			this.add(RTFTranslationKeys.GUI_LABEL_MAIN_RIVERS, "干流");
			this.add(RTFTranslationKeys.GUI_LABEL_SMOOTHING, "平滑化");
			this.add(RTFTranslationKeys.GUI_LABEL_WETLANDS, "湿地");
			this.add(RTFTranslationKeys.GUI_MISCELLANEOUS_SETTINGS_TITLE, "杂项");
			this.add(RTFTranslationKeys.GUI_DEFAULT_PRESET_NAME, "ReTerraForged - 默认");
			this.add(RTFTranslationKeys.GUI_RIVER_SETTINGS_TITLE, "河流");
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_MISSING_LEGACY_PRESETS, "未找到旧版预设");
			this.add(RTFTranslationKeys.GUI_SELECT_PRESET_TITLE, "预设");
			this.add(RTFTranslationKeys.GUI_SLIDER_BEACH, "海滩");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_GAIN, "增益gain");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_LACUNARITY, "隙度lacunarity");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_OCTAVES, "倍频octave");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_SCALE, "规模");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_STRENGTH, "强度");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_SIZE, "尺寸");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_SCALE, "扭曲规模");
			this.add(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_STRENGTH, "扭曲强度");
			this.add(RTFTranslationKeys.GUI_SLIDER_COAST, "海岸");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER, "大陆偏移");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY, "大陆噪声隙度lacunarity");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN, "大陆噪声增益gain");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES, "大陆噪声倍频数octave");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE, "大陆尺寸");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE, "大陆尺寸多样性");
			this.add(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING, "大陆生成跳过");
			this.add(RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN, "深海");
			this.add(RTFTranslationKeys.GUI_SLIDER_DEPOSITE_RATE, "沉积强度");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_LIFETIME, "水滴存在时间");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VELOCITY, "水滴流速");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VOLUME, "水滴体积");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLETS_PER_CHUNK, "每区块水滴数量");
			this.add(RTFTranslationKeys.GUI_SLIDER_EROSION_RATE, "侵蚀强度");
			this.add(RTFTranslationKeys.GUI_SLIDER_GLOBAL_HORIZONTAL_SCALE, "全局水平缩放");
			this.add(RTFTranslationKeys.GUI_SLIDER_GLOBAL_VERTICAL_SCALE, "全局垂直缩放");
			this.add(RTFTranslationKeys.GUI_SLIDER_INLAND, "陆地");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_CHANCE, "概率");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_DEPTH, "深度");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_BANK_HEIGHT, "最大岸高");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_START_DISTANCE, "最大起始距离");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_BANK_HEIGHT, "最小岸高");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_START_DISTANCE, "最小起始距离");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MAX, "最大尺寸");
			this.add(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MIN, "最小尺寸");
			this.add(RTFTranslationKeys.GUI_SLIDER_MACRO_NOISE_SIZE, "宏观噪声大小");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_BIAS, "倾向");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_FALLOFF, "过渡");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MAX, "最大");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MIN, "最小");
			this.add(RTFTranslationKeys.GUI_SLIDER_MOISTURE_SCALE, "水平缩放");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_BANK_WIDTH, "两岸间距");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_DEPTH, "河床深度");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_WIDTH, "河床宽度");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_FADE, "源头渐隐速度");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_MAX_BANK_HEIGHT, "最大岸高");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_MIN_BANK_HEIGHT, "最小岸高");
			this.add(RTFTranslationKeys.GUI_SLIDER_RIVER_COUNT, "河流数量");
			this.add(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL, "海平面高度");
			this.add(RTFTranslationKeys.GUI_SLIDER_SEPARATION, "分割");
			this.add(RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN, "浅海");
			this.add(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_ITERATIONS, "迭代次数");
			this.add(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RADIUS, "平滑化计算半径");
			this.add(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RATE, "平滑化强度");
			this.add(RTFTranslationKeys.GUI_SLIDER_SPACING, "间隔");
			this.add(RTFTranslationKeys.GUI_SLIDER_STRATA_REGION_SIZE, "岩层区域大小");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_BIAS, "倾向");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_FALLOFF, "过渡");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MAX, "最大");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MIN, "最小");
			this.add(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_SCALE, "水平缩放");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE, "基础缩放");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE, "水平缩放");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE, "垂直缩放");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT, "权重");
			this.add(RTFTranslationKeys.GUI_SLIDER_TERRAIN_REGION_SIZE, "区域大小");
			this.add(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MAX, "最大尺寸");
			this.add(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MIN, "最小尺寸");
			this.add(RTFTranslationKeys.GUI_SLIDER_WETLAND_CHANCE, "概率");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_DEPTH, "世界深度");
			this.add(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT, "世界高度");
			this.add(RTFTranslationKeys.GUI_SLIDER_ZOOM, "放大倍数");
			this.add(RTFTranslationKeys.GUI_STRUCTURE_SETTINGS_TITLE, "特殊结构设置");
			this.add(RTFTranslationKeys.GUI_TERRAIN_SETTINGS_TITLE, "地形设置");
			this.add(RTFTranslationKeys.GUI_WORLD_SETTINGS_TITLE, "世界设置");
			this.add(RTFTranslationKeys.METADATA_DESCRIPTION, "ReTerraForged游戏资源");
			this.add(RTFTranslationKeys.PRESET_METADATA_DESCRIPTION, "ReTerraForged预设");

			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_BIOME_EDGE_TYPE), "用来生成边缘的噪声类型");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CLIMATE_SEED_OFFSET), "使气候分布随机化");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_SHAPE), "控制大陆形状，你可能也需要调整过渡基准点来保证沙滩等地形仍能够生成");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CONTINENT_TYPE), "控制大陆生成器的类型");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_CUSTOM_BIOME_FEATURES), "使用自定义生物群系特征来替换原版特征（比如树）");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_DISABLED), "不生成这个特殊结构");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_EROSION_DECORATOR), "替换被侵蚀的地表的材料");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_FANCY_MOUNTAINS), "对山脉进行特殊处理，使其看起来更好看。可以禁用此设置来轻微提升性能。");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_NATURAL_SNOW_DECORATOR), "移除非自然沉积的雪");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_NOISE), "控制预览地图的输出");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_ORE_COMPATIBLE_STONE_ONLY), "只使用可以生成矿物的石头种类");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_PLAIN_STONE_EROSION), "把大部分裸露于地表的石头替换为普通的石头");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_RIVER_SEED_OFFSET), "使河流分布随机化");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SALT), "使特殊结构分布随机化");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SEED), "预览图使用的世界种子");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SMOOTH_LAYER_DECORATOR), "使得雪层更加适应地形变化");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_SPAWN_TYPE), "出生点应该靠近x=0,z=0还是位于最近大陆的中央");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_STRATA_DECORATOR), "生成岩层（有层次的岩石），而非单纯的石头");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_TERRAIN_SEED_OFFSET), "使地貌分布随机化");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_LAKES), "允许原版岩浆湖生成");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_VANILLA_LAVA_SPRINGS), "允许原版岩浆泉（岩浆源方块）生成");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_BUTTON_VANILLA_SPRINGS), "允许原版泉水（水源方块）生成");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BEACH), "控制海岸地貌被转化为沙滩生物群系的比例");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_GAIN), "控制不同组噪声倍频octave之间的增益gain");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_LACUNARITY), "控制噪声倍频octave的隙度（不同倍频octave之间频率的差距）");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_OCTAVES), "控制噪声倍频octave组数（影响噪声精细度）");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_SCALE), "控制噪声水平缩放倍数");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_EDGE_STRENGTH), "控制噪声强度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_SIZE), "生物群系的大小");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_SCALE), "生物群系形状的扭曲规模");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_BIOME_WARP_STRENGTH), "生物群系形状的扭曲强度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_COAST),
					  "控制海岸区域的尺寸，同时也是基准点，"
					+ "低于此基准点的内陆地貌会转变为海洋"
					+ "某些生物群系（如蘑菇岛）仅生成于海岸。");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_JITTER), "控制大陆中心相对底层噪声网格偏移的程度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_GAIN), "每个噪声的影响程度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_LACUNARITY), "控制噪声倍频octave的隙度（不同倍频octave之间频率的差距）");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_NOISE_OCTAVES), "控制用来扭曲大陆形状的噪声倍频octave组数（影响噪声精细度）");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SCALE), "控制大陆的大小。你可能也需要调整过渡基准点来保证沙滩等仍然生成。");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SIZE_VARIANCE), "提高大陆尺寸的多样性");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_CONTINENT_SKIPPING), "减少大陆数量，生成更广阔的海洋");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_DEEP_OCEAN), "控制深海转变为浅海的基准点，高于此基准点的深海将转变为浅海，与浅海基准点差值越大，过渡越缓慢。");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_DEPOSITE_RATE), "控制物质沉积的速度（在侵蚀过程中）");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLETS_PER_CHUNK), "每区块模拟侵蚀的水滴平均数量");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_LIFETIME), "控制一滴水模拟侵蚀的迭代次数");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VELOCITY), "控制水滴的初始速度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_DROPLET_VOLUME), "控制水滴的初始体积");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_EROSION_RATE), "控制物质剥离的速度（在侵蚀过程中）");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_GLOBAL_HORIZONTAL_SCALE), "全局地形水平缩放");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_GLOBAL_VERTICAL_SCALE), "全局地形垂直缩放");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_INLAND), "控制海洋转化为内陆地貌的基准点");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_CHANCE), "控制生成湖泊的几率");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_DEPTH), "湖床最大深度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_BANK_HEIGHT), "最大岸高");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MAX_START_DISTANCE), "沿河流生成湖泊的最大间隔");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_BANK_HEIGHT), "最小岸高");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_MIN_START_DISTANCE), "沿河流生成湖泊的最小间隔");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MAX), "最大尺寸");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_LAKE_SIZE_MIN), "最小尺寸");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MACRO_NOISE_SIZE), "宏观噪声用来组合同种类型的生物群系（比如各种沙漠）。值越大，同类型群系越不容易成群出现。");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_BIAS), "对某一个极端（干/湿）的倾向");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_FALLOFF), "从一个极端到另一个极端的过渡速度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MAX), "最大值");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_MIN), "最小值");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_MOISTURE_SCALE), "水平缩放");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_BANK_WIDTH), "控制两岸之间的最大距离");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_DEPTH), "控制河流的最大深度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_BED_WIDTH), "控制河床的最大宽度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_COUNT), "控制每个大陆上河流干流的数量");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_FADE), "控制河流源头变细的程度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_MAX_BANK_HEIGHT), "控制河岸的最大高度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_RIVER_MIN_BANK_HEIGHT), "控制河岸的最小高度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SEA_LEVEL), "控制海平面高度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SEPARATION),
					  "控制同一种特殊结构的最小距离。"
					+ "值越大，它们间隔越远，但是会导致它们的分布更趋向于对齐网格。"
					+ "较小的值会使得它们的分布更加随机，但是间隔会更近。");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SHALLOW_OCEAN),
					  "控制浅海过渡到海岸的基准点，高于此基准点的浅海将转变为海岸。"
					+ "与海岸基准点的差值越大，过渡越平滑");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_ITERATIONS), "控制平滑化的迭代次数");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RADIUS), "控制平滑化计算过程中所考虑的半径");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SMOOTHING_RATE), "控制平滑化对地形的影响程度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_SPACING),
					  "控制生成特殊结构的网格的大小"
					+ "在每个格子里都会尝试生成一次特殊结构。"
					+ "较大的间隔会导致特殊结构更为稀有。");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_STRATA_REGION_SIZE), "控制岩层的尺寸");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_BIAS), "对某一个极端（冷/暖）的倾向");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_FALLOFF), "从一个极端到另一个极端的过渡速度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MAX), "最大值");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_MIN), "最小值");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TEMPERATURE_SCALE), "水平缩放倍数");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_BASE_SCALE), "控制该地貌的基础高度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_HORIZONTAL_SCALE), "在水平方向上，伸展或者压缩这种地貌");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_REGION_SIZE), "控制该地貌的区域尺寸");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_VERTICAL_SCALE), "在竖直方向上，伸展或者压缩这种地貌");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_TERRAIN_WEIGHT), "控制这种地貌的常见程度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WETLAND_CHANCE), "控制湿地的常见程度");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MAX), "最大尺寸");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WETLAND_SIZE_MIN), "最小尺寸");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WORLD_DEPTH), "控制世界方块y轴下限");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_WORLD_HEIGHT), "控制世界方块y轴上限");
			this.add(Tooltips.translationKey(RTFTranslationKeys.GUI_SLIDER_ZOOM), "控制预览地图的放大倍数");

			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_COPY), "预设复制失败");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_CREATE), "预设创建失败");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_DELETE), "预设删除失败");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_EXPORT), "预设导出失败");
			this.add(Tooltips.failTranslationKey(RTFTranslationKeys.GUI_BUTTON_IMPORT_LEGACY), "旧版预设导入失败");

        }
	}
}
