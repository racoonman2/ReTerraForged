package raccoonman.reterraforged.world.worldgen;

import raccoonman.reterraforged.world.worldgen.cell.heightmap.Heightmap;

public record WorldGenerator(Heightmap heightmap, WorldFilters filters) {
}
