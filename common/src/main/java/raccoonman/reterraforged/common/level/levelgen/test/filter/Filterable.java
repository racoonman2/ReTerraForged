package raccoonman.reterraforged.common.level.levelgen.test.filter;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Size;

public interface Filterable {
    int getBlockX();
    
    int getBlockZ();
    
    Size getSize();
    
    Cell[] getBacking();
    
    Cell getCellRaw(int x, int z);
}
