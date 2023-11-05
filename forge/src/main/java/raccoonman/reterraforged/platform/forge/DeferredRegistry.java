package raccoonman.reterraforged.platform.forge;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Holder;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup.RegistryLookup;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.HolderSet.Named;
import net.minecraft.core.Registry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.GameData;

// this class sucks and i hate it
//TODO we should probably override default methods here too
class DeferredRegistry<T> implements Registry<T> {
	private ResourceKey<? extends Registry<T>> key;
	protected Supplier<Registry<T>> registry;
	
	public DeferredRegistry(ResourceKey<? extends Registry<T>> key, Supplier<Registry<T>> registry) {
		this.key = key;
		this.registry = registry;
	}
	
	@Override
	public T byId(int id) {
		return this.registry.get().byId(id);
	}

	@Override
	public int size() {
		return this.registry.get().size();
	}

	@Override
	public Iterator<T> iterator() {
		return this.registry.get().iterator();
	}

	@Override
	public ResourceKey<? extends Registry<T>> key() {
		return this.key;
	}

	@Override
	public ResourceLocation getKey(T var1) {
		return this.registry.get().getKey(var1);
	}

	@Override
	public Optional<ResourceKey<T>> getResourceKey(T var1) {
		return this.registry.get().getResourceKey(var1);
	}

	@Override
	public int getId(T var1) {
		return this.registry.get().getId(var1);
	}

	@Override
	public T get(ResourceKey<T> var1) {
		return this.registry.get().get(var1);
	}

	@Override
	public T get(ResourceLocation var1) {
		return this.registry.get().get(var1);
	}

	@Override
	public Lifecycle lifecycle(T var1) {
		return this.registry.get().lifecycle(var1);
	}

	@Override
	public Lifecycle registryLifecycle() {
		return this.registry.get().registryLifecycle();
	}

	@Override
	public Set<ResourceLocation> keySet() {
		return this.registry.get().keySet();
	}

	@Override
	public Set<Entry<ResourceKey<T>, T>> entrySet() {
		return this.registry.get().entrySet();
	}

	@Override
	public Set<ResourceKey<T>> registryKeySet() {
		return this.registry.get().registryKeySet();
	}

	@Override
	public Optional<Reference<T>> getRandom(RandomSource var1) {
		return this.registry.get().getRandom(var1);
	}

	@Override
	public boolean containsKey(ResourceLocation var1) {
		return this.registry.get().containsKey(var1);
	}

	@Override
	public boolean containsKey(ResourceKey<T> var1) {
		return this.registry.get().containsKey(var1);
	}

	@Override
	public Registry<T> freeze() {
		return this.registry.get().freeze();
	}

	@Override
	public Reference<T> createIntrusiveHolder(T var1) {
		return this.registry.get().createIntrusiveHolder(var1);
	}

	@Override
	public Optional<Reference<T>> getHolder(int var1) {
		return this.registry.get().getHolder(var1);
	}

	@Override
	public Optional<Reference<T>> getHolder(ResourceKey<T> var1) {
		return this.registry.get().getHolder(var1);
	}

	@Override
	public Holder<T> wrapAsHolder(T var1) {
		return this.registry.get().wrapAsHolder(var1);
	}

	@Override
	public Stream<Reference<T>> holders() {
		return this.registry.get().holders();
	}

	@Override
	public Optional<Named<T>> getTag(TagKey<T> var1) {
		return this.registry.get().getTag(var1);
	}

	@Override
	public Named<T> getOrCreateTag(TagKey<T> var1) {
		return this.registry.get().getOrCreateTag(var1);
	}

	@Override
	public Stream<Pair<TagKey<T>, Named<T>>> getTags() {
		return this.registry.get().getTags();
	}

	@Override
	public Stream<TagKey<T>> getTagNames() {
		return this.registry.get().getTagNames();
	}

	@Override
	public void resetTags() {
		this.registry.get().resetTags();
	}

	@Override
	public void bindTags(Map<TagKey<T>, List<Holder<T>>> var1) {
		this.registry.get().bindTags(var1);
	}

	@Override
	public HolderOwner<T> holderOwner() {
		return this.registry.get().holderOwner();
	}

	@Override
	public RegistryLookup<T> asLookup() {
		return this.registry.get().asLookup();
	}
	
	public static <T> Registry<T> memoize(ResourceKey<? extends Registry<T>> key, Supplier<Registry<T>> supplier) {
		return new DeferredRegistry<>(key, Suppliers.memoize(supplier::get));
	}
	
	public static class Writable<T> extends DeferredRegistry<T> implements WritableRegistry<T> {
		private final DeferredRegister<T> register;
		
		public Writable(DeferredRegister<T> register) {
			super(register.getRegistryKey(), () -> GameData.getWrapper(register.getRegistryKey(), Lifecycle.stable()));
			
			this.register = register;
		}
		
		public void register(IEventBus bus) {
			this.register.register(bus);
		}
		
		@Override
		public Holder<T> registerMapping(int i, ResourceKey<T> key, T value, Lifecycle lifecycle) {
			Holder.Reference<T> holder = Holder.Reference.createStandAlone(new HolderOwner<>() {
			    
				@Override
				public boolean canSerializeIn(HolderOwner<T> arg) {
			        return false;
			    }
			}, key);
			this.register.register(key.location().getPath(), () -> value);
			return holder;
		}

		@Override
		public Reference<T> register(ResourceKey<T> key, T value, Lifecycle lifecycle) {
			Holder.Reference<T> holder = Holder.Reference.createStandAlone(new HolderOwner<>() {
			    
				@Override
				public boolean canSerializeIn(HolderOwner<T> arg) {
			        return false;
			    }
			}, key);
			this.register.register(key.location().getPath(), () -> value);
			return holder;
		}

		@Override
		public boolean isEmpty() {
			throw new UnsupportedOperationException();
		}

		@Override
		public HolderGetter<T> createRegistrationLookup() {
			throw new UnsupportedOperationException();
		}
	}
}
