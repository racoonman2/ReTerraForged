package raccoonman.reterraforged.platform.registries;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public final class RegistryUtil {

	@ExpectPlatform
	public static <A> Codec<A> codec(ResourceKey<? extends Registry<A>> registry, Lifecycle lifecycle) {
		throw missingImplementation();
	}
	
	@ExpectPlatform
	public static <T> void register(ResourceKey<T> key, Supplier<T> value) {
		throw missingImplementation();
	}
	
	@ExpectPlatform
	public static <T> void createRegistry(ResourceKey<Registry<T>> key) {
		throw missingImplementation();
	}
	

	@ExpectPlatform
	public static <T> void createDataRegistry(ResourceKey<Registry<T>> noise, Codec<T> directCodec) {
		throw missingImplementation();
	}
	
	private static final RuntimeException missingImplementation() {
		return new UnsupportedOperationException("Missing platform implementation");
	}
}
