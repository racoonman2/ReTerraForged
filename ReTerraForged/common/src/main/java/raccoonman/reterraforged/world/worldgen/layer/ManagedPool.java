package raccoonman.reterraforged.world.worldgen.layer;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Supplier;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.ResourceLocation;
import raccoonman.reterraforged.RTFCommon;
import raccoonman.reterraforged.world.Ticking;

public class ManagedPool<A> implements Ticking {
	private static final int TPS = 20;
	private static final int UPDATE_INTERVAL = secondsToTicks(30);
	private static final int MAX_STALE_UPDATES = 3;
	
	private int cullThreshold;
	private Supplier<A> factory;
	private Deque<ManagedPool<A>.Entry> pool;
	private int totalAllocated;
	private int lastUpdateTick;
	
	public ManagedPool(int cullThreshold, Supplier<A> factory) {
		this.cullThreshold = cullThreshold;
		this.factory = factory;
		this.pool = new LinkedList<>();
		this.lastUpdateTick = 0;
	}

	public A take() {
		ManagedPool<A>.Entry entry = this.pool.pollLast();
		if(entry != null) {
			return entry.value;
		}
		this.totalAllocated++;
		return this.factory.get();
	}
	
	public void add(A value) {
		ManagedPool<A>.Entry entry = this.new Entry();
		entry.value = value;
		this.pool.add(entry);
	}
	
	@Override
	public void tick(int currentTick) {
		if(currentTick - this.lastUpdateTick < UPDATE_INTERVAL) {
			return;
		}
		
		if(this.pool.size() > this.cullThreshold) {
			this.cullStaleEntries();
		}
		
		this.lastUpdateTick = currentTick;
	}
	
	private void cullStaleEntries() {
		int culled = 0;
		Iterator<ManagedPool<A>.Entry> iterator = this.pool.iterator();
		while(iterator.hasNext()) {
			ManagedPool<A>.Entry entry = iterator.next();
			if(entry.staleUpdates++ < MAX_STALE_UPDATES) {
				continue;
			}
			iterator.remove();
			this.totalAllocated--;
			culled++;
		}
	
		if(culled > 0) {
			RTFCommon.LOGGER.info("Culled {} entries", culled);
		}
	}
	
	public void addDebugScreenInfo(DebugScreenDisplayer display, ResourceLocation group) {
		display.addToGroup(group, "Resources: " + this.pool.size() + "/" + this.totalAllocated);
	}

	private static int secondsToTicks(int seconds) {
		return seconds * TPS;
	}
	
	private class Entry {
		A value;
		int staleUpdates;
	}
}