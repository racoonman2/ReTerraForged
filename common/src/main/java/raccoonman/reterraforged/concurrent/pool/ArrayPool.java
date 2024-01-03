package raccoonman.reterraforged.concurrent.pool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;
import java.util.function.Supplier;

import raccoonman.reterraforged.concurrent.Resource;

public class ArrayPool<T> {
	private int capacity;
	private IntFunction<T[]> constructor;
	private List<Item<T>> pool;
	private Object lock;

	public ArrayPool(int size, IntFunction<T[]> constructor) {
		this.lock = new Object();
		this.capacity = size;
		this.constructor = constructor;
		this.pool = new ArrayList<>(size);
	}

	public Resource<T[]> get(int arraySize) {
		synchronized (this.lock) {
			if (this.pool.size() > 0) {
				Item<T> resource = this.pool.remove(this.pool.size() - 1);
				if (resource.get().length >= arraySize) {
					return resource.retain();
				}
			}
		}
		return new Item<>(this.constructor.apply(arraySize), this);
	}

	private boolean restore(Item<T> item) {
		synchronized (this.lock) {
			if (this.pool.size() < this.capacity) {
				this.pool.add(item);
				return true;
			}
		}
		return false;
	}

	public static <T> ArrayPool<T> of(int size, IntFunction<T[]> constructor) {
		return new ArrayPool<>(size, constructor);
	}

	public static <T> ArrayPool<T> of(int size, Supplier<T> supplier, IntFunction<T[]> constructor) {
		return new ArrayPool<>(size, new ArrayConstructor<>(supplier, constructor));
	}

	public static class Item<T> implements Resource<T[]> {
		private T[] value;
		private ArrayPool<T> pool;
		private boolean released;

		private Item(T[] value, ArrayPool<T> pool) {
			this.released = false;
			this.value = value;
			this.pool = pool;
		}

		@Override
		public T[] get() {
			return this.value;
		}

		@Override
		public boolean isOpen() {
			return !this.released;
		}

		@Override
		public void close() {
			if (!this.released) {
				this.released = true;
				this.released = this.pool.restore(this);
			}
		}

		private Item<T> retain() {
			this.released = false;
			return this;
		}
	}

	private static class ArrayConstructor<T> implements IntFunction<T[]> {
		private Supplier<T> element;
		private IntFunction<T[]> array;

		private ArrayConstructor(Supplier<T> element, IntFunction<T[]> array) {
			this.element = element;
			this.array = array;
		}

		@Override
		public T[] apply(int size) {
			T[] t = this.array.apply(size);
			for (int i = 0; i < t.length; ++i) {
				t[i] = this.element.get();
			}
			return t;
		}
	}
}
