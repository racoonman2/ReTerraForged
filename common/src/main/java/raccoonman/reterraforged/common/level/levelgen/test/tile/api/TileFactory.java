package raccoonman.reterraforged.common.level.levelgen.test.tile.api;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Disposable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;

public interface TileFactory {
    int chunkToRegion(int coord);
    
    void setListener(Disposable.Listener<Tile> listener);
    
    LazyCallable<Tile> getTile(int x, int z);
    
    LazyCallable<Tile> getTile(float centerX, float centerZ, float zoom, boolean filter);
    
    TileFactory async();
    
    TileProvider cached();
}
