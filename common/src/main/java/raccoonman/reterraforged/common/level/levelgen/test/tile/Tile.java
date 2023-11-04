package raccoonman.reterraforged.common.level.levelgen.test.tile;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Disposable;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.Batcher;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.cache.SafeCloseable;
import raccoonman.reterraforged.common.level.levelgen.test.filter.Filterable;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkBatchTask;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkGenTask;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkReader;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkStripeBatchTask;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkWriter;
import raccoonman.reterraforged.common.level.levelgen.test.tile.gen.TileResources;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Heightmap;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.HeightmapCache;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;

public class Tile implements Disposable, SafeCloseable {
    protected int regionX;
    protected int regionZ;
    protected int chunkX;
    protected int chunkZ;
    protected int blockX;
    protected int blockZ;
    protected int border;
    protected int chunkCount;
    protected int size;
    protected Size blockSize;
    protected Size chunkSize;
    protected Cell[] blocks;
    protected GenChunk[] chunks;
    protected Resource<Cell[]> blockResource;
    protected Resource<GenChunk[]> chunkResource;
    protected AtomicInteger active;
    protected AtomicInteger disposed;
    protected Listener<Tile> listener;
    
    public Tile(int regionX, int regionZ, int size, int borderChunks, TileResources resources, Listener<Tile> listener) {
        this.active = new AtomicInteger();
        this.disposed = new AtomicInteger();
        this.size = size;
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.listener = listener;
        this.chunkX = regionX << size;
        this.chunkZ = regionZ << size;
        this.blockX = Size.chunkToBlock(this.chunkX);
        this.blockZ = Size.chunkToBlock(this.chunkZ);
        this.border = borderChunks;
        this.chunkSize = Size.chunks(size, borderChunks);
        this.blockSize = Size.blocks(size, borderChunks);
        this.chunkCount = this.chunkSize.size() * this.chunkSize.size();
        this.blockResource = resources.blocks.get(this.blockSize.arraySize());
        this.chunkResource = resources.chunks.get(this.chunkSize.arraySize());
        this.blocks = this.blockResource.get();
        this.chunks = this.chunkResource.get();
    }
    
    public int getGenerationSize() {
        return this.size;
    }
    
    @Override
    public void dispose() {
        if (this.disposed.incrementAndGet() >= this.chunkCount) {
            this.listener.onDispose(this);
        }
    }
    
    @Override
    public void close() {
        if (this.active.compareAndSet(0, -1)) {
            if (this.blockResource.isOpen()) {
                for (Cell cell : this.blocks) {
                    if (cell != null) {
                        cell.reset();
                    }
                }
                this.blockResource.close();
            }
            if (this.chunkResource.isOpen()) {
                Arrays.fill(this.chunks, null);
                this.chunkResource.close();
            }
        }
    }
    
    public long getRegionId() {
        return getRegionId(this.getRegionX(), this.getRegionZ());
    }
    
    public int getRegionX() {
        return this.regionX;
    }
    
    public int getRegionZ() {
        return this.regionZ;
    }
    
    public int getBlockX() {
        return this.blockX;
    }
    
    public int getBlockZ() {
        return this.blockZ;
    }
    
    public int getOffsetChunks() {
        return this.border;
    }
    
    public int getChunkCount() {
        return this.chunks.length;
    }
    
    public int getBlockCount() {
        return this.blocks.length;
    }
    
    public Size getChunkSize() {
        return this.chunkSize;
    }
    
    public Size getBlockSize() {
        return this.blockSize;
    }
    
    public Filterable filterable() {
        return new FilterRegion();
    }
    
    public Cell getCell(int blockX, int blockZ) {
        int relBlockX = this.blockSize.border() + this.blockSize.mask(blockX);
        int relBlockZ = this.blockSize.border() + this.blockSize.mask(blockZ);
        int index = this.blockSize.indexOf(relBlockX, relBlockZ);
        return this.blocks[index];
    }
    
    public Cell getRawCell(int blockX, int blockZ) {
        int index = this.blockSize.indexOf(blockX, blockZ);
        return this.blocks[index];
    }
    
    public ChunkWriter getChunkWriter(int chunkX, int chunkZ) {
        int index = this.chunkSize.indexOf(chunkX, chunkZ);
        return this.computeChunk(index, chunkX, chunkZ);
    }
    
    public ChunkReader getChunk(int chunkX, int chunkZ) {
        int relChunkX = this.chunkSize.border() + this.chunkSize.mask(chunkX);
        int relChunkZ = this.chunkSize.border() + this.chunkSize.mask(chunkZ);
        int index = this.chunkSize.indexOf(relChunkX, relChunkZ);
        return this.chunks[index].open();
    }
    
    public void generate(Heightmap heightmap) {
        Rivermap riverMap = null;
        for (int cz = 0; cz < this.chunkSize.total(); ++cz) {
            for (int cx = 0; cx < this.chunkSize.total(); ++cx) {
                int index = this.chunkSize.indexOf(cx, cz);
                GenChunk chunk = this.computeChunk(index, cx, cz);
                for (int dz = 0; dz < 16; ++dz) {
                    for (int dx = 0; dx < 16; ++dx) {
                        float x = (float)(chunk.getBlockX() + dx);
                        float z = (float)(chunk.getBlockZ() + dz);
                        Cell cell = chunk.genCell(dx, dz);
                        heightmap.applyBase(cell, x, z);
                        riverMap = Rivermap.get(cell, riverMap, heightmap);
                        heightmap.applyRivers(cell, x, z, riverMap);
                        heightmap.applyClimate(cell, x, z);
                    }
                }
            }
        }
    }
    
    public void generate(Heightmap heightmap, float offsetX, float offsetZ, float zoom) {
        Rivermap riverMap = null;
        float translateX = offsetX - this.blockSize.size() * zoom / 2.0f;
        float translateZ = offsetZ - this.blockSize.size() * zoom / 2.0f;
        for (int cz = 0; cz < this.chunkSize.total(); ++cz) {
            for (int cx = 0; cx < this.chunkSize.total(); ++cx) {
                int index = this.chunkSize.indexOf(cx, cz);
                GenChunk chunk = this.computeChunk(index, cx, cz);
                for (int dz = 0; dz < 16; ++dz) {
                    for (int dx = 0; dx < 16; ++dx) {
                        float x = (chunk.getBlockX() + dx) * zoom + translateX;
                        float z = (chunk.getBlockZ() + dz) * zoom + translateZ;
                        Cell cell = chunk.genCell(dx, dz);
                        heightmap.applyBase(cell, x, z);
                        riverMap = Rivermap.get(cell, riverMap, heightmap);
                        heightmap.applyRivers(cell, x, z, riverMap);
                        heightmap.applyClimate(cell, x, z);
                    }
                }
            }
        }
    }
    
    public void generateArea(Heightmap heightmap, Batcher batcher, int batchCount) {
        batcher.size(batchCount * batchCount);
        int batchSize = getBatchSize(batchCount, this.chunkSize);
        for (int dz = 0; dz < batchCount; ++dz) {
            int cz = dz * batchSize;
            for (int dx = 0; dx < batchCount; ++dx) {
                int cx = dx * batchSize;
                batcher.submit(new ChunkBatchTask(cx, cz, batchSize, this, heightmap));
            }
        }
    }
    
    public void generateArea(Heightmap heightmap, Batcher batcher, int batchCount, float offsetX, float offsetZ, float zoom) {
        batcher.size(batchCount * batchCount);
        int batchSize = getBatchSize(batchCount, this.chunkSize);
        float translateX = offsetX - this.blockSize.size() * zoom / 2.0f;
        float translateZ = offsetZ - this.blockSize.size() * zoom / 2.0f;
        for (int dz = 0; dz < batchCount; ++dz) {
            int cz = dz * batchSize;
            for (int dx = 0; dx < batchCount; ++dx) {
                int cx = dx * batchSize;
                batcher.submit(new ChunkBatchTask.Zoom(cx, cz, batchSize, this, heightmap, translateX, translateZ, zoom));
            }
        }
    }
    
    public void generateAreaStriped(Heightmap heightmap, Batcher batcher, int sections) {
        batcher.size(this.chunkSize.total() * sections);
        int sectionLength = getBatchSize(sections, this.chunkSize);
        for (int cz = 0; cz < this.chunkSize.total(); ++cz) {
            for (int s = 0; s < sections; ++s) {
                int cx = s * sectionLength;
                batcher.submit(new ChunkStripeBatchTask(cx, cz, sectionLength, this, heightmap));
            }
        }
    }
    
    public void generateAreaStriped(Heightmap heightmap, Batcher batcher, int sections, float offsetX, float offsetZ, float zoom) {
        batcher.size(this.chunkSize.total() * sections);
        int sectionLength = getBatchSize(sections, this.chunkSize);
        float translateX = offsetX - this.blockSize.size() * zoom / 2.0f;
        float translateZ = offsetZ - this.blockSize.size() * zoom / 2.0f;
        for (int cz = 0; cz < this.chunkSize.total(); ++cz) {
            for (int s = 0; s < sections; ++s) {
                int cx = s * sectionLength;
                batcher.submit(new ChunkStripeBatchTask.Zoom(cx, cz, sectionLength, this, heightmap, translateX, translateZ, zoom));
            }
        }
    }
    
    protected GenChunk computeChunk(int index, int chunkX, int chunkZ) {
        GenChunk chunk = this.chunks[index];
        if (chunk == null) {
            chunk = new GenChunk(chunkX, chunkZ);
            this.chunks[index] = chunk;
        }
        return chunk;
    }
    
    protected Cell computeCell(int index) {
        Cell cell = this.blocks[index];
        if (cell == null) {
            cell = new Cell();
            this.blocks[index] = cell;
        }
        return cell;
    }
    
    protected static int getBatchSize(int batchCount, Size chunkSize) {
        int batchSize = chunkSize.total() / batchCount;
        if (batchSize * batchCount < chunkSize.total()) {
            ++batchSize;
        }
        return batchSize;
    }
    
    public static long getRegionId(int regionX, int regionZ) {
        return NoiseUtil.pack(regionX, regionZ);
    }
    
    public static int getRegionX(long id) {
        return NoiseUtil.unpackLeft(id);
    }
    
    public static int getRegionZ(long id) {
        return NoiseUtil.unpackRight(id);
    }
    
    public class GenChunk implements ChunkReader, ChunkWriter {
        private int chunkX;
        private int chunkZ;
        private int blockX;
        private int blockZ;
        private int regionBlockX;
        private int regionBlockZ;
        
        protected GenChunk(int regionChunkX, int regionChunkZ) {
            this.regionBlockX = regionChunkX << 4;
            this.regionBlockZ = regionChunkZ << 4;
            this.chunkX = Tile.this.chunkX + regionChunkX - Tile.this.getOffsetChunks();
            this.chunkZ = Tile.this.chunkZ + regionChunkZ - Tile.this.getOffsetChunks();
            this.blockX = this.chunkX << 4;
            this.blockZ = this.chunkZ << 4;
        }
        
        public GenChunk open() {
            Tile.this.active.getAndIncrement();
            return this;
        }
        
        @Override
        public void close() {
            Tile.this.active.decrementAndGet();
        }
        
        @Override
        public void dispose() {
            Tile.this.dispose();
        }
        
        @Override
        public int getChunkX() {
            return this.chunkX;
        }
        
        @Override
        public int getChunkZ() {
            return this.chunkZ;
        }
        
        @Override
        public int getBlockX() {
            return this.blockX;
        }
        
        @Override
        public int getBlockZ() {
            return this.blockZ;
        }
        
        @Override
        public Cell getCell(int blockX, int blockZ) {
            int relX = this.regionBlockX + (blockX & 0xF);
            int relZ = this.regionBlockZ + (blockZ & 0xF);
            int index = Tile.this.blockSize.indexOf(relX, relZ);
            return Tile.this.blocks[index];
        }
        
        @Override
        public Cell genCell(int blockX, int blockZ) {
            int relX = this.regionBlockX + (blockX & 0xF);
            int relZ = this.regionBlockZ + (blockZ & 0xF);
            int index = Tile.this.blockSize.indexOf(relX, relZ);
            return Tile.this.computeCell(index);
        }
    }
    
    protected class FilterRegion implements Filterable {
        @Override
        public int getBlockX() {
            return Tile.this.blockX;
        }
        
        @Override
        public int getBlockZ() {
            return Tile.this.blockZ;
        }
        
        @Override
        public Size getSize() {
            return Tile.this.blockSize;
        }
        
        @Override
        public Cell[] getBacking() {
            return Tile.this.blocks;
        }
        
        @Override
        public Cell getCellRaw(int x, int z) {
            int index = Tile.this.blockSize.indexOf(x, z);
            if (index < 0 || index >= Tile.this.blockSize.arraySize()) {
                return Cell.empty();
            }
            return Tile.this.blocks[index];
        }
    }
}
