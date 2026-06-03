package raccoonman.reterraforged.data;

import raccoonman.reterraforged.RTFCommon;

public class RTFTranslationKeys {
	public static final String RESOURCE_PACK_METADATA_DESCRIPTION = prependModId("resourcePack.metadata.description");
	public static final String DATA_PACK_METADATA_DESCRIPTION = prependModId("dataPack.metadata.description");

	public static final String MISSING_ERROR_MESSAGE = prependModId("gui.error.missingMesssage");

	public static final String LITE_PRESET = prependModId("preset.builtin.lite");
	public static final String DEFAULT_PRESET = prependModId("preset.builtin.default");
	public static final String REALISTIC_PRESET = prependModId("preset.builtin.realistic");
	public static final String LITE_PRESET_DESCRIPTION = prependModId("preset.builtin.lite.description");
	public static final String DEFAULT_PRESET_DESCRIPTION = prependModId("preset.builtin.default.description");
	public static final String REALISTIC_PRESET_DESCRIPTION = prependModId("preset.builtin.realistic.description");

	public static final String GUI_TAB_TITLE = prependModId("gui.tab.title");
	public static final String GUI_TRUE = prependModId("gui.true");
	public static final String GUI_FALSE = prependModId("gui.false");
	public static final String GUI_APPLY = prependModId("gui.apply");
	public static final String GUI_APPLY_TOOLTIP = tooltipKey(GUI_APPLY);
	public static final String GUI_NEW_WORLD = prependModId("gui.newWorld");
	public static final String GUI_NEW_WORLD_TOOLTIP = tooltipKey(GUI_NEW_WORLD);
	
	public static final String RENDER_MODE_CONTINENTALNESS = prependModId("renderMode.continentalness");
	public static final String RENDER_MODE_TEMPERATURE = prependModId("renderMode.temperature");
	public static final String RENDER_MODE_HUMIDITY = prependModId("renderMode.humidity");
	public static final String RENDER_MODE_MACRO_BIOME = prependModId("renderMode.macroBiome");
	public static final String RENDER_MODE_EROSION = prependModId("renderMode.erosion");
	public static final String RENDER_MODE_WEIRDNESS = prependModId("renderMode.weirdness");

	public static final String SELECT_PRESET_TITLE = prependModId("gui.selectPreset.title");
	public static final String CREATE_PRESET_PROMPT = prependModId("gui.selectPreset.createPresetPrompt");
	public static final String CREATE_PRESET = prependModId("gui.selectPreset.createPreset");
	public static final String COPY_PRESET = prependModId("gui.selectPreset.copyPreset");
	public static final String RENAME_PRESET = prependModId("gui.selectPreset.renamePreset");
	public static final String DELETE_PRESET = prependModId("gui.selectPreset.deletePreset");
	public static final String EXPORT_PRESET = prependModId("gui.selectPreset.exportPreset");
	public static final String OPEN_PRESET_FOLDER = prependModId("gui.selectPreset.openPresetFolder");
	public static final String OPEN_EXPORT_FOLDER = prependModId("gui.selectPreset.openExportFolder");

	public static final String GUI_EXPORT_PRESET_SUCCESS = prependModId("gui.selectPreset.export.success");

	public static final String WORLD_PREVIEW_SEED = prependModId("gui.worldPreview.seed");
	public static final String WORLD_PREVIEW_ZOOM = prependModId("gui.worldPreview.zoom");
	public static final String WORLD_PREVIEW_RESOLUTION = prependModId("gui.worldPreview.resolution");
	public static final String WORLD_PREVIEW_HEIGHT_LIMIT_WARNING = prependModId("gui.worldPreview.heightLimitWarning");

	public static final String GUI_ACTION_DRAG = prependModId("gui.action.drag");
	public static final String GUI_ACTION_COPY = prependModId("gui.action.copy");
	public static final String GUI_ACTION_RESET = prependModId("gui.action.reset");
	public static final String GUI_ACTION_RANDOMIZE = prependModId("gui.action.randomize");
	public static final String GUI_INPUT_LEFT_CLICK = prependModId("gui.input.leftClick");
	public static final String GUI_INPUT_LEFT_CONTROL = prependModId("gui.input.leftControl");
	
	public static final String STRUCTURE_PAGE = prependModId("structures");

	public static final String STRUCTURE_ENABLED = prependModId("structures.enabled");
	public static final String STRUCTURE_FREQUENCY = prependModId("structures.frequency");
	public static final String STRUCTURE_SPACING = prependModId("structures.spacing");
	public static final String STRUCTURE_SEPARATION = prependModId("structures.separation");

	public static final String STRUCTURE_DISTANCE = prependModId("structures.distance");
	public static final String STRUCTURE_SPREAD = prependModId("structures.spread");
	public static final String STRUCTURE_COUNT = prependModId("structures.count");

	public static final String EXPERIMENTAL = prependModId("experimental");
	public static final String MEDIUM_PERFORMANCE_IMPACT = prependModId("medium_performance_impact");
	public static final String HEAVY_PERFORMANCE_IMPACT = prependModId("heavy_performance_impact");

	public static final String MIN_GREATER_THAN_MAX = prependModId("commands.args.minGreaterThanMax");
	
	public static final String POINT_FOUND = prependModId("commands.locate.point.found");
	public static final String POINT_NOT_FOUND = prependModId("commands.locate.point.notFound");
	public static final String SEARCHING = prependModId("commands.locate.searchingForPoint");
	public static final String CANCEL_SEARCH = prependModId("commands.locate.cancelSearch");
	public static final String CANCEL_SEARCH_DESCRIPTION = prependModId("commands.locate.cancelSearch.description");
	public static final String ALREADY_SEARCHING = prependModId("commands.locate.searchAlreadyRunning");
	public static final String SEARCH_CANCELLED = prependModId("commands.locate.searchCancelled");
	public static final String NO_SEARCH = prependModId("commands.locate.noSearch");

	public static final String EXPORT_SUCCESS = prependModId("commands.export.success");
	public static final String EXPORT_FAIL = prependModId("commands.export.fail");
	public static final String EXPORT_DESCRIPTION = prependModId("commands.export.description");

	public static String prependModId(String key) {
		return RTFCommon.MOD_ID + "." + key;
	}

	public static String tooltipKey(String key) {
		return key + ".tooltip";
	}
	
	public static String tooltipFailKey(String key) {
		return tooltipKey(key) + ".fail";
	}
}