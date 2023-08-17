package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.density.NoopDensityFunction;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class RTFDensityFunctionTypes {
	public static final ResourceKey<Codec<? extends DensityFunction>> TERRAIN = resolve("terrain");
	public static final ResourceKey<Codec<? extends DensityFunction>> NOOP = resolve("noop");

	public static void register() {
		RegistryUtil.register(TERRAIN, () -> TerrainNoise.Marker.CODEC);
		RegistryUtil.register(NOOP, NoopDensityFunction.CODEC::codec);
	}
	
	private static ResourceKey<Codec<? extends DensityFunction>> resolve(String path) {
		return ReTerraForged.resolve(Registries.DENSITY_FUNCTION_TYPE, path);
	}
}
