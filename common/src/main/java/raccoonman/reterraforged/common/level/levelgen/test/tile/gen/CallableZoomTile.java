package raccoonman.reterraforged.common.level.levelgen.test.tile.gen;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;

public class CallableZoomTile extends LazyCallable<Tile> {
    private float centerX;
    private float centerY;
    private float zoom;
    private boolean filters;
    private TileGenerator generator;
    
    public CallableZoomTile(float centerX, float centerY, float zoom, boolean filters, TileGenerator generator) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.zoom = zoom;
        this.filters = filters;
        this.generator = generator;
    }
    
    @Override
    protected Tile create() {
        return this.generator.generateRegion(this.centerX, this.centerY, this.zoom, this.filters);
    }
}
