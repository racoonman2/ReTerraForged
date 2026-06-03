package raccoonman.reterraforged.world.worldgen;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import net.minecraft.server.level.ChunkResult;
import net.minecraft.world.level.chunk.ChunkAccess;
import raccoonman.reterraforged.world.worldgen.layer.Reference;
import raccoonman.reterraforged.world.worldgen.layer.Resource;

public record LayerData(CompletableFuture<ChunkResult<ChunkAccess>> future, List<Reference<?>> references) implements Resource {

	@Override
	public void close() {
		for(Reference<?> ref : this.references) {
			ref.release();
		}
	}	
}
