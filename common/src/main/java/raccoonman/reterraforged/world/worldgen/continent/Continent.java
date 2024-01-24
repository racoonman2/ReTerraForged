package raccoonman.reterraforged.world.worldgen.continent;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.rivermap.Rivermap;

public interface Continent extends CellPopulator {
    float getEdgeValue(float x, float z);
    
    default float getLandValue(float x, float z) {
        return this.getEdgeValue(x, z);
    }
    
    long getNearestCenter(float x, float z);
    
    Rivermap getRivermap(int x, int z);
    
    default Rivermap getRivermap(Cell cell) {
        return this.getRivermap(cell.continentX, cell.continentZ);
    }
}
