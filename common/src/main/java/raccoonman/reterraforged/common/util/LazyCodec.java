package raccoonman.reterraforged.common.util;

import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;

public record LazyCodec<A>(Supplier<Codec<A>> codec) implements Codec<A> {

	@Override
	public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
		return this.codec.get().encode(input, ops, prefix);
	}

	@Override
	public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
		return this.codec.get().decode(ops, input);
	}
	
	public static <A> Codec<A> memoize(Supplier<Codec<A>> delegate) {
		return new LazyCodec<>(Suppliers.memoize(delegate::get));
	}
}
