package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.layer.Layer;
import raccoonman.reterraforged.world.worldgen.layer.Reference;
import raccoonman.reterraforged.world.worldgen.noise.Noise;	

public class RTFDensityFunctions {

	public static void bootstrap() {
		register("noise", NoiseFunction.Marker.CODEC);
		register("fast_flat_cache", FastFlatCache.Marker.CODEC);
		register("range", RangeFunction.CODEC);		
		register("elevation", ElevationFunction.Marker.CODEC);
		register("spline", SplineFunction.CODEC);
		register("cache_2d", Cache2d.CODEC);
		register("flat_cache_2", FlatCache2.Marker.CODEC);
		register("layer", LayerFunction.Marker.CODEC);
		register("weighted", WeightedFunction.Marker.CODEC);
		register("region", RegionFunction.CODEC);
		register("blend", BlendFunction.CODEC);
	}
	
	public static DensityFunction noise(Noise noise) {
		return noise(Holder.direct(noise));
	}
	
	public static DensityFunction noise(Holder<Noise> noise) {
		return new NoiseFunction.Marker(noise);
	}
	
	public static DensityFunction fastFlatCache(DensityFunction function, boolean fullResolution) {
		return new FastFlatCache.Marker(function, fullResolution);
	}
	
	public static DensityFunction range(DensityFunction input, double from, double to, boolean exclusive) {
		return new RangeFunction(input, from, to, exclusive);
	}
	
	public static DensityFunction elevation(float scaler) {
		return new ElevationFunction.Marker(scaler);
	}
	
	public static DensityFunction layer(Holder<Layer.Factory<Reference<DensityFunction>>> layer, DensityFunction fallback) {
		return new LayerFunction.Marker(layer, fallback);
	}
	
	public static DensityFunction flatCache2(DensityFunction function, int size) {
		return new FlatCache2.Marker(function, size);
	}
	
	private static void register(String name, MapCodec<? extends DensityFunction> type) {
		RegistryUtil.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, type);
	}
}
