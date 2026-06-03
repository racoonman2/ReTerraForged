package raccoonman.reterraforged.world.worldgen.layer;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.ResourceLocation;

public class ValueReference<A> implements Reference<A>, Resource {
	private CompletableFuture<A> future;
	private Runnable closeCallback;
	private int referenceCount;
	
	private int claimed;
	private int released;
	private int closed;
	
	public ValueReference(CompletableFuture<A> future, Runnable closeCallback) {
		this.future = future;
		this.closeCallback = closeCallback;
	}

	@Override
	public CompletableFuture<A> future() {
		return this.future;
	}

	@Override
	public void claim() {
		this.referenceCount++;
		this.claimed++;
	}
	
	@Override
	public void release() {
		this.released++;
		if(--this.referenceCount == 0) {
			this.close();
		}
	}
	
	@Override
	public void addDebugScreenInfo(DebugScreenDisplayer display, ResourceLocation group) {
		display.addToGroup(group, "Claimed: " + this.claimed);
		display.addToGroup(group, "Released: " + this.released);
		display.addToGroup(group, "Pending: " + (this.claimed - this.released));
		display.addToGroup(group, "Closed: " + this.closed);
	}
	
	@Override
	public void close() {
		this.closed++;
		A value = this.future.resultNow();
		this.closeCallback.run();
		if(value instanceof Resource resource) {
			resource.close();
		}
	}
}
