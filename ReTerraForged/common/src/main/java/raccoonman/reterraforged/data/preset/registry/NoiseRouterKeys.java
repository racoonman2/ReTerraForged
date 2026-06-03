package raccoonman.reterraforged.data.preset.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.registry.RTFRegistries;

public class NoiseRouterKeys {
	public static final ResourceKey<DensityFunction> CONTINENTS = createKey("continents");
	public static final ResourceKey<DensityFunction> EROSION = createKey("erosion");
	public static final ResourceKey<DensityFunction> RIDGES = createKey("ridges");
	public static final ResourceKey<DensityFunction> RIDGES_FOLDED = createKey("ridges_folded");
	public static final ResourceKey<DensityFunction> OFFSET = createKey("offset");
	public static final ResourceKey<DensityFunction> FACTOR = createKey("factor");
	public static final ResourceKey<DensityFunction> JAGGEDNESS = createKey("jaggedness");
	public static final ResourceKey<DensityFunction> DEPTH = createKey("depth");
	public static final ResourceKey<DensityFunction> SLOPED_CHEESE = createKey("sloped_cheese");
	public static final ResourceKey<DensityFunction> SPAGHETTI_ROUGHNESS_FUNCTION = createKey("caves/spaghetti_roughness_function");
	public static final ResourceKey<DensityFunction> ENTRANCES = createKey("caves/entrances");
	public static final ResourceKey<DensityFunction> NOODLE = createKey("caves/noodle");
	public static final ResourceKey<DensityFunction> PILLARS = createKey("caves/pillars");
	public static final ResourceKey<DensityFunction> SPAGHETTI_2D_THICKNESS_MODULATOR = createKey("caves/spaghetti_2d_thickness_modulator");
	public static final ResourceKey<DensityFunction> SPAGHETTI_2D = createKey("caves/spaghetti_2d");
	
	public static final ResourceKey<DensityFunction> AQUIFER_BARRIER = createKey("aquifer_barrier");
	public static final ResourceKey<DensityFunction> AQUIFER_FLUID_LEVEL_FLOODEDNESS = createKey("aquifer_fluid_level_floodedness");
	public static final ResourceKey<DensityFunction> AQUIFER_FLUID_LEVEL_SPREAD = createKey("aquifer_fluid_level_spread");
	public static final ResourceKey<DensityFunction> AQUIFER_LAVA = createKey("aquifer_lava");
	public static final ResourceKey<DensityFunction> TEMPERATURE = createKey("temperature");
	public static final ResourceKey<DensityFunction> VEGETATION = createKey("vegetation");
	
	public static final ResourceKey<DensityFunction> PRELIMINARY_SURFACE_LEVEL = createKey("preliminary_surface_level");
	public static final ResourceKey<DensityFunction> FINAL_DENSITY = createKey("final_density");
	public static final ResourceKey<DensityFunction> ORE_VEININESS = createKey("ore_veininess");
	public static final ResourceKey<DensityFunction> ORE_VEIN = createKey("ore_vein");
	public static final ResourceKey<DensityFunction> ORE_GAP = createKey("ore_gap");
	public static final ResourceKey<DensityFunction> UNDERGROUND = createKey("underground");

	public static final ResourceKey<DensityFunction> RAW_CONTINENTS = createKey("raw_continents");
	public static final ResourceKey<DensityFunction> BIOME_REGION = createKey("biome_region");
	public static final ResourceKey<DensityFunction> TERRAIN_HEIGHT = createKey("terrain_height");

    private static ResourceKey<DensityFunction> createKey(String path) {
    	return RTFRegistries.createKey(Registries.DENSITY_FUNCTION, path);
    }
}
