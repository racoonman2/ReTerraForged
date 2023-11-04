package raccoonman.reterraforged.common.level.levelgen.test.tile.api;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Disposable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkReader;

public interface TileProvider extends Disposable.Listener<Tile> {
    int chunkToRegion(int coord);
    
    void queueChunk(int chunkX, int chunkZ);
    
    void queueRegion(int regionX, int regionZ);
    
    LazyCallable<Tile> get(long id);
    
    LazyCallable<Tile> getOrCompute(long id);
    
    default Tile getTile(long id) {
        return this.getOrCompute(id).get();
    }
    
    default Tile getTile(int regionX, int regionZ) {
        return this.getTile(Tile.getRegionId(regionX, regionZ));
    }
    
    default Tile getTileIfPresent(long id) {
        LazyCallable<Tile> entry = this.get(id);
        if (entry == null || !entry.isDone()) {
            return null;
        }
        return entry.get();
    }
    
    default Tile getTileIfPresent(int regionX, int regionZ) {
        return this.getTileIfPresent(Tile.getRegionId(regionX, regionZ));
    }
    
    default ChunkReader getChunk(int chunkX, int chunkZ) {
        int regionX = this.chunkToRegion(chunkX);
        int regionZ = this.chunkToRegion(chunkZ);
        return this.getTile(regionX, regionZ).getChunk(chunkX, chunkZ);
    }
}
