package raccoonman.reterraforged.registry;

import com.mojang.serialization.Codec;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryUtil {

	public static <A, B> ResourceKey<B> createTypedKey(ResourceKey<Registry<A>> registryKey, ResourceLocation location) {
		ResourceKey<Registry<B>> typedRegistryKey = ResourceKey.createRegistryKey(registryKey.location());
		return ResourceKey.create(typedRegistryKey, location);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <A, B> Holder.Reference<A> registerTyped(BootstrapContext<B> ctx, ResourceKey<A> key, A value) {
		return ctx.register((ResourceKey) key, (B) value);
	}

	public static <B> Holder<B> lookupTyped(BootstrapContext<?> ctx, ResourceKey<B> key) {
		return ctx.lookup(key.registryKey()).getOrThrow(key);
	}

	public static <B> Holder<B> lookupTyped(HolderLookup.Provider ctx, ResourceKey<B> key) {
		return ctx.lookupOrThrow(key.registryKey()).getOrThrow(key);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <A> Codec<Holder<A>> codec(Codec<?> codec) {
		return (Codec) codec;
	}
}
