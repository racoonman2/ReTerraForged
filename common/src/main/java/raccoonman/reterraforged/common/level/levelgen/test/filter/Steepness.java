package raccoonman.reterraforged.common.level.levelgen.test.filter;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.filter.Filter.Visitor;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;

public class Steepness implements Filter, Visitor {
	private int radius;
	private float scaler;
	private float waterLevel;

	public Steepness(int radius, float scaler, Levels levels) {
		this.radius = radius;
		this.scaler = scaler;
		this.waterLevel = levels.water;
	}

	@Override
	public void apply(Filterable cellMap, int seedX, int seedZ, int iterations) {
		this.iterate(cellMap, this);
	}

	@Override
	public void visit(Filterable cellMap, Cell cell, int cx, int cz) {
		float totalHeightDif = 0.0F;
		for (int dz = -1; dz <= 2; ++dz) {
			for (int dx = -1; dx <= 2; ++dx) {
				if (dx != 0 || dz != 0) {
					int x = cx + dx * this.radius;
					int z = cz + dz * this.radius;
					Cell neighbour = cellMap.getCellRaw(x, z);
					if (!neighbour.isAbsent()) {
						float height = Math.max(neighbour.value, this.waterLevel);
						totalHeightDif += Math.abs(cell.value - height) / this.radius;
					}
				}
			}
		}
		cell.gradient = Math.min(1.0F, totalHeightDif * this.scaler);
	}
}
