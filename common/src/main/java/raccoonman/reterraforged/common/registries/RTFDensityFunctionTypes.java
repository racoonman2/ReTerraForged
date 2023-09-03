package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.density.FlatCache;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.density.Ridges;
import raccoonman.reterraforged.common.level.levelgen.density.WorldHeightMarker;
import raccoonman.reterraforged.common.level.levelgen.density.YGradient;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFDensityFunctionTypes {

	public static void bootstrap() {
		register("noise_wrapper", NoiseWrapper.Marker.CODEC);
		register("flat_cache", FlatCache.Marker.CODEC);
		register("y_gradient", YGradient.CODEC);
		register("ridges", Ridges.CODEC);
		register("world_height", WorldHeightMarker.CODEC);
	}
	
	private static void register(String name, Codec<? extends DensityFunction> type) {
		RegistryUtil.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, type);
	}
}
