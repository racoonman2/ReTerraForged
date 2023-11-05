package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.density.CellSampler;
import raccoonman.reterraforged.common.level.levelgen.density.NoiseSampler;
import raccoonman.reterraforged.platform.RegistryUtil;

public final class RTFDensityFunctionTypes {

	public static void bootstrap() {
		register("noise_sampler", NoiseSampler.Marker.CODEC);
		register("cell_sampler", CellSampler.Marker.CODEC);
	}
	
	private static void register(String name, Codec<? extends DensityFunction> type) {
		RegistryUtil.register(BuiltInRegistries.DENSITY_FUNCTION_TYPE, name, type);
	}
}
