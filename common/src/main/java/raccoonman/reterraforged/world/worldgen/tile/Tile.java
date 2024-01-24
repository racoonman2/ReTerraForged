package raccoonman.reterraforged.world.worldgen.tile;

import java.util.Arrays;

import raccoonman.reterraforged.concurrent.Resource;
import raccoonman.reterraforged.concurrent.cache.SafeCloseable;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellLookup;
import raccoonman.reterraforged.world.worldgen.tile.filter.Filterable;

public class Tile implements SafeCloseable, Filterable, CellLookup {
	private int x, z;
	private int chunkX, chunkZ;
	private int size;
	private int border;
	private Size blockSize;
	private Size chunkSize;
	private Resource<Cell[]> cacheResource;
	private Resource<Chunk[]> chunkResource;
	private Cell[] cache;
	private Chunk[] chunks;
	
	public Tile(int x, int z, int size, int border, Size blockSize, Size chunkSize, Resource<Cell[]> cacheResource, Resource<Chunk[]> chunkResource) {
		this.x = x;
		this.z = z;
        this.chunkX = x << size;
        this.chunkZ = z << size;
        this.size = size;
        this.border = border;
		this.blockSize = blockSize;
		this.chunkSize = chunkSize;
		this.cacheResource = cacheResource;
		this.chunkResource = chunkResource;
		this.cache = cacheResource.get();
		this.chunks = chunkResource.get();
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getZ() {
		return this.z;
	}
	
	@Override
	public Cell lookup(int blockX, int blockZ) {
		int border = this.blockSize.border();
        int relBlockX = border + this.blockSize.mask(blockX);
        int relBlockZ = border + this.blockSize.mask(blockZ);
        int index = this.blockSize.indexOf(relBlockX, relBlockZ);
        return this.cache[index];
	}
	
	public Chunk getChunkWriter(int chunkX, int chunkZ) {
        int index = this.chunkSize.indexOf(chunkX, chunkZ);
        return this.computeChunk(index, chunkX, chunkZ);
	}
	
	public Chunk getChunkReader(int chunkX, int chunkZ) {
        int relChunkX = this.chunkSize.border() + this.chunkSize.mask(chunkX);
        int relChunkZ = this.chunkSize.border() + this.chunkSize.mask(chunkZ);
        int index = this.chunkSize.indexOf(relChunkX, relChunkZ);
        return this.computeChunk(index, chunkX, chunkZ);
	}
	
    public void iterate(Cell.Visitor visitor) {
        for (int dz = 0; dz < this.blockSize.size(); ++dz) {
            int z = this.blockSize.border() + dz;
            for (int dx = 0; dx < this.blockSize.size(); ++dx) {
                int x = this.blockSize.border() + dx;
                int index = this.blockSize.indexOf(x, z);
                Cell cell = this.cache[index];
                visitor.visit(cell, dx, dz);
            }
        }
    }

	@Override
	public int getBlockX() {
		return Size.chunkToBlock(this.chunkX);
	}

	@Override
	public int getBlockZ() {
		return Size.chunkToBlock(this.chunkZ);
	}

	@Override
	public Size getBlockSize() {
		return this.blockSize;
	}

	public Size getChunkSize() {
		return this.chunkSize;
	}

	@Override
	public Cell[] getBacking() {
		return this.cache;
	}

	@Override
	public Cell getCellRaw(int x, int z) {
		int index = Tile.this.blockSize.indexOf(x, z);
        if (index < 0 || index >= Tile.this.blockSize.arraySize()) {
            return Cell.empty();
        }
        return Tile.this.cache[index];
	}

	@Override
	public void close() {
        for (Cell cell : this.cache) {
        	cell.reset();
        }
        Arrays.fill(this.chunks, null);
		this.cacheResource.close();
		this.chunkResource.close();
	}
	
    private Chunk computeChunk(int index, int chunkX, int chunkZ) {
        Chunk chunk = this.chunks[index];
        if (chunk == null) {
            chunk = new Chunk(chunkX, chunkZ);
            this.chunks[index] = chunk;
        }
        return chunk;
    }
	
	public class Chunk {
        private int chunkX;
        private int chunkZ;
        private int blockX;
        private int blockZ;
        private int regionBlockX;
        private int regionBlockZ;
		
        private float generationHeight;
        
		public Chunk(int regionChunkX, int regionChunkZ) {
            this.regionBlockX = regionChunkX << 4;
            this.regionBlockZ = regionChunkZ << 4;
            this.chunkX = Tile.this.chunkX + regionChunkX - Tile.this.border;
            this.chunkZ = Tile.this.chunkZ + regionChunkZ - Tile.this.border;
            this.blockX = this.chunkX << 4;
            this.blockZ = this.chunkZ << 4;
            
            this.generationHeight = Float.MIN_VALUE;
		}
		
		protected void updateGenerationHeight(Cell cell) {
            if(this.generationHeight < cell.height) {
            	this.generationHeight = cell.height;
            }
		}
		
		public float getGenerationHeight() {
			return this.generationHeight;
		}
		
        public int getChunkX() {
            return this.chunkX;
        }
        
        public int getChunkZ() {
            return this.chunkZ;
        }
        
        public int getBlockX() {
            return this.blockX;
        }
        
        public int getBlockZ() {
            return this.blockZ;
        }
        
        public Cell getCell(int blockX, int blockZ) {
            int relX = this.regionBlockX + (blockX & 0xF);
            int relZ = this.regionBlockZ + (blockZ & 0xF);
            int index = Tile.this.blockSize.indexOf(relX, relZ);
            return Tile.this.cache[index];
        }
        
        public static int clampToNearestCell(int height, int cellHeight) {
        	return (height / cellHeight + 1) * cellHeight;
        }
	}
}
