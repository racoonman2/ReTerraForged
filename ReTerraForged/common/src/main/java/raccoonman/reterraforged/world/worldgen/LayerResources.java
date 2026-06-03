package raccoonman.reterraforged.world.worldgen;

import net.minecraft.world.level.chunk.ChunkAccess;

@Deprecated
public class LayerResources {
	private static final ThreadLocal<ChunkAccess> CURRENT_CHUNK = new ThreadLocal<>();
	
	public static void setCurrentChunk(ChunkAccess chunk) {
		CURRENT_CHUNK.set(chunk);
	}
	
	@Deprecated
	public static ChunkAccess getCurrentChunk() {
		return CURRENT_CHUNK.get();
	}
}