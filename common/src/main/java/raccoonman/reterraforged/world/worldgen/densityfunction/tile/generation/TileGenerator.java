package raccoonman.reterraforged.world.worldgen.densityfunction.tile.generation;

import java.util.concurrent.CompletableFuture;

import raccoonman.reterraforged.concurrent.ThreadPools;
import raccoonman.reterraforged.concurrent.pool.ArrayPool;
import raccoonman.reterraforged.world.worldgen.WorldFilters;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.Heightmap;
import raccoonman.reterraforged.world.worldgen.cell.rivermap.Rivermap;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Size;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile.Chunk;

public class TileGenerator {
	private Heightmap heightmap;
	private WorldFilters filters;
	private ArrayPool<Cell> cellPool;
	private ArrayPool<Chunk> chunkPool;
	private int tileChunks;
	private int tileBorder;
	private Size tileSizeBlocks;
	private Size tileSizeChunks;
	private int batchSize;
	private int batchCount;
	
	public TileGenerator(Heightmap heightmap, WorldFilters filters, int tileChunks, int tileBorder, int batchCount) {
		this.heightmap = heightmap;
		this.filters = filters;
		this.cellPool = ArrayPool.of(100, (length) -> {
			Cell[] cells = new Cell[length];
			for(int i = 0; i < cells.length; i++) {
				cells[i] = new Cell();
			}
			return cells;
		});
		this.chunkPool = ArrayPool.of(100, Chunk[]::new);
		this.tileChunks = tileChunks;
		this.tileBorder = tileBorder;
		this.tileSizeBlocks = Size.blocks(tileChunks, tileBorder);
		this.tileSizeChunks = Size.chunks(tileChunks, tileBorder);
		this.batchSize = getBatchSize(batchCount, this.tileSizeChunks);
		this.batchCount = batchCount;
	}
	
	public Heightmap getHeightmap() {
		return this.heightmap;
	}
	
	public CompletableFuture<Tile> generate(int tileX, int tileZ) {
		Tile tile = this.makeTile(tileX, tileZ);
		CompletableFuture<?>[] futures = new CompletableFuture<?>[this.batchCount * this.batchCount];
		for (int batchX = 0; batchX < this.batchCount; batchX++) {
			for (int batchZ = 0; batchZ < this.batchCount; batchZ++) {
				int chunkX = batchX * this.batchSize;
				int chunkZ = batchZ * this.batchSize;
				futures[batchX * this.batchCount + batchZ] = CompletableFuture.runAsync(() -> {
			        int maxX = Math.min(this.tileSizeChunks.total(), chunkX + this.batchSize);
			        int maxZ = Math.min(this.tileSizeChunks.total(), chunkZ + this.batchSize);
			        for (int cX = chunkX; cX < maxX; cX++) {
			            for (int cZ = chunkZ; cZ < maxZ; cZ++) {
			            	Chunk chunk = tile.getChunkWriter(cX, cZ);
			            	
			                Rivermap rivers = null;
		                    for (int dx = 0; dx < 16; dx++) {
		                    	for (int dz = 0; dz < 16; dz++) {
		                    		int worldX = chunk.getBlockX() + dx;
		                    		int worldZ = chunk.getBlockZ() + dz;
		                    		Cell cell = chunk.getCell(dx, dz);
		                    		
			                        this.heightmap.applyTerrain(cell, worldX, worldZ);
			                        rivers = Rivermap.get(cell, rivers, this.heightmap);
			                        this.heightmap.applyRivers(cell, worldX, worldZ, rivers);
			                        this.heightmap.applyClimate(cell, worldX, worldZ);
			                    }
			                }
			            }
			        }
				}, ThreadPools.WORLD_GEN);
	        }
	    }
		return CompletableFuture.allOf(futures).thenApply((v) -> {
			this.filters.apply(tile, true);
			return tile;
		});
	}
	
	public CompletableFuture<Tile> generateZoomed(float centerX, float centerZ, float zoom, boolean applyOptionalFilters) {
		Tile tile = this.makeTile(0, 0);
		CompletableFuture<?>[] futures = new CompletableFuture<?>[this.batchCount * this.batchCount];
        float translateX = centerX - this.tileSizeBlocks.size() * zoom / 2.0F;
        float translateZ = centerZ - this.tileSizeBlocks.size() * zoom / 2.0F;
		for (int batchX = 0; batchX < this.batchCount; batchX++) {
			for (int batchZ = 0; batchZ < this.batchCount; batchZ++) {
				int chunkX = batchX * this.batchSize;
				int chunkZ = batchZ * this.batchSize;
				futures[batchX * this.batchCount + batchZ] = CompletableFuture.runAsync(() -> {
			        int maxX = Math.min(this.tileSizeChunks.total(), chunkX + this.batchSize);
			        int maxZ = Math.min(this.tileSizeChunks.total(), chunkZ + this.batchSize);
			        for (int cX = chunkX; cX < maxX; cX++) {
			            for (int cZ = chunkZ; cZ < maxZ; cZ++) {
			            	Chunk chunk = tile.getChunkWriter(cX, cZ);
			            	
			                Rivermap rivers = null;
		                    for (int dx = 0; dx < 16; dx++) {
		                    	for (int dz = 0; dz < 16; dz++) {
		                    		float worldX = (chunk.getBlockX() + dx) * zoom + translateX;
		                    		float worldZ = (chunk.getBlockZ() + dz) * zoom + translateZ;
		                    		Cell cell = chunk.getCell(dx, dz);
		                    		
			                        this.heightmap.applyTerrain(cell, worldX, worldZ);
			                        rivers = Rivermap.get(cell, rivers, this.heightmap);
			                        this.heightmap.applyRivers(cell, worldX, worldZ, rivers);
			                        this.heightmap.applyClimate(cell, worldX, worldZ);
			                    }
			                }
			            }
			        }
				}, ThreadPools.WORLD_GEN);
	        }
	    }
		return CompletableFuture.allOf(futures).thenApply((v) -> {
			this.filters.apply(tile, applyOptionalFilters);
			return tile;
		});
	}
    
	private Tile makeTile(int x, int z) {
		return new Tile(x, z, this.tileChunks, this.tileBorder, this.tileSizeBlocks, this.tileSizeChunks, this.cellPool.get(this.tileSizeBlocks.arraySize()), this.chunkPool.get(this.tileSizeChunks.arraySize()));
	}
	
    private static int getBatchSize(int batchCount, Size chunkSize) {
        int batchSize = chunkSize.total() / batchCount;
        if (batchSize * batchCount < chunkSize.total()) {
            ++batchSize;
        }
        return batchSize;
    }
}
