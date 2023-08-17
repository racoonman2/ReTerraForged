package raccoonman.reterraforged.common.asm.extensions;

import raccoonman.reterraforged.common.level.levelgen.terrain.Terrain;

public interface TerrainHolder {
	Terrain getTerrain();
	
	void setTerrain(Terrain terrain);
}
