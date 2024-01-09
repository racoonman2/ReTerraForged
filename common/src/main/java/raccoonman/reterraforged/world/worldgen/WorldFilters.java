package raccoonman.reterraforged.world.worldgen;

import java.util.function.IntFunction;

import raccoonman.reterraforged.data.worldgen.preset.settings.FilterSettings;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.Tile;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter.BeachDetect;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter.Erosion;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter.Filterable;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter.NoiseCorrection;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter.Smoothing;
import raccoonman.reterraforged.world.worldgen.densityfunction.tile.filter.Steepness;

public class WorldFilters {
    private Smoothing smoothing;
    private Steepness steepness;
    private BeachDetect beach;
    private NoiseCorrection corrections;
    private FilterSettings settings;
    private WorldErosion<Erosion> erosion;
    private int erosionIterations;
    private int smoothingIterations;
    
    public WorldFilters(GeneratorContext context) {
        IntFunction<Erosion> factory = Erosion.factory(context);
        this.settings = context.preset.filters();
        this.beach = BeachDetect.make(context);
        this.smoothing = Smoothing.make(context.preset.filters().smoothing, context.levels);
        this.steepness = Steepness.make(1, 10.0F, context.levels);
        this.corrections = new NoiseCorrection(context.levels);
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
        if(optionalFilters) {
        	this.applyCorrections(tile, regionX, regionZ);
        }
    }
    
    private void applyRequiredFilters(Filterable map, int seedX, int seedZ) {
        this.steepness.apply(map, seedX, seedZ, 1);
        this.beach.apply(map, seedX, seedZ, 1);
    }
    
    private void applyOptionalFilters(Filterable map, int seedX, int seedZ) {
        Erosion erosion = this.erosion.get(map.getBlockSize().total());
        erosion.apply(map, seedX, seedZ, this.erosionIterations);
        this.smoothing.apply(map, seedX, seedZ, this.smoothingIterations);
    }
    
    public void applyCorrections(Filterable map, int seedX, int seedZ) {
        this.corrections.apply(map, seedX, seedZ, 1);
    }
}
