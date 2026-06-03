package raccoonman.reterraforged.mixin;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.level.chunk.ChunkGenerator;
import raccoonman.reterraforged.extensions.RTFChunkGenerator;
import raccoonman.reterraforged.world.PointFinder;
import raccoonman.reterraforged.world.worldgen.layer.Layer;

@Mixin(ChunkGenerator.class)
public class MixinChunkGenerator implements RTFChunkGenerator {
	private PointFinder spawnFinder;
	private Layer.Factory<Float> maxHeightLayer;
	
	@Override
	public void setSpawnFinder(PointFinder spawnFinder) {
		this.spawnFinder = spawnFinder;
	}

	@Override
	public PointFinder getSpawnFinder() {
		return this.spawnFinder;
	}
	
	@Override
	public void setMaxHeightLayer(Layer.Factory<Float> layer) {
		this.maxHeightLayer = layer;
	}

	@Override
	public Layer.Factory<Float> getMaxHeightLayer() {
		return this.maxHeightLayer;
	}
}
