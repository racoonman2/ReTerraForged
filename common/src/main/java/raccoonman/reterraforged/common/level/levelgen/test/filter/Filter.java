package raccoonman.reterraforged.common.level.levelgen.test.filter;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;

public interface Filter {
	void apply(Filterable map, int regionX, int regionZ, int iterationsPerChunk);

	default void iterate(Filterable map, Visitor visitor) {
		for (int dz = 0; dz < map.getSize().total(); ++dz) {
			for (int dx = 0; dx < map.getSize().total(); ++dx) {
				Cell cell = map.getCellRaw(dx, dz);
				visitor.visit(map, cell, dx, dz);
			}
		}
	}

	public interface Visitor {
		void visit(Filterable map, Cell cell, int dx, int dz);
	}
}
