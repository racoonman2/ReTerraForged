package raccoonman.reterraforged.world.worldgen.cell.heightmap;

import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.TileCache;

public class WorldLookup {
	private float waterLevel;
	private float beachLevel;
	private TileCache cache;
	private Heightmap heightmap;

	public WorldLookup(GeneratorContext context) {
		this.cache = context.cache;
		this.heightmap = context.generator.getHeightmap();
		this.waterLevel = context.levels.water;
		this.beachLevel = context.levels.water(5);
	}
	
	public Heightmap getHeightmap() {
		return this.heightmap;
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
		int rx = this.cache.chunkToTile(x >> 4);
		int rz = this.cache.chunkToTile(z >> 4);
		Tile tile = this.cache.provide(rx, rz);
		Cell c = tile.getCell(x, z);
		if (c != null) {
			cell.copyFrom(c);
		}
		return cell.terrain != null;
	}

	private boolean computeCached(Cell cell, int x, int z) {
		int rx = this.cache.chunkToTile(x >> 4);
		int rz = this.cache.chunkToTile(z >> 4);
		Tile tile = this.cache.provideIfPresent(rx, rz);
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
		this.heightmap.apply(cell, x, z, this.heightmap.continent().getRivermap(cell));
		if (cell.terrain == TerrainType.COAST && cell.height > this.waterLevel && cell.height <= this.beachLevel) {
			cell.terrain = TerrainType.BEACH;
		}
	}
}
