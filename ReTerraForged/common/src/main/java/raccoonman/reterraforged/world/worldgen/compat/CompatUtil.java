package raccoonman.reterraforged.world.worldgen.compat;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.RandomState;
import raccoonman.reterraforged.platform.ModLoaderUtil;

@Deprecated
public class CompatUtil {
	public static final boolean HAS_TB = ModLoaderUtil.isLoaded("terrablender");
	public static final boolean HAS_BWG = ModLoaderUtil.isLoaded("biomeswevegone");
	public static final boolean HAS_BOP = ModLoaderUtil.isLoaded("biomesoplenty");
	public static final boolean HAS_RU = ModLoaderUtil.isLoaded("regions_unexplored");
	public static final boolean HAS_NS = ModLoaderUtil.isLoaded("natures_spirit");
	public static final boolean HAS_WP = ModLoaderUtil.isLoaded("world_preview");

	public static void setUniqueness(RandomState randomState, ResourceKey<Level> dimension, RegistryAccess registryAccess) {
//		if(CompatUtil.HAS_TB && dimension.equals(Level.OVERWORLD)) {
//			HolderGetter<DensityFunction> densityFunctions = registryAccess.lookupOrThrow(Registries.DENSITY_FUNCTION);
//			densityFunctions.get(NoiseRouterKeys.BIOME_REGION.apply(Level.OVERWORLD.location())).ifPresent((holder) -> {
//				RTFRandomState rtfRandomState = ExtensionUtil.cast(randomState);
//				TBClimateSampler tbClimateSampler = ExtensionUtil.cast(randomState.sampler());
//				tbClimateSampler.setUniqueness(holder.value().mapAll(rtfRandomState.globalFunctionVisitor()));
//			});
//		}
	}
}
