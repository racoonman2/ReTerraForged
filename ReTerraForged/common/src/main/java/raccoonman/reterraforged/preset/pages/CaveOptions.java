package raccoonman.reterraforged.preset.pages;

import raccoonman.reterraforged.preset.option.BoolOption;
import raccoonman.reterraforged.preset.option.Category;
import raccoonman.reterraforged.preset.option.FloatOption;
import raccoonman.reterraforged.preset.option.IntOption;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.Page;

public class CaveOptions {
	public static final Page PAGE = Page.make("caves");

	public static final Category CAVE_CARVERS = PAGE.addCategory("caveCarvers");
	public static final Option<Float> CAVE_CARVER_RARITY = FloatOption.builder("rarity", 0.15F, 0.0F, 1.0F).add(CAVE_CARVERS);
	public static final Option<Float> CAVE_CARVER_HORIZONTAL_SCALE = FloatOption.builder("horizontalScale", 1.0F, 0.0F, 4.0F).add(CAVE_CARVERS);
	public static final Option<Float> CAVE_CARVER_VERTICAL_SCALE = FloatOption.builder("verticalScale", 1.0F, 0.0F, 4.0F).add(CAVE_CARVERS);
	public static final Option<Integer> CAVE_CARVER_MIN_Y = IntOption.builder("minY", -56, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).upper(() -> CaveOptions.CAVE_CARVER_MAX_Y).add(CAVE_CARVERS);
	public static final Option<Integer> CAVE_CARVER_MAX_Y = IntOption.builder("maxY", 56, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).lower(() -> CaveOptions.CAVE_CARVER_MIN_Y).add(CAVE_CARVERS);
	
	public static final Category DEEP_CAVE_CARVERS = PAGE.addCategory("deepCaveCarvers");
	public static final Option<Float> DEEP_CAVE_CARVER_RARITY = FloatOption.builder("rarity", 0.07F, 0.0F, 1.0F).add(DEEP_CAVE_CARVERS);
	public static final Option<Float> DEEP_CAVE_CARVER_HORIZONTAL_SCALE = FloatOption.builder("horizontalScale", 1.0F, 0.0F, 4.0F).add(DEEP_CAVE_CARVERS);
	public static final Option<Float> DEEP_CAVE_CARVER_VERTICAL_SCALE = FloatOption.builder("verticalScale", 1.0F, 0.0F, 4.0F).add(DEEP_CAVE_CARVERS);
	public static final Option<Integer> DEEP_CAVE_CARVER_MIN_Y = IntOption.builder("minY", -56, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).upper(() -> CaveOptions.DEEP_CAVE_CARVER_MAX_Y).add(DEEP_CAVE_CARVERS);
	public static final Option<Integer> DEEP_CAVE_CARVER_MAX_Y = IntOption.builder("maxY", 320, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).lower(() -> CaveOptions.DEEP_CAVE_CARVER_MIN_Y).add(DEEP_CAVE_CARVERS);

	public static final Category CANYON_CARVERS = PAGE.addCategory("canyonCarvers");
	public static final Option<Float> CANYON_CARVER_RARITY = FloatOption.builder("rarity", 0.01F, 0.0F, 1.0F).add(CANYON_CARVERS);
	public static final Option<Float> CANYON_CARVER_HORIZONTAL_SCALE = FloatOption.builder("horizontalScale", 1.0F, 0.0F, 4.0F).add(CANYON_CARVERS);
	public static final Option<Float> CANYON_CARVER_VERTICAL_SCALE = FloatOption.builder("verticalScale", 1.0F, 0.0F, 4.0F).add(CANYON_CARVERS);
	public static final Option<Integer> CANYON_CARVER_MIN_Y = IntOption.builder("minY", 10, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).upper(() -> CaveOptions.CANYON_CARVER_MAX_Y).add(CANYON_CARVERS);
	public static final Option<Integer> CANYON_CARVER_MAX_Y = IntOption.builder("maxY", 56, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).lower(() -> CaveOptions.CANYON_CARVER_MIN_Y).add(CANYON_CARVERS);
	
	public static final Category NOODLE_CAVES = PAGE.addCategory("noodleCaves");
	public static final Option<Integer> NOODLE_CAVES_MIN_Y = IntOption.builder("minY", -64, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).upper(() -> CaveOptions.NOODLE_CAVES_MAX_Y).add(NOODLE_CAVES);
	public static final Option<Integer> NOODLE_CAVES_MAX_Y = IntOption.builder("maxY", 56, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).lower(() -> CaveOptions.NOODLE_CAVES_MIN_Y).add(NOODLE_CAVES);
	
	public static final Category SPAGHETTI_CAVES = PAGE.addCategory("spaghettiCaves");
	public static final Option<Integer> SPAGHETTI_CAVES_MIN_Y = IntOption.builder("minY", -64, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).upper(() -> CaveOptions.SPAGHETTI_CAVES_MAX_Y).add(SPAGHETTI_CAVES);
	public static final Option<Integer> SPAGHETTI_CAVES_MAX_Y = IntOption.builder("maxY", 56, WorldOptions.MIN_Y_FUNCTION, WorldOptions.MAX_Y_FUNCTION).lower(() -> CaveOptions.SPAGHETTI_CAVES_MIN_Y).add(SPAGHETTI_CAVES);
//	public static final Option<Float> SPAGHETTI_CAVES_THICKNESS = FloatOption.builder("thickness", 0.083F, 0.0F, 0.3F).add(NOODLE_CAVES);

//	public static final Category CHEESE_CAVES = PAGE.addCategory("cheeseCaves");
//	public static final Option<Float> CHEESE_CAVE_HOLLOWNESS = FloatOption.builder("hollowness", 1.0F, 0.0F, 4.0F).add(CHEESE_CAVES);
//	public static final Option<Float> CHEESE_CAVE_LAYERS = FloatOption.builder("layers", 1.0F, 0.0F, 4.0F).add(CHEESE_CAVES);

	public static final Category ENTRANCE_CAVES = PAGE.addCategory("entranceCaves");
	public static final Option<Float> ENTRANCE_CAVE_SIZE = FloatOption.builder("size", 0.0F, 0.0F, 1.0F).add(ENTRANCE_CAVES);
	
//	public static final Category PILLARS = PAGE.addCategory("pillars");
//	public static final Option<Float> PILLAR_RARENESS = FloatOption.builder("pillarRareness", 1.0F, 0.0F, 3.0F).add(PILLARS);
//	public static final Option<Float> PILLAR_THICKNESS = FloatOption.builder("pillarThickness", 1.0F, 0.0F, 3.0F).add(PILLARS);

	public static final Category ORE = PAGE.addCategory("ore");
	public static final Option<Boolean> LARGE_ORE_VEINS = BoolOption.builder("largeOreVeins", true).add(ORE);
	public static final Option<Boolean> ADJUST_ORE_Y_DISTRIBUTION = BoolOption.builder("adjustYDistribution", false).add(ORE);
}
