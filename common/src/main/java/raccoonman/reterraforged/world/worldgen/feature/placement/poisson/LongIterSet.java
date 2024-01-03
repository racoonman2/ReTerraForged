package raccoonman.reterraforged.world.worldgen.feature.placement.poisson;

import it.unimi.dsi.fastutil.longs.LongArrays;
import java.util.Random;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;

public class LongIterSet {
	public static long NULL = Long.MAX_VALUE;
	private int size;
	private int index;
	private long[] order;
	private LongSet points;

	public LongIterSet() {
		this.size = 0;
		this.index = -1;
		this.order = new long[32];
		this.points = (LongSet) new LongOpenHashSet(32);
	}

	public boolean contains(long value) {
		return this.points.contains(value);
	}

	public boolean add(long value) {
		if (this.points.add(value)) {
			(this.order = ensureCapacity(this.order, this.size))[this.size++] = value;
			return true;
		}
		return false;
	}

	public void clear() {
		this.points.clear();
		this.index = -1;
		this.size = 0;
	}

	public void shuffle(Random random) {
		LongArrays.shuffle(this.order, 0, this.size, random);
	}

	public boolean hasNext() {
		return this.index + 1 < this.size;
	}

	public long nextLong() {
		while (++this.index < this.size) {
			long value = this.order[this.index];
			if (value != Long.MAX_VALUE && this.points.contains(value)) {
				return value;
			}
		}
		return Long.MAX_VALUE;
	}

	public void remove() {
		long value = this.order[this.index];
		this.points.remove(value);
		this.order[this.index] = Long.MAX_VALUE;
	}

	public void reset() {
		this.index = -1;
	}

	private static long[] ensureCapacity(long[] backing, int index) {
		if (backing.length <= index) {
			long[] next = new long[backing.length << 1];
			System.arraycopy(backing, 0, next, 0, backing.length);
			return next;
		}
		return backing;
	}
}
