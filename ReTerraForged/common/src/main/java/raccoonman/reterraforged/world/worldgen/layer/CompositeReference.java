package raccoonman.reterraforged.world.worldgen.layer;

import java.util.concurrent.CompletableFuture;

public class CompositeReference<A> implements Reference<A> {
	private CompletableFuture<A> future;
	private Reference<?>[] children;
	
	public CompositeReference(CompletableFuture<A> future, Reference<?>... children) {
		this.future = future;
		this.children = children;
	}

	@Override
	public CompletableFuture<A> future() {
		return this.future;
	}

	@Override
	public void claim() {
		for(Reference<?> ref : this.children) {
			ref.claim();
		}
	}

	@Override
	public void release() {
		for(Reference<?> ref : this.children) {
			ref.release();
		}
	}
}
