package raccoonman.reterraforged.data.worldgen.preset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.RTFCommon;

public class PresetTemplatePaths {
	public static final List<ResourceLocation> OAK_FOREST = tree("oak/forest", "forest_oak_1", "forest_oak_2");
	public static final List<ResourceLocation> OAK_LARGE = tree("oak/large", "oak_1", "oak_2", "oak_3", "oak_4", "oak_5");

	public static final List<ResourceLocation> BIRCH_SMALL = tree("birch/small", "birch_bush_1", "birch_bush_2", "birch_small_1", "birch_small_2");
	public static final List<ResourceLocation> BIRCH_LARGE = tree("birch/large", "birch_1", "birch_2", "birch_3");
	public static final List<ResourceLocation> BIRCH_FOREST = tree("birch/forest", "forest_birch_1", "forest_birch_2", "forest_birch_3", "forest_birch_4");
	
	public static final List<ResourceLocation> ACACIA_BUSH = tree("acacia/bush", "acacia_bush_1", "acacia_bush_2");
	public static final List<ResourceLocation> ACACIA_SMALL = union(tree("acacia/small", "acacia_small_1", "acacia_small_2"), ACACIA_BUSH);
	public static final List<ResourceLocation> ACACIA_LARGE = tree("acacia/large", "acacia_1", "acacia_2");
	
	public static final List<ResourceLocation> DARK_OAK_SMALL = tree("dark_oak/small", "dark_oak_1", "dark_oak_2", "dark_oak_3", "dark_oak_bush_1", "dark_oak_bush_2");
	public static final List<ResourceLocation> DARK_OAK_LARGE = tree("dark_oak/large", "dark_oak_tall_1", "dark_oak_tall_2", "dark_oak_tall_3", "dark_oak_tall_4", "dark_oak_tall_5");
	
	public static final List<ResourceLocation> BROWN_MUSHROOM = mushroom("brown", "brown_mushroom_1", "brown_mushroom_2", "brown_mushroom_3", "brown_mushroom_4", "brown_mushroom_5", "brown_mushroom_6", "brown_mushroom_7");
	public static final List<ResourceLocation> RED_MUSHROOM = mushroom("red", "red_mushroom_1", "red_mushroom_2", "red_mushroom_3", "red_mushroom_4", "red_mushroom_5");
	
	public static final List<ResourceLocation> WILLOW_SMALL = tree("willow/small", "weeping_willow_small_1", "weeping_willow_small_2");
	public static final List<ResourceLocation> WILLOW_LARGE = tree("willow/large", "weeping_willow_big_1", "weeping_willow_big_2");

	public static final List<ResourceLocation> PINE = tree("pine", "huangshan_pine_1", "huangshan_pine_2", "huangshan_pine_3", "scots_pine_1", "scots_pine_2", "scots_pine_small_1", "scots_pine_small_2", "scots_pine_1", "scots_pine_2");
	public static final List<ResourceLocation> SPRUCE_BUSH = tree("spruce/bush", "spruce_bush_1", "spruce_bush_2");
	public static final List<ResourceLocation> SPRUCE_SMALL = union(tree("spruce/small", "spruce_small_1", "spruce_small_2", "spruce_small_3", "spruce_small_4"), SPRUCE_BUSH);
	public static final List<ResourceLocation> SPRUCE_LARGE = tree("spruce/large", "spruce_large_1", "spruce_large_2", "spruce_large_3", "spruce_large_4", "spruce_large_5");
	
	private static List<ResourceLocation> tree(String variant, String... paths) {
		return path("trees", variant, paths);
	}
	
	private static List<ResourceLocation> mushroom(String variant, String... paths) {
		return path("mushrooms", variant, paths);
	}
	
	private static List<ResourceLocation> path(String root, String variant, String... paths) {
		return Arrays.stream(paths).map((path) -> {
			return RTFCommon.location("structures/" + root + "/" + variant + "/" + path + ".nbt");
		}).toList();
	}

	private static List<ResourceLocation> union(List<ResourceLocation> list1, List<ResourceLocation> list2) {
		List<ResourceLocation> newList = new ArrayList<>();
		newList.addAll(list1);
		newList.addAll(list2);
		return newList;
	}
}
