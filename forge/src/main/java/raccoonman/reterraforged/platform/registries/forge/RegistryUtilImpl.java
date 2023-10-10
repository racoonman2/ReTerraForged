package raccoonman.reterraforged.platform.registries.forge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryBuilder;
import raccoonman.reterraforged.common.ReTerraForged;

public final class RegistryUtilImpl {
	private static final Map<ResourceKey<? extends Registry<?>>, DeferredRegistry.Writable<?>> REGISTERS = new ConcurrentHashMap<>();
	private static final List<DataRegistry<?>> DATA_REGISTRIES = Collections.synchronizedList(new ArrayList<>());

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(IEventBus bus) {
		for(DeferredRegistry.Writable<?> registry : REGISTERS.values()) {
			registry.register(bus);
		}
		
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			for(DataRegistry registry : DATA_REGISTRIES) {
				event.dataPackRegistry(registry.key(), registry.codec());
			}
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> WritableRegistry<T> getWritable(Registry<T> registry) {
		return (WritableRegistry<T>) REGISTERS.computeIfAbsent(registry.key(), (k) -> {
			return new DeferredRegistry.Writable<>(DeferredRegister.create((ResourceKey) k, ReTerraForged.MOD_ID));
		});
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Registry<T> createRegistry(ResourceKey<? extends Registry<T>> key) {
		DeferredRegister<T> register = DeferredRegister.create((ResourceKey) key, ReTerraForged.MOD_ID);
		register.makeRegistry(() -> {
			return new RegistryBuilder().hasTags();
		});
		REGISTERS.put(key, new DeferredRegistry.Writable<>(register));
		return DeferredRegistry.memoize(key, () -> {
			return GameData.getWrapper(key, Lifecycle.stable());
		});
	}

	public static <T> void createDataRegistry(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
		DATA_REGISTRIES.add(new DataRegistry<>(key, codec));
	}
	
	private record DataRegistry<T>(ResourceKey<? extends Registry<T>> key, Codec<T> codec) {
	}
}
