package raccoonman.reterraforged.common.registries;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.CurveFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public final class RTFRegistries {
	public static final ResourceKey<Registry<Codec<? extends Noise>>> NOISE_TYPE = createKey("worldgen/noise_type");
	public static final ResourceKey<Registry<Codec<? extends Domain>>> DOMAIN_TYPE = createKey("worldgen/domain_type");
	public static final ResourceKey<Registry<Codec<? extends CurveFunction>>> CURVE_FUNCTION_TYPE = createKey("worldgen/curve_function_type");
	public static final ResourceKey<Registry<Noise>> NOISE = createKey("worldgen/noise");
	@Deprecated
	public static final ResourceKey<Registry<Preset>> PRESET = createKey("worldgen/preset");

	public static ResourceLocation resolve(String name) {
		if (name.contains(":")) return new ResourceLocation(name);
		return new ResourceLocation(ReTerraForged.MOD_ID, name);
	}

	public static <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> registryKey, String valueKey) {
		return ResourceKey.create(registryKey, resolve(valueKey));
	}

	private static <T> ResourceKey<Registry<T>> createKey(String key) {
		return ResourceKey.createRegistryKey(resolve(key));
	}
}
