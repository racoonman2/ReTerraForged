package raccoonman.reterraforged.platform.fabric;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.slf4j.Logger;

import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class DataGenUtilImpl {

	public static DataProvider createRegistryProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> providerLookup) {
		return new Provider(output, providerLookup);
	}

	// a port of the forge patches to RegistriesDatapackGenerator
	private static class Provider implements DataProvider {
	    private static final Logger LOGGER = LogUtils.getLogger();
	    private final PackOutput output;
	    private final CompletableFuture<HolderLookup.Provider> registries;

	    public Provider(PackOutput arg, CompletableFuture<HolderLookup.Provider> completableFuture) {
	        this.registries = completableFuture;
	        this.output = arg;
	    }

	    @Override
	    public CompletableFuture<?> run(CachedOutput arg) {
	        return this.registries.thenCompose(provider -> {
	            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, provider);
	            return CompletableFuture.allOf(DynamicRegistries.getDynamicRegistries().stream().flatMap(arg3 -> this.dumpRegistryCap(arg, provider, ops, arg3).stream()).toArray(CompletableFuture[]::new));
	        });
	    }

	    private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput output, HolderLookup.Provider provider, DynamicOps<JsonElement> ops, RegistryDataLoader.RegistryData<T> registryData) {
	        ResourceKey<? extends Registry<T>> resourcekey = registryData.key();

	        return provider.lookup(resourcekey).map(lookup -> {
	            PackOutput.PathProvider path = this.output.createPathProvider(PackOutput.Target.DATA_PACK, prefixNamespace(resourcekey.location()));
	            Stream<Holder.Reference<T>> holders = lookup.listElements();
	            return CompletableFuture.allOf(holders.map((ref) -> {
	            	return dumpValue(path.json(ref.key().location()), output, ops, registryData.elementCodec(), ref.value());
	            }).toArray(CompletableFuture[]::new));
	        });
	    }

	    private static String prefixNamespace(ResourceLocation location) {
	        return location.getNamespace().equals("minecraft") ? location.getPath() : location.getNamespace() +  "/"  + location.getPath();
	    }

	    private static <E> CompletableFuture<?> dumpValue(Path path, CachedOutput arg, DynamicOps<JsonElement> dynamicOps, Encoder<E> encoder, E object) {
	        Optional<JsonElement> optional = encoder.encodeStart(dynamicOps, object).resultOrPartial(string -> LOGGER.error("Couldn't serialize element {}: {}", path, string));
	        return optional.isPresent() ? DataProvider.saveStable(arg, optional.get(), path) : CompletableFuture.completedFuture(null);
	    }

	    @Override
	    public String getName() {
	        return "Registries";
	    }
	}
}