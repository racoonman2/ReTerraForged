package raccoonman.reterraforged.world.worldgen.densityfunction.tile.chunk;

import raccoonman.reterraforged.world.worldgen.cell.Cell;

public interface ChunkWriter extends ChunkHolder {
    Cell genCell(int x, int z);
}
