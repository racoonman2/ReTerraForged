package raccoonman.reterraforged.world.worldgen.terrain.region;

import java.util.LinkedList;
import java.util.List;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public class RegionSelector implements CellPopulator {
	private int maxIndex;
	private CellPopulator[] nodes;

	public RegionSelector(List<CellPopulator> populators) {
		this.nodes = getWeightedArray(populators);
		this.maxIndex = this.nodes.length - 1;
	}

	@Override
	public void apply(Cell cell, float x, float y) {
		this.get(cell.terrainRegionId).apply(cell, x, y);
	}

	public CellPopulator get(float identity) {
		int index = NoiseUtil.round(identity * this.maxIndex);
		return this.nodes[index];
	}

	private static CellPopulator[] getWeightedArray(List<CellPopulator> modules) {
		float smallest = Float.MAX_VALUE;
		for (CellPopulator p : modules) {
			if (p instanceof Weighted tp) {
				if (tp.weight() == 0.0F) {
					continue;
				}
				smallest = Math.min(smallest, tp.weight());
			} else {
				smallest = Math.min(smallest, 1.0F);
			}
		}
		if (smallest == Float.MAX_VALUE) {
			return modules.toArray(CellPopulator[]::new);
		}
		List<CellPopulator> result = new LinkedList<>();
		for (CellPopulator p2 : modules) {
			int count;
			if (p2 instanceof Weighted tp2) {
				if (tp2.weight() == 0.0F) {
					continue;
				}
				count = Math.round(tp2.weight() / smallest);
			} else {
				count = Math.round(1.0F / smallest);
			}
			while (count-- > 0) {
				result.add(p2);
			}
		}
		if (result.isEmpty()) {
			return modules.toArray(CellPopulator[]::new);
		}
		return result.toArray(CellPopulator[]::new);
	}
	
	public interface Weighted {
		float weight();
	}
}
