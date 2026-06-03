package raccoonman.reterraforged.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.KeyTagProvider;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.RTFCommon;

public record RegistryTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) implements DataProvider {

	@Override
	public CompletableFuture<Void> run(CachedOutput cachedOutput) {
		return this.registries.thenCompose((provider) -> {
			CompletableFuture<?>[] futures = provider.listRegistries().map((lookup) -> {
				return this.runProvider(lookup, cachedOutput);
			}).toArray(CompletableFuture[]::new);
			return CompletableFuture.allOf(futures);
		});
	}

	@Override
	public String getName() {
		return "Registry tags";
	}
	
	private CompletableFuture<?> runProvider(RegistryLookup<?> lookup, CachedOutput output) {
		return new TagProvider<>(lookup).run(output);
	}
	
	private class TagProvider<T> extends KeyTagProvider<T> {
		private RegistryLookup<T> lookup;
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		protected TagProvider(RegistryLookup<T> lookup) {
			super(RegistryTagProvider.this.output, (ResourceKey) lookup.key(), RegistryTagProvider.this.registries);
			
			this.lookup = lookup;
		}

		@Override
		protected void addTags(HolderLookup.Provider lookup) {
			this.lookup.listTags().forEach((tag) -> {
				TagAppender<ResourceKey<T>, T> appender = this.tag(tag.key());
				tag.forEach((holder) -> {
					holder.unwrapKey().ifPresentOrElse(appender::add, () -> {
						RTFCommon.LOGGER.error("Couldn't serialize inlined tag element: {}", holder.value());
					});
				});
			});
		}
	}
}
