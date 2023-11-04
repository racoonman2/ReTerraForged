package raccoonman.reterraforged.common.level.levelgen.test.world;

import java.util.function.IntFunction;

import raccoonman.reterraforged.common.level.levelgen.test.filter.BeachDetect;
import raccoonman.reterraforged.common.level.levelgen.test.filter.Erosion;
import raccoonman.reterraforged.common.level.levelgen.test.filter.Filterable;
import raccoonman.reterraforged.common.level.levelgen.test.filter.Smoothing;
import raccoonman.reterraforged.common.level.levelgen.test.filter.Steepness;
import raccoonman.reterraforged.common.level.levelgen.test.tile.Tile;
import raccoonman.reterraforged.common.worldgen.data.preset.FilterSettings;

public class WorldFilters {
    private Smoothing smoothing;
    private Steepness steepness;
    private BeachDetect beach;
    private FilterSettings settings;
    private WorldErosion<Erosion> erosion;
    private int erosionIterations;
    private int smoothingIterations;
    
    public WorldFilters(GeneratorContext context) {
        IntFunction<Erosion> factory = Erosion.factory(context);
        this.settings = context.settings.filters();
        this.beach = new BeachDetect(context);
        this.smoothing = new Smoothing(context.settings, context.levels);
        this.steepness = new Steepness(1, 10.0F, context.levels);
        this.erosion = new WorldErosion<>(factory, (e, size) -> e.getSize() == size);
        this.erosionIterations = context.settings.filters().erosion.dropletsPerChunk;
        this.smoothingIterations = context.settings.filters().smoothing.iterations;
    }
    
    public FilterSettings getSettings() {
        return this.settings;
    }
    
    public void apply(Tile tile, boolean optionalFilters) {
        Filterable map = tile.filterable();
        if (optionalFilters) {
            this.applyOptionalFilters(map, tile.getRegionX(), tile.getRegionZ());
        }
        this.applyRequiredFilters(map, tile.getRegionX(), tile.getRegionZ());
    }
    
    public void applyRequiredFilters(Filterable map, int seedX, int seedZ) {
        this.steepness.apply(map, seedX, seedZ, 1);
        this.beach.apply(map, seedX, seedZ, 1);
    }
    
    public void applyOptionalFilters(Filterable map, int seedX, int seedZ) {
        Erosion erosion = this.erosion.get(map.getSize().total());
        erosion.apply(map, seedX, seedZ, this.erosionIterations);
        this.smoothing.apply(map, seedX, seedZ, this.smoothingIterations);
    }
}
