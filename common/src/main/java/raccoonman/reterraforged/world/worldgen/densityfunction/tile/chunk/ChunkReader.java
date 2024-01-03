package raccoonman.reterraforged.world.worldgen.densityfunction.tile.chunk;

import raccoonman.reterraforged.concurrent.Disposable;
import raccoonman.reterraforged.world.worldgen.cell.Cell;

public interface ChunkReader extends ChunkHolder, Disposable {
    Cell getCell(int x, int z);
}
