package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.density.Cache2DFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.density.NoiseDensityFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.density.YGradientFunction;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFDensityFunctionTypes {
	public static final ResourceKey<Codec<? extends DensityFunction>> NOISE = resolve("noise");
	public static final ResourceKey<Codec<? extends DensityFunction>> CACHE_2D = resolve("cache2d");
	public static final ResourceKey<Codec<? extends DensityFunction>> Y_GRADIENT = resolve("y_gradient");
	
	public static void register() {
		RegistryUtil.register(NOISE, () -> NoiseDensityFunction.Marker.CODEC);
		RegistryUtil.register(CACHE_2D, () -> Cache2DFunction.Marker.CODEC);
		RegistryUtil.register(Y_GRADIENT, () -> YGradientFunction.CODEC);
	}
	
	private static ResourceKey<Codec<? extends DensityFunction>> resolve(String path) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION_TYPE, path);
	}
}
