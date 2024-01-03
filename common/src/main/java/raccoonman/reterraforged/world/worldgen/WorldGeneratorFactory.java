package raccoonman.reterraforged.world.worldgen;

import java.util.function.Supplier;

import raccoonman.reterraforged.world.worldgen.cell.climate.Climate;
import raccoonman.reterraforged.world.worldgen.cell.heightmap.Heightmap;

public class WorldGeneratorFactory implements Supplier<WorldGenerator> {
    private Heightmap heightmap;
    private WorldFilters filters;
    
    public WorldGeneratorFactory(GeneratorContext context) {
        this.heightmap = Heightmap.make(context);
        this.filters = new WorldFilters(context);
    }
    
    public WorldGeneratorFactory(GeneratorContext context, Heightmap heightmap) {
        this.heightmap = heightmap;
        this.filters = new WorldFilters(context);
    }
    
    public Heightmap getHeightmap() {
        return this.heightmap;
    }
    
    public Climate getClimate() {
        return this.getHeightmap().climate();
    }
    
    public WorldFilters getFilters() {
        return this.filters;
    }
    
    @Override
    public WorldGenerator get() {
        return new WorldGenerator(this.heightmap, this.filters);
    }
}
