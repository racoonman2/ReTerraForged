package raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter;

import raccoonman.reterraforged.data.worldgen.preset.settings.FilterSettings;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;

public record Smoothing(float smoothingRadius, float smoothingRate, Modifier modifier) implements Filter {

	@Override
	public void apply(Filterable map, int seedX, int seedZ, int iterations) {
		while (iterations-- > 0) {
			this.apply(map);
		}
	}

	private void apply(Filterable cellMap) {
		int radius = NoiseUtil.round(this.smoothingRadius + 0.5F);
		float radiusSq = this.smoothingRadius * this.smoothingRadius;
		
		int maxZ = cellMap.getBlockSize().total() - radius;
		int maxX = cellMap.getBlockSize().total() - radius;
		for (int z = radius; z < maxZ; ++z) {
			for (int x = radius; x < maxX; ++x) {
				Cell cell = cellMap.getCellRaw(x, z);
				if (!cell.erosionMask) {
					float total = 0.0F;
					float weights = 0.0F;
					for (int dz = -radius; dz <= radius; ++dz) {
						for (int dx = -radius; dx <= radius; ++dx) {
							float dist2 = (float) (dx * dx + dz * dz);
							if (dist2 <= radiusSq) {
								int px = x + dx;
								int pz = z + dz;
								Cell neighbour = cellMap.getCellRaw(px, pz);
								if (!neighbour.isAbsent()) {
									float value = neighbour.height;
									float weight = 1.0F - dist2 / radiusSq;
									total += value * weight;
									weights += weight;
								}
							}
						}
					}
					if (weights > 0.0F) {
						float dif = cell.height - total / weights;
						Cell cell2 = cell;
						cell2.height -= this.modifier.modify(cell, dif * this.smoothingRate);
					}
				}
			}
		}
	}
	
	public static Smoothing make(FilterSettings.Smoothing settings, Levels levels) {
		return new Smoothing(settings.smoothingRadius, settings.smoothingRate, Modifier.range(levels.ground(1), levels.ground(120)).invert());
	}
}
