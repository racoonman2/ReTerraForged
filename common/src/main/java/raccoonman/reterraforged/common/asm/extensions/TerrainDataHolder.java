package raccoonman.reterraforged.common.asm.extensions;

import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainData;

@Deprecated // i really hate this interface
public interface TerrainDataHolder {
	void setTerrainData(TerrainData terrainData);
	
	TerrainData getTerrainData();
}
