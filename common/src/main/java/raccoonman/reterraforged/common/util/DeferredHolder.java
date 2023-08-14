package raccoonman.reterraforged.common.util;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public record DeferredHolder<T>(Supplier<Holder<T>> supplier) implements Holder<T> {

	public DeferredHolder {
		supplier = Suppliers.memoize(supplier::get);
	}
	
	@Override
	public T value() {
		return this.supplier.get().value();
	}

	@Override
	public boolean isBound() {
		return this.supplier.get().isBound();
	}

	@Override
	public boolean is(ResourceLocation loc) {
		return this.supplier.get().is(loc);
	}

	@Override
	public boolean is(ResourceKey<T> key) {
		return this.supplier.get().is(key);
	}

	@Override
	public boolean is(Predicate<ResourceKey<T>> filter) {
		return this.supplier.get().is(filter);
	}

	@Override
	public boolean is(TagKey<T> tag) {
		return this.supplier.get().is(tag);
	}

	@Override
	public Stream<TagKey<T>> tags() {
		return this.supplier.get().tags();
	}

	@Override
	public Either<ResourceKey<T>, T> unwrap() {
		return this.supplier.get().unwrap();
	}

	@Override
	public Optional<ResourceKey<T>> unwrapKey() {
		return this.supplier.get().unwrapKey();
	}

	@Override
	public Kind kind() {
		return this.supplier.get().kind();
	}

	@Override
	public boolean canSerializeIn(HolderOwner<T> owner) {
		return this.supplier.get().canSerializeIn(owner);
	}
}
