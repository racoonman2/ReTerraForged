package raccoonman.reterraforged.world.worldgen.layer;

import java.util.concurrent.CompletableFuture;

import net.minecraft.client.gui.components.debug.DebugScreenDisplayer;
import net.minecraft.resources.ResourceLocation;

public interface Reference<A> {
	CompletableFuture<A> future();
	
	void claim();
	
	void release();

	default void addDebugScreenInfo(DebugScreenDisplayer display, ResourceLocation group) {
	}
}
