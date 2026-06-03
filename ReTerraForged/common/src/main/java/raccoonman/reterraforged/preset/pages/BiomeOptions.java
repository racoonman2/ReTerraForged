package raccoonman.reterraforged.preset.pages;

import raccoonman.reterraforged.preset.option.BoolOption;
import raccoonman.reterraforged.preset.option.Category;
import raccoonman.reterraforged.preset.option.Option;
import raccoonman.reterraforged.preset.option.Page;

public class BiomeOptions {
	public static final Page PAGE = Page.make("biome");

	public static final Category FEATURES = PAGE.addCategory("biomeFeatures");
	public static final Option<Boolean> CUSTOM_TREES = BoolOption.builder("customTrees", true).add(FEATURES);
//	public static final Option<Boolean> SEDIMENT_DISKS = BoolOption.builder("sedimentPatches", true).add(FEATURES);
	public static final Option<Boolean> SPRINGS = BoolOption.builder("springs", true).add(FEATURES);
	public static final Option<Boolean> LAVA_LAKES = BoolOption.builder("lavaLakes", true).add(FEATURES);
	public static final Option<Boolean> LAVA_SPRINGS = BoolOption.builder("lavaSprings", true).add(FEATURES);
}
