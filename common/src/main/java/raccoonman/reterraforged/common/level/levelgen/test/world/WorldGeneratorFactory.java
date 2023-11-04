package raccoonman.reterraforged.common.level.levelgen.test.world;

import java.util.function.Supplier;

import raccoonman.reterraforged.common.level.levelgen.test.climate.Climate;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Heightmap;

public class WorldGeneratorFactory implements Supplier<WorldGenerator> {
    private final Heightmap heightmap;
    private final WorldFilters filters;
    
    public WorldGeneratorFactory(GeneratorContext context) {
        this.heightmap = new Heightmap(context);
        this.filters = new WorldFilters(context);
    }
    
    public WorldGeneratorFactory(final GeneratorContext context, final Heightmap heightmap) {
        this.heightmap = heightmap;
        this.filters = new WorldFilters(context);
    }
    
    public Heightmap getHeightmap() {
        return this.heightmap;
    }
    
    public Climate getClimate() {
        return this.getHeightmap().getClimate();
    }
    
    public WorldFilters getFilters() {
        return this.filters;
    }
    
    @Override
    public WorldGenerator get() {
        return new WorldGenerator(this.heightmap, this.filters);
    }
}
