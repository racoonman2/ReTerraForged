package raccoonman.reterraforged.world.worldgen.tile.filter;

import java.util.function.IntFunction;

import raccoonman.reterraforged.data.preset.settings.FilterSettings;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.heightmap.Heightmap;
import raccoonman.reterraforged.world.worldgen.tile.Tile;

public class WorldFilters {
    private Smoothing smoothing;
    private Steepness steepness;
    private BeachDetect beach;
    private PostProcessing processing;
    private FilterSettings settings;
    private WorldErosion<Erosion> erosion;
    private int erosionIterations;
    private int smoothingIterations;
    
    public WorldFilters(GeneratorContext context, Heightmap heightmap) {
        IntFunction<Erosion> factory = Erosion.factory(context);
        this.settings = context.preset.filters();
        this.beach = BeachDetect.make(context);
        this.smoothing = Smoothing.make(context.preset.filters().smoothing, context.levels);
        this.steepness = Steepness.make(1, 10.0F, context.levels);
        this.processing = new PostProcessing(heightmap, context.levels);
        this.erosion = new WorldErosion<>(factory, (e, size) -> e.getSize() == size);
        this.erosionIterations = context.preset.filters().erosion.dropletsPerChunk;
        this.smoothingIterations = context.preset.filters().smoothing.iterations;
    }
    
    public FilterSettings getSettings() {
        return this.settings;
    }
    
    public void apply(Tile tile, boolean optionalFilters) {
        int regionX = tile.getX();
        int regionZ = tile.getZ();
        
        if (optionalFilters) {
            this.applyOptionalFilters(tile, regionX, regionZ);
        }
        this.applyRequiredFilters(tile, regionX, regionZ);
        this.applyPostProcessing(tile, regionX, regionZ);
    }
    
    private void applyRequiredFilters(Tile tile, int seedX, int seedZ) {
        this.steepness.apply(tile, seedX, seedZ, 1);
        this.beach.apply(tile, seedX, seedZ, 1);
    }
    
    private void applyOptionalFilters(Tile tile, int seedX, int seedZ) {
        Erosion erosion = this.erosion.get(tile.getBlockSize().total());
        erosion.apply(tile, seedX, seedZ, this.erosionIterations);
        this.smoothing.apply(tile, seedX, seedZ, this.smoothingIterations);
    }
    
    public void applyPostProcessing(Tile map, int seedX, int seedZ) {
        this.processing.apply(map, seedX, seedZ, 1);
    }
}
