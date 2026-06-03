package raccoonman.reterraforged.preset.pages;

import raccoonman.reterraforged.preset.PresetFunction;
import raccoonman.reterraforged.preset.option.Category;
import raccoonman.reterraforged.preset.option.IntOption;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.Page;
import raccoonman.reterraforged.preset.option.TextOption;
import raccoonman.reterraforged.preset.option.TooltipModifier;

public class WorldOptions {
	public static final Page PAGE = Page.make("world");
	
	public static final PresetFunction<Integer> MIN_Y_FUNCTION = PresetFunction.option(() -> WorldOptions.MIN_Y);
	public static final PresetFunction<Integer> MAX_Y_FUNCTION = PresetFunction.option(() -> WorldOptions.MAX_Y);
	
	public static final Category PROPERTIES = PAGE.addCategory("properties");
	public static final Option<Integer> MIN_Y = IntOption.builder("minY", -64, -2032, 0).multiple(16).tooltipModifier(TooltipModifier.HEAVY_PERFORMANCE_IMPACT).add(PROPERTIES);
	public static final Option<Integer> MAX_Y = IntOption.builder("maxY", 320, 16, 2032).multiple(16).lower(() -> WorldOptions.SEA_LEVEL).tooltipModifier(TooltipModifier.HEAVY_PERFORMANCE_IMPACT).add(PROPERTIES);
	public static final Option<Integer> SEA_LEVEL = IntOption.builder("seaLevel", 63, -2032, 2032).bound(PresetFunction.option(() -> WorldOptions.LAVA_LEVEL), MAX_Y_FUNCTION).add(PROPERTIES);
	public static final Option<Integer> LAVA_LEVEL = IntOption.builder("lavaLevel", -54, -2032, 2032).bound(MIN_Y_FUNCTION, PresetFunction.option(() -> WorldOptions.SEA_LEVEL)).add(PROPERTIES);
	public static final Option<Integer> CLOUD_LEVEL = IntOption.builder("cloudLevel", 192, -2032, 2032).add(PROPERTIES);
	public static final Option<Double> COORDINATE_SCALE = TextOption.doubleBuilder("coordinateScale", 1.0D, 1.0E-5D, 3.0E7D).add(PROPERTIES);
}
