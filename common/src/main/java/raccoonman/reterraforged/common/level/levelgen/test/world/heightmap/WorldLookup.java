package raccoonman.reterraforged.common.level.levelgen.test.world.heightmap;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;
import raccoonman.reterraforged.common.level.levelgen.test.tile.chunk.ChunkReader;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;

public class WorldLookup {
	private float waterLevel;
	private float beachLevel;
	private TileProvider cache;
	private Heightmap heightmap;

	public WorldLookup(GeneratorContext context) {
		this.cache = context.cache.get();
		this.heightmap = context.worldGenerator.get().getHeightmap();
		this.waterLevel = context.levels.water;
		this.beachLevel = context.levels.water(5);
	}

	public Resource<Cell> get(int x, int z) {
		ChunkReader chunk = this.cache.getChunk(x >> 4, z >> 4);
		Resource<Cell> cell = Cell.getResource();
		cell.get().copyFrom(chunk.getCell(x & 0xF, z & 0xF));
		return cell;
	}

	public Resource<Cell> getCell(int x, int z) {
		return this.getCell(x, z, false);
	}

	public Resource<Cell> getCell(int x, int z, boolean load) {
		Resource<Cell> resource = Cell.getResource();
		this.applyCell(resource.get(), x, z, load);
		return resource;
	}

	public void applyCell(Cell cell, int x, int z) {
		this.applyCell(cell, x, z, false);
	}

	public void applyCell(Cell cell, int x, int z, boolean load) {
		if (load && this.computeAccurate(cell, x, z)) {
			return;
		}
		if (this.computeCached(cell, x, z)) {
			return;
		}
		this.compute(cell, x, z);
	}

	private boolean computeAccurate(Cell cell, int x, int z) {
		int rx = this.cache.chunkToRegion(x >> 4);
		int rz = this.cache.chunkToRegion(z >> 4);
		Tile tile = this.cache.getTile(rx, rz);
		Cell c = tile.getCell(x, z);
		if (c != null) {
			cell.copyFrom(c);
		}
		return cell.terrain != null;
	}

	private boolean computeCached(Cell cell, int x, int z) {
		int rx = this.cache.chunkToRegion(x >> 4);
		int rz = this.cache.chunkToRegion(z >> 4);
		Tile tile = this.cache.getTileIfPresent(rx, rz);
		if (tile != null) {
			Cell c = tile.getCell(x, z);
			if (c != null) {
				cell.copyFrom(c);
			}
			return cell.terrain != null;
		}
		return false;
	}

	private void compute(Cell cell, int x, int z) {
		this.heightmap.apply(cell, x, z, 0);
		if (cell.terrain == TerrainType.COAST && cell.value > this.waterLevel && cell.value <= this.beachLevel) {
			cell.terrain = TerrainType.BEACH;
		}
	}
}
