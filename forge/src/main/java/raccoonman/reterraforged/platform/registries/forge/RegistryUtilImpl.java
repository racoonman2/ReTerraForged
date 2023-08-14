package raccoonman.reterraforged.platform.registries.forge;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.RegistryBuilder;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.util.CodecUtil;

//TODO handle overrides
public final class RegistryUtilImpl {
	private static final Map<ResourceKey<? extends Registry<?>>, DeferredRegister<?>> REGISTERS = Maps.newConcurrentMap();
	private static final Map<ResourceKey<? extends Registry<?>>, Codec<?>> CODECS = Maps.newConcurrentMap();
	
	public static <A> Codec<A> codec(ResourceKey<? extends Registry<A>> registry, Lifecycle lifecycle) {
		return CodecUtil.forLazy(() -> {
			return GameData.getWrapper(registry, lifecycle).byNameCodec();
		});
	}
	
	public static <T> void createRegistry(ResourceKey<Registry<T>> key) {
		ResourceLocation registry = key.location();
		REGISTERS.computeIfAbsent(ResourceKey.createRegistryKey(registry), (f) -> {
			DeferredRegister<T> register = DeferredRegister.create(registry, registry.getNamespace());
			register.makeRegistry(() -> {
				RegistryBuilder<T> builder = new RegistryBuilder<>();
				builder.hasTags();
				return builder.onCreate((owner, stage) -> {
					ReTerraForged.LOGGER.info("Created registry {}", registry);
				});
			});
			return register;
		});
	}
	
	
	public static <T> void createDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec) {
		CODECS.computeIfAbsent(key, (k) -> {
			return codec;
		});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> void register(ResourceKey<T> key, Supplier<T> value) {
		ResourceLocation registry = key.registry();
		ResourceLocation location = key.location();
		REGISTERS.computeIfAbsent(ResourceKey.createRegistryKey(registry), (f) -> {
			return DeferredRegister.create(registry, location.getNamespace());
		}).register(location.getPath(), (Supplier) value);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void register(IEventBus bus) {
		for(DeferredRegister<?> register : REGISTERS.values()) {
			register.register(bus);
		}
		bus.addListener((DataPackRegistryEvent.NewRegistry event) -> {
			for(Entry<ResourceKey<? extends Registry<?>>, Codec<?>> entry : CODECS.entrySet()) {
				ResourceKey key = entry.getKey();
				Codec value = entry.getValue();
				
				event.dataPackRegistry(key, value);
			}
		});
	}
}
