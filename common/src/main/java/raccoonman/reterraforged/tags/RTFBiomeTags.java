package raccoonman.reterraforged.tags;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import raccoonman.reterraforged.RTFCommon;

public class RTFBiomeTags {
	public static final TagKey<Biome> HAS_SWAMP_SURFACE = resolve("features/has_swamp_surface");
	public static final TagKey<Biome> HAS_SWAMP_TREES = resolve("features/has_swamp_trees");
	public static final TagKey<Biome> HAS_PLAINS_TREES = resolve("features/has_plains_trees");
	public static final TagKey<Biome> HAS_FOREST_TREES = resolve("features/has_forest_trees");
	public static final TagKey<Biome> HAS_FLOWER_FOREST_TREES = resolve("features/has_flower_forest_trees");
	public static final TagKey<Biome> HAS_BIRCH_FOREST_TREES = resolve("features/has_birch_forest_trees");
	public static final TagKey<Biome> HAS_DARK_FOREST_TREES = resolve("features/has_dark_forest_trees");
	public static final TagKey<Biome> HAS_SAVANNA_TREES = resolve("features/has_savanna_trees");
	public static final TagKey<Biome> HAS_MEADOW_TREES = resolve("features/has_meadow_trees");
	@Deprecated //rename to HAS_WINDSWEPT_FOREST_TREES
	public static final TagKey<Biome> HAS_FIR_FOREST_TREES = resolve("features/has_fir_forest_trees");
	public static final TagKey<Biome> HAS_GROVE_TREES = resolve("features/has_grove_trees");
	public static final TagKey<Biome> HAS_WINDSWEPT_HILLS_TREES = resolve("features/has_windswept_hills_trees");
	public static final TagKey<Biome> HAS_PINE_FOREST_TREES = resolve("features/has_pine_forest_trees");
	public static final TagKey<Biome> HAS_SPRUCE_FOREST_TREES = resolve("features/has_spruce_forest_trees");
	public static final TagKey<Biome> HAS_SPRUCE_TUNDRA_TREES = resolve("features/has_spruce_tundra_trees");
	public static final TagKey<Biome> HAS_REDWOOD_FOREST_TREES = resolve("features/has_redwood_forest_trees");
	public static final TagKey<Biome> HAS_JUNGLE_TREES = resolve("features/has_jungle_trees");
	public static final TagKey<Biome> HAS_JUNGLE_EDGE_TREES = resolve("features/has_jungle_edge_trees");
	public static final TagKey<Biome> HAS_BADLANDS_TREES = resolve("features/has_badlands_trees");
	public static final TagKey<Biome> HAS_WOODED_BADLANDS_TREES = resolve("features/has_wooded_badlands_trees");

	public static final TagKey<Biome> HAS_MARSH_BUSHES = resolve("features/has_marsh_bushes");
	public static final TagKey<Biome> HAS_PLAINS_BUSHES = resolve("features/has_plains_bushes");
	public static final TagKey<Biome> HAS_STEPPE_BUSHES = resolve("features/has_steppe_bushes");
	public static final TagKey<Biome> HAS_COLD_STEPPE_BUSHES = resolve("features/has_cold_steppe_bushes");
	public static final TagKey<Biome> HAS_COLD_TAIGA_SCRUB_BUSHES = resolve("features/has_cold_taiga_scrub_bushes");

	public static final TagKey<Biome> HAS_FOREST_GRASS = resolve("features/has_forest_grass");
	public static final TagKey<Biome> HAS_MEADOW_GRASS = resolve("features/has_meadow_grass");
	public static final TagKey<Biome> HAS_FERN_GRASS = resolve("features/has_fern_grass");
	public static final TagKey<Biome> HAS_BIRCH_GRASS = resolve("features/has_birch_grass");
	
	public static final TagKey<Biome> EROSION_BLACKLIST = resolve("surface/erosion_blacklist");
	
    private static TagKey<Biome> resolve(String path) {
    	return TagKey.create(Registries.BIOME, RTFCommon.location(path));
    }
}
