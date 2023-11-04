package raccoonman.reterraforged.common.level.levelgen.test.tile.gen;

import raccoonman.reterraforged.common.level.levelgen.test.concurrent.Resource;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.batch.Batcher;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;

public class TileGeneratorStriped extends TileGenerator {
	
	public TileGeneratorStriped(Builder builder) {
		super(builder);
	}

	@Override
	public Tile generateRegion(int regionX, int regionZ) {
		Tile tile = this.createEmptyRegion(regionX, regionZ);
		try (Resource<Batcher> batcher = this.threadPool.batcher()) {
			tile.generateAreaStriped(this.generator.heightmap(), batcher.get(), this.batchSize);
		}
		this.postProcess(tile);
		return tile;
	}

	@Override
	public Tile generateRegion(float centerX, float centerZ, float zoom, boolean filter) {
		Tile tile = this.createEmptyRegion(0, 0);
		try (Resource<Batcher> batcher = this.threadPool.batcher()) {
			tile.generateAreaStriped(this.generator.heightmap(), batcher.get(), this.batchSize, centerX, centerZ, zoom);
		}
		this.postProcess(tile, filter);
		return tile;
	}
}
