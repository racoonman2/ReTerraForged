package raccoonman.reterraforged.extensions;

import raccoonman.reterraforged.world.PointFinder;
import raccoonman.reterraforged.world.worldgen.layer.Layer;

public interface RTFChunkGenerator {
	void setSpawnFinder(PointFinder spawnFinder);

	PointFinder getSpawnFinder();

	void setMaxHeightLayer(Layer.Factory<Float> layer);
	
	Layer.Factory<Float> getMaxHeightLayer();
}
