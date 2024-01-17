package raccoonman.reterraforged.world.worldgen.tile.filter;

import raccoonman.reterraforged.world.worldgen.cell.Cell;

public interface Filter {
	void apply(Filterable map, int regionX, int regionZ, int iterationsPerChunk);

	default void iterate(Filterable map, Visitor visitor) {
		for (int dz = 0; dz < map.getBlockSize().total(); ++dz) {
			for (int dx = 0; dx < map.getBlockSize().total(); ++dx) {
				Cell cell = map.getCellRaw(dx, dz);
				visitor.visit(map, cell, dx, dz);
			}
		}
	}

	public interface Visitor {
		void visit(Filterable map, Cell cell, int dx, int dz);
	}
}
