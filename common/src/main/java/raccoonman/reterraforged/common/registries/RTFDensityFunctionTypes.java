package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.density.FlatCache;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseWrapper;
import raccoonman.reterraforged.common.level.levelgen.density.YScale;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFDensityFunctionTypes {

	public static void bootstrap() {
		register("noise", NoiseWrapper.Marker.CODEC);
		register("flat_cache", FlatCache.Marker.CODEC);
		register("y_scale", YScale.CODEC);
	}
	
	private static void register(String name, Codec<? extends DensityFunction> type) {
		RegistryUtil.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, type);
	}
}
