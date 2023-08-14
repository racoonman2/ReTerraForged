package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.densityfunctions.HeightDensityFunction;
import raccoonman.reterraforged.common.level.levelgen.densityfunctions.NoopDensityFunction;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFDensityFunctionTypes {
	public static final ResourceKey<Codec<? extends DensityFunction>> HEIGHT = resolve("height");
	public static final ResourceKey<Codec<? extends DensityFunction>> NOOP = resolve("noop");

	public static void register() {
		RegistryUtil.register(HEIGHT, HeightDensityFunction.Marker.CODEC::codec);
		RegistryUtil.register(NOOP, NoopDensityFunction.CODEC::codec);
	}
	
	private static ResourceKey<Codec<? extends DensityFunction>> resolve(String path) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION_TYPE, path);
	}
}
