package raccoonman.reterraforged.common.level.levelgen.test.filter;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public class Smoothing implements Filter {
	private int radius;
	private float rad2;
	private float strength;
	private Modifier modifier;

	public Smoothing(Preset settings, Levels levels) {
		this.radius = NoiseUtil.round(settings.filters().smoothing.smoothingRadius + 0.5F);
		this.rad2 = settings.filters().smoothing.smoothingRadius * settings.filters().smoothing.smoothingRadius;
		this.strength = settings.filters().smoothing.smoothingRate;
		this.modifier = Modifier.range(levels.ground(1), levels.ground(120)).invert();
	}

	@Override
	public void apply(Filterable map, int seedX, int seedZ, int iterations) {
		while (iterations-- > 0) {
			this.apply(map);
		}
	}

	private void apply(Filterable cellMap) {
		int maxZ = cellMap.getSize().total() - this.radius;
		int maxX = cellMap.getSize().total() - this.radius;
		for (int z = this.radius; z < maxZ; ++z) {
			for (int x = this.radius; x < maxX; ++x) {
				Cell cell = cellMap.getCellRaw(x, z);
				if (!cell.erosionMask) {
					float total = 0.0F;
					float weights = 0.0F;
					for (int dz = -this.radius; dz <= this.radius; ++dz) {
						for (int dx = -this.radius; dx <= this.radius; ++dx) {
							float dist2 = (float) (dx * dx + dz * dz);
							if (dist2 <= this.rad2) {
								int px = x + dx;
								int pz = z + dz;
								Cell neighbour = cellMap.getCellRaw(px, pz);
								if (!neighbour.isAbsent()) {
									float value = neighbour.value;
									float weight = 1.0F - dist2 / this.rad2;
									total += value * weight;
									weights += weight;
								}
							}
						}
					}
					if (weights > 0.0F) {
						float dif = cell.value - total / weights;
						Cell cell2 = cell;
						cell2.value -= this.modifier.modify(cell, dif * this.strength);
					}
				}
			}
		}
	}
}
