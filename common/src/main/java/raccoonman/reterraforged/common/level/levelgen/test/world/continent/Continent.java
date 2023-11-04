package raccoonman.reterraforged.common.level.levelgen.test.world.continent;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;

public interface Continent extends Populator {
    float getEdgeValue(float x, float z, int seed);
    
    default float getLandValue(float x, float z, int seed) {
        return this.getEdgeValue(x, z, seed);
    }
    
    long getNearestCenter(float x, float z, int seed);
    
    Rivermap getRivermap(int x, int z, int seed);
    
    default Rivermap getRivermap(Cell cell, int seed) {
        return this.getRivermap(cell.continentX, cell.continentZ, seed);
    }
}
