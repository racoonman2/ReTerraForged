package raccoonman.reterraforged.common.level.levelgen.test.tile.gen;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.pool.ArrayPool;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;

public class TileResources {
    public ArrayPool<Cell> blocks;
    public ArrayPool<Tile.GenChunk> chunks;
    
    public TileResources() {
        this.blocks = ArrayPool.of(100, Cell[]::new);
        this.chunks = ArrayPool.of(100, Tile.GenChunk[]::new);
    }
}
