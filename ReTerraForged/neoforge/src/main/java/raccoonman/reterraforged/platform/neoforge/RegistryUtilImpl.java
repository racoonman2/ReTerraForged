package raccoonman.reterraforged.platform.neoforge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader.RegistryData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DataPackRegistriesHooks;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import raccoonman.reterraforged.RTFCommon;

public class RegistryUtilImpl {
	private static final List<Registry<?>> BUILTIN_REGISTRIES = Collections.synchronizedList(new ArrayList<>());
	private static final List<DataRegistry<?>> DATA_REGISTRIES = Collections.synchronizedList(new ArrayList<>());
	private static final Map<ResourceKey<?>, DeferredRegister<?>> REGISTERS = new ConcurrentHashMap<>();

	public static void register(IEventBus bus) {
		bus.addListener((NewRegistryEvent event) -> {
			BUILTIN_REGISTRIES.forEach(event::register);
		});
		
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			DATA_REGISTRIES.forEach((registry) -> registry.register(event));
		});

		REGISTERS.values().forEach((register) -> register.register(bus));
	}

	public static <T> void register(Registry<T> registry, String name, T value) {
		DeferredRegister<T> deferredRegistry = getRegister(registry.key());
		deferredRegistry.register(name, () -> value);
	}

	public static <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
		Registry<T> registry = new RegistryBuilder<>(key).create();
		BUILTIN_REGISTRIES.add(registry);
		return registry;
	}

	public static <T> void createDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec, boolean synced) {
		DATA_REGISTRIES.add(new DataRegistry<>(key, codec, synced));
	}

	public static <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(String name, GameRules.Category category, GameRules.Type<T> type) {
		return GameRules.register(name, category, type);
	}
	
	public static List<RegistryData<?>> getDynamicRegistries() {
		return DataPackRegistriesHooks.getDataPackRegistries();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> DeferredRegister<T> getRegister(ResourceKey<? extends Registry<T>> key) {
		return (DeferredRegister<T>) REGISTERS.computeIfAbsent(key, (v) -> DeferredRegister.create(key, RTFCommon.MOD_ID));
	}
	
	private record DataRegistry<T>(ResourceKey<Registry<T>> key, Codec<T> codec, boolean synced) {
		
		public void register(DataPackRegistryEvent.NewRegistry event) {
			event.dataPackRegistry(this.key, this.codec, this.synced ? this.codec : null);
		}
	}
}