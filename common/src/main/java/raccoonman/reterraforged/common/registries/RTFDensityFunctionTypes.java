package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.density.FlatCache;
import raccoonman.reterraforged.common.level.levelgen.noise.density.NoiseDensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.density.YGradientFunction;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFDensityFunctionTypes {

	public static void bootstrap() {
		register("noise", NoiseDensityFunction.Marker.CODEC);
		register("flat_cache", FlatCache.Marker.CODEC);
		register("y_gradient", YGradientFunction.CODEC);
	}
	
	private static void register(String name, Codec<? extends DensityFunction> type) {
		RegistryUtil.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, type);
	}
}
