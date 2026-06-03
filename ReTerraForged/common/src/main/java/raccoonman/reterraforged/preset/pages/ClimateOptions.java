package raccoonman.reterraforged.preset.pages;

import org.apache.commons.lang3.StringUtils;

import raccoonman.reterraforged.preset.option.BoolOption;
import raccoonman.reterraforged.preset.option.Category;
import raccoonman.reterraforged.preset.option.FloatOption;
import raccoonman.reterraforged.preset.option.IntOption;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.Page;
import raccoonman.reterraforged.preset.option.TextOption;

public class ClimateOptions {
	public static final Page PAGE = Page.make("climate");
	
	public static final Category BIOMES = PAGE.addCategory("biomes");
	//TODO
	public static final Option<Boolean> CUSTOM_BIOME_GENERATION = BoolOption.builder("customBiomeGeneration", false/*true*/).build();//.add(BIOMES);
	public static final Option<Integer> BIOME_SCALE = IntOption.builder("biomeScale", 105, 50, 4000).add(BIOMES);
	public static final Option<Integer> BIOME_WARP_SCALE = IntOption.builder("warpScale", 150, 1, 500).add(BIOMES);
	public static final Option<Integer> BIOME_WARP_STRENGTH = IntOption.builder("warpStrength", 80, 1, 500).add(BIOMES);
	public static final Option<Float> BIOME_REGION_SCALE = FloatOption.builder("biomeRegionScale", 5.0F, 1.0F, 25.0F).add(BIOMES);

	public static final Category TEMPERATURE = PAGE.addCategory("temperature");
	public static final Option<String> TEMPERATURE_SEED = TextOption.stringBuilder("seed", StringUtils.EMPTY).add(TEMPERATURE);
	public static final Option<Integer> TEMPERATURE_SCALE = IntOption.builder("scale", 13, 5, 50).add(TEMPERATURE);
	public static final Option<Integer> TEMPERATURE_WARP_SCALE = IntOption.builder("warp_scale", 4, 1, 50).add(TEMPERATURE);
	public static final Option<Float> COLD_POINT = FloatOption.builder("coldPoint", 0.05F, 0.0F, 1.0F).upper(() -> ClimateOptions.TEMPERATE_POINT).add(TEMPERATURE);
	public static final Option<Float> TEMPERATE_POINT = FloatOption.builder("temperatePoint", 0.326F, 0.0F, 1.0F).bound(() -> ClimateOptions.COLD_POINT, () -> ClimateOptions.WARM_POINT).add(TEMPERATURE);
	public static final Option<Float> WARM_POINT = FloatOption.builder("warmPoint", 0.638F, 0.0F, 1.0F).bound(() -> ClimateOptions.TEMPERATE_POINT, () -> ClimateOptions.HOT_POINT).add(TEMPERATURE);
	public static final Option<Float> HOT_POINT = FloatOption.builder("hotPoint", 0.915F, 0.0F, 1.0F).lower(() -> ClimateOptions.WARM_POINT).add(TEMPERATURE);
	
	public static final Category HUMIDITY = PAGE.addCategory("humidity");
	public static final Option<String> HUMIDITY_SEED = TextOption.stringBuilder("seed", StringUtils.EMPTY).add(HUMIDITY);
	public static final Option<Integer> HUMIDITY_SCALE = IntOption.builder("scale", 25, 5, 100).add(HUMIDITY);
	public static final Option<Float> DRY_POINT = FloatOption.builder("dryPoint", 0.245F, 0.0F, 1.0F).upper(() -> ClimateOptions.MILD_POINT).add(HUMIDITY);
	public static final Option<Float> MILD_POINT = FloatOption.builder("mildPoint", 0.465F, 0.0F, 1.0F).bound(() -> ClimateOptions.DRY_POINT, () -> ClimateOptions.HUMID_POINT).add(HUMIDITY);
	public static final Option<Float> HUMID_POINT = FloatOption.builder("humidPoint", 0.57F, 0.0F, 1.0F).bound(() -> ClimateOptions.MILD_POINT, () -> ClimateOptions.WET_POINT).add(HUMIDITY);
	public static final Option<Float> WET_POINT = FloatOption.builder("wetPoint", 0.64F, 0.0F, 1.0F).lower(() -> ClimateOptions.HUMID_POINT).add(HUMIDITY);
}
