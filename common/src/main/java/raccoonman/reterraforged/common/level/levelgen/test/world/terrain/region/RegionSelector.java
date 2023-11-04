package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.region;

import java.util.LinkedList;
import java.util.List;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator.TerrainPopulator;

public class RegionSelector implements Populator {
	private int maxIndex;
	private Populator[] nodes;

	public RegionSelector(List<Populator> populators) {
		this.nodes = getWeightedArray(populators);
		this.maxIndex = this.nodes.length - 1;
	}

	@Override
	public void apply(Cell cell, float x, float y, int seed) {
		this.get(cell.terrainRegionId).apply(cell, x, y, seed);
	}

	public Populator get(float identity) {
		int index = NoiseUtil.round(identity * this.maxIndex);
		return this.nodes[index];
	}

	private static Populator[] getWeightedArray(List<Populator> modules) {
		float smallest = Float.MAX_VALUE;
		for (Populator p : modules) {
			if (p instanceof TerrainPopulator tp) {
				if (tp.getWeight() == 0.0F) {
					continue;
				}
				smallest = Math.min(smallest, tp.getWeight());
			} else {
				smallest = Math.min(smallest, 1.0F);
			}
		}
		if (smallest == Float.MAX_VALUE) {
			return modules.toArray(new Populator[0]);
		}
		List<Populator> result = new LinkedList<>();
		for (Populator p2 : modules) {
			int count;
			if (p2 instanceof TerrainPopulator tp2) {
				if (tp2.getWeight() == 0.0F) {
					continue;
				}
				count = Math.round(tp2.getWeight() / smallest);
			} else {
				count = Math.round(1.0F / smallest);
			}
			while (count-- > 0) {
				result.add(p2);
			}
		}
		if (result.isEmpty()) {
			return modules.toArray(new Populator[0]);
		}
		return result.toArray(new Populator[0]);
	}
}
