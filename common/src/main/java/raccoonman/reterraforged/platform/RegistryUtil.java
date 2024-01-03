package raccoonman.reterraforged.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.registries.RTFRegistries;

@Deprecated
public final class RegistryUtil {
	
	public static <T> void register(Registry<T> registry, String name, T value) {
		getWritable(registry).register(RTFRegistries.createKey(registry.key(), name), value, Lifecycle.stable());
	}
	
	@ExpectPlatform
	public static <T> WritableRegistry<T> getWritable(Registry<T> registry) {
		throw new IllegalStateException();
	}
	
	@ExpectPlatform
	public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> key) {
		throw new IllegalStateException();
	}

	@ExpectPlatform
	public static <T> void createDataRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
		throw new IllegalStateException();
	}
}
