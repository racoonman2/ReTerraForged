package raccoonman.reterraforged.platform;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryDataLoader.RegistryData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.GameRules;

public class RegistryUtil {
	
	@ExpectPlatform
	public static <T> void register(Registry<T> registry, String name, T value) {
		throw new IllegalStateException();
	}
	
	@ExpectPlatform
	public static <T> Registry<T> createRegistry(ResourceKey<Registry<T>> key) {
		throw new IllegalStateException();
	}

	@ExpectPlatform
	public static <T> void createDataRegistry(ResourceKey<Registry<T>> key, Codec<T> codec, boolean synced) {
		throw new IllegalStateException();
	}
	
	@ExpectPlatform
	public static <T extends GameRules.Value<T>> GameRules.Key<T> registerGameRule(String name, GameRules.Category category, GameRules.Type<T> type) {
		throw new IllegalStateException();
	}
	
	@ExpectPlatform
	public static List<RegistryData<?>> getDynamicRegistries() {
		throw new IllegalStateException();
	}

	public static List<RegistryData<?>> getDynamicRegistriesWithDimensions() {
		ImmutableList.Builder<RegistryData<?>> list = ImmutableList.builder();
		list.addAll(getDynamicRegistries());
		list.addAll(RegistryDataLoader.DIMENSION_REGISTRIES);
		return list.build();
	}
}
