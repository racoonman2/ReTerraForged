package raccoonman.reterraforged.common.level.levelgen.test.tile.gen;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Disposable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPool;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileFactory;

public class TileGeneratorAsync implements TileFactory {
    protected TileGenerator generator;
    protected ThreadPool threadPool;
    
    public TileGeneratorAsync(TileGenerator generator, ThreadPool threadPool) {
        this.generator = generator;
        this.threadPool = threadPool;
    }
    
    @Override
    public int chunkToRegion(int i) {
        return this.generator.chunkToRegion(i);
    }
    
    @Override
    public void setListener(Disposable.Listener<Tile> listener) {
        this.generator.setListener(listener);
    }
    
    @Override
    public LazyCallable<Tile> getTile(int regionX, int regionZ) {
        return LazyCallable.callAsync(this.generator.getTile(regionX, regionZ), this.threadPool);
    }
    
    @Override
    public LazyCallable<Tile> getTile(float centerX, float centerZ, float zoom, boolean filter) {
        return LazyCallable.callAsync(this.generator.getTile(centerX, centerZ, zoom, filter), this.threadPool);
    }
    
    @Override
    public TileFactory async() {
        return this;
    }
    
    @Override
    public TileCache cached() {
        return new TileCache(this.generator, this.threadPool);
    }
}
