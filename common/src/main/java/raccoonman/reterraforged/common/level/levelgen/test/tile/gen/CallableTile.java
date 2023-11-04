package raccoonman.reterraforged.common.level.levelgen.test.tile.gen;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazyCallable;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;

public class CallableTile extends LazyCallable<Tile> {
    private int regionX;
    private int regionZ;
    private TileGenerator generator;
    
    public CallableTile(int regionX, int regionZ, TileGenerator generator) {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.generator = generator;
    }
    
    @Override
    protected Tile create() {
        return this.generator.generateRegion(this.regionX, this.regionZ);
    }
}
