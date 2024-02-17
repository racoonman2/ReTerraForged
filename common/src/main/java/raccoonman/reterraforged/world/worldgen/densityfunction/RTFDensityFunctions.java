package raccoonman.reterraforged.world.worldgen.densityfunction;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.platform.RegistryUtil;
import raccoonman.reterraforged.world.worldgen.cell.CellField;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;	

public class RTFDensityFunctions {

	public static void bootstrap() {
		register("noise_sampler", NoiseSampler.Marker.CODEC);
		register("cell", CellSampler.Marker.CODEC);
		register("clamp_to_nearest_unit", ClampToNearestUnit.CODEC);
		register("linear_spline", LinearSplineFunction.CODEC);
	}
	
	public static NoiseSampler.Marker noise(Holder<Noise> noise) {
		return new NoiseSampler.Marker(noise);
	}
	
	public static CellSampler.Marker cell(CellField field) {
		return new CellSampler.Marker(field);
	}
	
	public static ClampToNearestUnit clampToNearestUnit(DensityFunction function, int resolution) {
		return new ClampToNearestUnit(function, resolution);
	}
	
	private static void register(String name, Codec<? extends DensityFunction> type) {
		RegistryUtil.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, type);
	}
}
