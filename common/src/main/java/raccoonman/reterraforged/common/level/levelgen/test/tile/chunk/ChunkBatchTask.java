package raccoonman.reterraforged.common.level.levelgen.test.tile.chunk;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.BatchTask;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.BatchTaskException;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Heightmap;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;

public class ChunkBatchTask implements BatchTask {
    private static final String ERROR = "Failed to generate tile area: x=%s, z=%s, size=%sx%s";
    private int x;
    private int z;
    private int size;
    private Tile tile;
    private Heightmap heightmap;
    private Notifier notifier;
    
    public ChunkBatchTask(int x, int z, int size, Tile tile, Heightmap heightmap) {
        this.notifier = BatchTask.NONE;
        this.heightmap = heightmap;
        this.tile = tile;
        this.x = x;
        this.z = z;
        this.size = size;
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
            throw new BatchTaskException(String.format(ERROR, this.x, this.z, this.size, this.size), t);
        } finally {
            this.notifier.markDone();
        }
    }
    
    private void drive() {
        int maxX = Math.min(this.tile.getChunkSize().total(), this.x + this.size);
        for (int maxZ = Math.min(this.tile.getChunkSize().total(), this.z + this.size), cz = this.z; cz < maxZ; ++cz) {
            for (int cx = this.x; cx < maxX; ++cx) {
                this.driveOne(this.tile.getChunkWriter(cx, cz), this.heightmap);
            }
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
    
    public static class Zoom extends ChunkBatchTask {
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
