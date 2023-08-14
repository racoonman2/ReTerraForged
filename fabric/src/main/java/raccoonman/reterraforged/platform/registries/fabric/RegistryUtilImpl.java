package raccoonman.reterraforged.platform.registries.fabric;

import java.util.function.Supplier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.mixin.registry.sync.RegistriesAccessor;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.common.util.CodecUtil;

public final class RegistryUtilImpl {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <A> Codec<A> codec(ResourceKey<? extends Registry<A>> registry, Lifecycle lifecycle) {
		// does this need to be lazy?
		return CodecUtil.forLazy(() -> {
			return RegistriesAccessor.getROOT().getOrThrow((ResourceKey) registry).byNameCodec();
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> void register(ResourceKey<T> key, Supplier<T> value) {
		ResourceKey registry = ResourceKey.createRegistryKey(key.registry());
		Registry.register(RegistriesAccessor.getROOT().getOrThrow(registry), key, value.get());
	}
	
	public static <T> void createRegistry(ResourceKey<Registry<T>> key) {
		FabricRegistryBuilder.createSimple(key).buildAndRegister();
	}

	public static <T> void createDataRegistry(ResourceKey<Registry<T>> key, Codec<T> directCodec) {
		DynamicRegistries.register(key, directCodec);
	}
}
