package raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Size;

public interface Filterable {
    int getBlockX();
    
    int getBlockZ();
    
    Size getBlockSize();
    
    Cell[] getBacking();
    
    Cell getCellRaw(int x, int z);
}
