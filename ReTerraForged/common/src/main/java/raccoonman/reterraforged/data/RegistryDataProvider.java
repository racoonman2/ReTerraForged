package raccoonman.reterraforged.data;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import com.mojang.serialization.JsonOps;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.platform.RegistryUtil;

public record RegistryDataProvider(PackOutput packOutput, CompletableFuture<List<HolderLookup.Provider>> providers) implements DataProvider {

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
    	return this.providers.thenCompose((providers) -> {
    		return CompletableFuture.allOf(providers.stream().map((provider) -> this.serialize(provider, cachedOutput)).toArray(CompletableFuture[]::new));
    	});
    }

    @Override
    public String getName() {
        return "Registries";
    }
    
    private CompletableFuture<?> serialize(HolderLookup.Provider lookupProvider, CachedOutput cachedOutput) {
    	RegistryOps<JsonElement> dynamicOps = lookupProvider.createSerializationContext(JsonOps.INSTANCE);
    	return CompletableFuture.allOf(RegistryUtil.getDynamicRegistriesWithDimensions().stream().flatMap(registryData -> this.dumpRegistryCap(cachedOutput, lookupProvider, dynamicOps, registryData).stream()).toArray(CompletableFuture[]::new));
    }

    private <T> Optional<CompletableFuture<?>> dumpRegistryCap(CachedOutput cachedOutput, HolderLookup.Provider provider, DynamicOps<JsonElement> dynamicOps, RegistryDataLoader.RegistryData<T> registryData) {
        ResourceKey<? extends Registry<T>> registryKey = registryData.key();
        return provider.lookup(registryKey).map(registry -> {
            PackOutput.PathProvider pathProvider = this.packOutput.createPathProvider(PackOutput.Target.DATA_PACK, prefixNamespace(registryKey.location()));
            return CompletableFuture.allOf(registry.listElements().map(ref -> {
            	CompletableFuture<?> future = dumpValue(pathProvider.json(ref.key().location()), cachedOutput, dynamicOps, registryData.elementCodec(), ref.value());
            	return future;
            }).toArray(CompletableFuture[]::new));
        });
    }
    
    private static String prefixNamespace(ResourceLocation location) {
        return location.getNamespace().equals("minecraft") ? location.getPath() : location.getNamespace() +  "/"  + location.getPath();
    }

    private static <E> CompletableFuture<?> dumpValue(Path path, CachedOutput cachedOutput, DynamicOps<JsonElement> dynamicOps, Encoder<E> encoder, E object) {
        return encoder.encodeStart(dynamicOps, object).mapOrElse(element -> DataProvider.saveStable(cachedOutput, element, path), error -> CompletableFuture.failedFuture(new IllegalStateException("Couldn't generate file '" + String.valueOf(path) + "': " + error.message())));
    }
}