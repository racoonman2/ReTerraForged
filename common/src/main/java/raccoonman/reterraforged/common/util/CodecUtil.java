/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.util;

import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import raccoonman.reterraforged.platform.registries.RegistryUtil;

public final class CodecUtil {

	public static <A> Codec<A> forLazy(Supplier<Codec<A>> codec) {
		return new Codec<>() {
			private final Supplier<Codec<A>> delegate = Suppliers.memoize(codec::get);
			
			@Override
			public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
				return this.delegate.get().encode(input, ops, prefix);
			}

			@Override
			public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
				return this.delegate.get().decode(ops, input);
			}
		};
	}
	
	public static <V> Codec<V[]> forArray(Codec<V> elementCodec, IntFunction<V[]> generator) {
		return Codec.list(elementCodec).xmap((v) -> {
			return v.toArray(generator);
		}, ImmutableList::copyOf);
	}

	public static <T extends Enum<T>> Codec<T> forEnum(Function<String, T> enumLookup) {
		return Codec.STRING.xmap(String::toUpperCase, String::toLowerCase).xmap(enumLookup::apply, Enum::name);
	}
	
	public static <A> Codec<A> forError(String error) {
		return new Codec<>() {

			@Override
			public <T> DataResult<T> encode(A input, DynamicOps<T> ops, T prefix) {
				return DataResult.error(() -> error);
			}

			@Override
			public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
				return DataResult.error(() -> error);
			}			
		};
	}

	public static <E, A> Codec<E> forRegistry(ResourceKey<Registry<A>> registry, Lifecycle lifecycle, Function<? super E, ? extends A> type, Function<? super A, ? extends Codec<? extends E>> codec) {
		return RegistryUtil.codec(registry, lifecycle).dispatchStable(type, codec);
	}
}
