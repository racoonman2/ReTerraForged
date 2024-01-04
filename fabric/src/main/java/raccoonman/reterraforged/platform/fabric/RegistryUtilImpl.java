package raccoonman.reterraforged.platform.fabric;

import com.mojang.serialization.Codec;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;

public class RegistryUtilImpl {
	
	public static <T> WritableRegistry<T> getWritable(Registry<T> registry) {
		return (WritableRegistry<T>) registry;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> key) {
		return FabricRegistryBuilder.createSimple((ResourceKey<Registry<T>>) key).buildAndRegister();
	}

	public static <T> void createDataRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
		DynamicRegistries.register(key, codec);
	}
}
