package raccoonman.reterraforged.extensions;

import raccoonman.reterraforged.world.worldgen.LayerData;

public interface RTFGenerationChunkHolder {
	LayerData getLayerData();
	
	void setLayerData(LayerData layerData);
}
