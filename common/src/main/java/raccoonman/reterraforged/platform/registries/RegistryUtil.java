package raccoonman.reterraforged.platform.registries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.ReTerraForged;

public final class RegistryUtil {
	
	public static <T> void register(Registry<T> registry, String name, T value) {
		getWritable(registry).register(ReTerraForged.resolve(registry.key(), name), value, Lifecycle.stable());
	}
	
	@ExpectPlatform
	public static <T> WritableRegistry<T> getWritable(Registry<T> registry) {
		throw missingImplementation();
	}
	
	@ExpectPlatform
	public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> key) {
		throw missingImplementation();
	}

	@ExpectPlatform
	public static <T> void createDataRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
		throw missingImplementation();
	}
	
	private static final RuntimeException missingImplementation() {
		return new UnsupportedOperationException("Missing platform implementation");
	}
}
