package raccoonman.reterraforged.world.worldgen.feature.placement;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.registry.RegistryFilter;

public class RTFPlacementModifiers {
	public static final PlacementModifierType<DimensionFilter> DIMENSION_FILTER = register("dimension_filter", DimensionFilter.CODEC);
	public static final PlacementModifierType<DensityFilter> DENSITY_FILTER = register("density_filter", DensityFilter.CODEC);
	public static final PlacementModifierType<PoissonModifier> POISSON = register("poisson", PoissonModifier.CODEC);

	@SafeVarargs
	public static DimensionFilter dimension(boolean inclusive, ResourceKey<Level>... levels) {
		List<ResourceKey<Level>> list = ImmutableList.copyOf(levels);
    	return new DimensionFilter(inclusive ? RegistryFilter.inclusive(list) : RegistryFilter.exclusive(list));
    }
    
    public static DensityFilter densityFilter(Holder<DensityFunction> densityFunction, double threshold) {
    	return new DensityFilter(densityFunction, threshold);
    }

    public static PoissonModifier poisson(int radius, float scale, float jitter, Holder<DensityFunction> density) {
		 return new PoissonModifier(radius, scale, jitter, density);
    }

    public static void bootstrap() {
    }
    
    private static <P extends PlacementModifier> PlacementModifierType<P> register(String name, MapCodec<P> codec) {
    	PlacementModifierType<P> type = () -> codec;
		RegistryUtil.register(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, name, type);
		return type;
    }
}
