package raccoonman.reterraforged.common.level.levelgen.test.tile.chunk;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.BatchTask;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.BatchTaskException;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Heightmap;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;

public class ChunkStripeBatchTask implements BatchTask {
    private static final String ERROR = "Failed to generate tile strip: x=%s, z=%s, length=%s";
    private int x;
    private int z;
    private int stripeSize;
    private Tile tile;
    private Heightmap heightmap;
    private Notifier notifier;
    
    public ChunkStripeBatchTask(int x, int z, int stripeSize, Tile tile, Heightmap heightmap) {
        this.notifier = BatchTask.NONE;
        this.x = x;
        this.z = z;
        this.stripeSize = stripeSize;
        this.tile = tile;
        this.heightmap = heightmap;
    }
    
    @Override
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier;
    }
    
    @Override
    public void run() {
        try {
            this.drive();
        } catch (Throwable t) {
            t.printStackTrace();
            throw new BatchTaskException(String.format(ERROR, this.x, this.z, this.stripeSize), t);
        } finally {
            this.notifier.markDone();
        }
    }
    
    private void drive() {
        for (int maxX = Math.min(this.tile.getChunkSize().total(), this.x + this.stripeSize), cx = this.x; cx < maxX; ++cx) {
            this.driveOne(this.tile.getChunkWriter(cx, this.z), this.heightmap);
        }
    }
    
    protected void driveOne(ChunkWriter chunk, Heightmap heightmap) {
        Rivermap rivers = null;
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                Cell cell = chunk.genCell(dx, dz);
                float x = chunk.getBlockX() + dx;
                float z = chunk.getBlockZ() + dz;
                heightmap.applyBase(cell, x, z);
                rivers = Rivermap.get(cell, rivers, heightmap);
                heightmap.applyRivers(cell, x, z, rivers);
                heightmap.applyClimate(cell, x, z);
            }
        }
    }
    
    public static class Zoom extends ChunkStripeBatchTask {
        private float translateX;
        private float translateZ;
        private float zoom;
        
        public Zoom(int x, int z, int size, Tile tile, Heightmap heightmap, float translateX, float translateZ, float zoom) {
            super(x, z, size, tile, heightmap);
            this.translateX = translateX;
            this.translateZ = translateZ;
            this.zoom = zoom;
        }
        
        @Override
        protected void driveOne(ChunkWriter chunk, Heightmap heightmap) {
            Rivermap rivers = null;
            for (int dz = 0; dz < 16; ++dz) {
                for (int dx = 0; dx < 16; ++dx) {
                    Cell cell = chunk.genCell(dx, dz);
                    float x = (chunk.getBlockX() + dx) * this.zoom + this.translateX;
                    float z = (chunk.getBlockZ() + dz) * this.zoom + this.translateZ;
                    heightmap.applyBase(cell, x, z);
                    rivers = Rivermap.get(cell, rivers, heightmap);
                    heightmap.applyRivers(cell, x, z, rivers);
                    heightmap.applyClimate(cell, x, z);
                }
            }
        }
    }
}
