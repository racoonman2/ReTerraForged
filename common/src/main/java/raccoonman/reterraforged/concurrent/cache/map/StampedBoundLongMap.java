package raccoonman.reterraforged.concurrent.cache.map;

import java.util.function.LongFunction;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import java.util.function.Predicate;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import java.util.concurrent.locks.StampedLock;

public class StampedBoundLongMap<T> implements LongMap<T> {
	private int capacity;
	private StampedLock lock;
	private Long2ObjectLinkedOpenHashMap<T> map;

	public StampedBoundLongMap(int size) {
		this.capacity = size;
		this.lock = new StampedLock();
		this.map = new Long2ObjectLinkedOpenHashMap<>(size);
	}

	@Override
	public int size() {
		long stamp = this.lock.readLock();
		try {
			return this.map.size();
		} finally {
			this.lock.unlockRead(stamp);
		}
	}

	@Override
	public void clear() {
		long stamp = this.lock.writeLock();
		try {
			this.map.clear();
		} finally {
			this.lock.unlockWrite(stamp);
		}
	}

	@Override
	public void remove(long key) {
		long stamp = this.lock.writeLock();
		try {
			this.map.remove(key);
		} finally {
			this.lock.unlockWrite(stamp);
		}
	}

	@Override
	public void remove(long key, Consumer<T> consumer) {
		long stamp = this.lock.writeLock();
		T t;
		try {
			t = this.map.remove(key);
		} finally {
			this.lock.unlockWrite(stamp);
		}
		if (t != null) {
			consumer.accept(t);
		}
	}

	@Override
	public int removeIf(Predicate<T> predicate) {
		long stamp = this.lock.writeLock();
		try {
			int startSize = this.map.size();
			ObjectIterator<Long2ObjectMap.Entry<T>> iterator = this.map.long2ObjectEntrySet().fastIterator();
			while (iterator.hasNext()) {
				Long2ObjectMap.Entry<T> entry = (Long2ObjectMap.Entry<T>) iterator.next();
				if (predicate.test((T) entry.getValue())) {
					iterator.remove();
				}
			}
			return startSize - this.map.size();
		} finally {
			this.lock.unlockWrite(stamp);
		}
	}

	@Override
	public void put(long key, T t) {
		long stamp = this.lock.writeLock();
		try {
			this.map.put(key, t);
		} finally {
			this.lock.unlockWrite(stamp);
		}
	}

	@Override
	public T get(long key) {
		long stamp = this.lock.readLock();
		try {
			return this.map.get(key);
		} finally {
			this.lock.unlockRead(stamp);
		}
	}

	@Override
	public T computeIfAbsent(long key, LongFunction<T> func) {
		long readStamp = this.lock.readLock();
		try {
			T t = this.map.get(key);
			if (t != null) {
				return t;
			}
		} finally {
			this.lock.unlockRead(readStamp);
		}
		long writeStamp = this.lock.writeLock();
		try {
			if (this.map.size() >= this.capacity) {
				this.map.removeFirst();
			}
			return this.map.computeIfAbsent(key, func);
		} finally {
			this.lock.unlockWrite(writeStamp);
		}
	}
}
