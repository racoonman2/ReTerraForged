package raccoonman.reterraforged.common.level.levelgen.test.tile.gen;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Disposable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPool;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPools;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileFactory;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;
import raccoonman.reterraforged.common.level.levelgen.test.world.WorldGenerator;
import raccoonman.reterraforged.common.level.levelgen.test.world.WorldGeneratorFactory;

public class TileGenerator implements TileFactory {
    protected int factor;
    protected int border;
    protected int batchSize;
    protected ThreadPool threadPool;
    protected WorldGenerator generator;
    private TileResources resources;
    private Disposable.Listener<Tile> listener;
    
    protected TileGenerator(Builder builder) {
        this.resources = new TileResources();
        this.listener = (r -> {});
        this.factor = builder.factor;
        this.border = builder.border;
        this.batchSize = builder.batchSize;
        this.generator = builder.factory.get();
        this.threadPool = Builder.getOrDefaultPool(builder);
    }
    
    public WorldGenerator getGenerator() {
        return this.generator;
    }
    
    @Override
    public void setListener(Disposable.Listener<Tile> listener) {
        this.listener = listener;
    }
    
    @Override
    public int chunkToRegion(int i) {
        return i >> this.factor;
    }
    
    @Override
    public LazyCallable<Tile> getTile(int regionX, int regionZ) {
        return new CallableTile(regionX, regionZ, this);
    }
    
    @Override
    public LazyCallable<Tile> getTile(float centerX, float centerZ, float zoom, boolean filter) {
        return new CallableZoomTile(centerX, centerZ, zoom, filter, this);
    }
    
    @Override
    public TileFactory async() {
        return new TileGeneratorAsync(this, this.threadPool);
    }
    
    @Override
    public TileProvider cached() {
        return new TileCache(this, this.threadPool);
    }
    
    public Tile generateRegion(int regionX, int regionZ) {
        Tile tile = this.createEmptyRegion(regionX, regionZ);
        tile.generate(this.generator.heightmap());
        this.postProcess(tile);
        return tile;
    }
    
    public Tile generateRegion(float centerX, float centerZ, float zoom, boolean filter) {
        Tile tile = this.createEmptyRegion(0, 0);
        tile.generate(this.generator.heightmap(), centerX, centerZ, zoom);
        this.postProcess(tile, filter);
        return tile;
    }
    
    public Tile createEmptyRegion(int regionX, int regionZ) {
        return new Tile(regionX, regionZ, this.factor, this.border, this.resources, this.listener);
    }
    
    protected void postProcess(Tile tile) {
        this.generator.filters().apply(tile, true);
    }
    
    protected void postProcess(Tile tile, boolean filter) {
        this.generator.filters().apply(tile, filter);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        protected int factor;
        protected int border;
        protected int batchSize;
        protected boolean striped;
        protected WorldGeneratorFactory factory;
        private ThreadPool threadPool;
        
        public Builder() {
            this.factor = 0;
            this.border = 0;
            this.batchSize = 0;
            this.striped = false;
        }
        
        public Builder size(int factor, int border) {
            return this.factor(factor).border(border);
        }
        
        public Builder factor(int factor) {
            this.factor = factor;
            return this;
        }
        
        public Builder border(int border) {
            this.border = border;
            return this;
        }
        
        public Builder pool(ThreadPool threadPool) {
            this.threadPool = threadPool;
            return this;
        }
        
        public Builder factory(WorldGeneratorFactory factory) {
            this.factory = factory;
            return this;
        }
        
        public Builder batch(int batchSize) {
            this.batchSize = batchSize;
            return this;
        }
        
        public Builder striped() {
            this.striped = true;
            return this;
        }
        
        public TileGenerator build() {
            if (this.batchSize <= 0) {
                return new TileGenerator(this);
            }
            if (this.striped) {
                return new TileGeneratorStriped(this);
            }
            return new TileGeneratorBatched(this);
        }
        
        protected static ThreadPool getOrDefaultPool(Builder builder) {
            return (builder.threadPool == null) ? ThreadPools.NONE : builder.threadPool;
        }
    }
}
