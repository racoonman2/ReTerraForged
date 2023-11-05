package raccoonman.reterraforged.common.level.levelgen.test.tile.chunk;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.BatchTask;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Heightmap;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;

public class ChunkGenTask implements BatchTask {
    private ChunkWriter chunk;
    private Heightmap heightmap;
    private Notifier notifier;
    
    public ChunkGenTask(ChunkWriter chunk, Heightmap heightmap) {
        this.notifier = BatchTask.NONE;
        this.chunk = chunk;
        this.heightmap = heightmap;
    }
    
    @Override
    public void setNotifier(Notifier notifier) {
        this.notifier = notifier;
    }
    
    @Override
    public void run() {
        try {
            this.driveOne(this.chunk, this.heightmap);
        } finally {
            this.notifier.markDone();
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
    
    public static class Zoom extends ChunkGenTask {
        private float translateX;
        private float translateZ;
        private float zoom;
        
        public Zoom(ChunkWriter chunk, Heightmap heightmap, float translateX, float translateZ, float zoom) {
            super(chunk, heightmap);
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
