package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.provider;

import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.RegionConfig;

public interface TerrainProviderFactory {
    TerrainProvider create(GeneratorContext ctx, RegionConfig regionConfig, Populator populator);
}
