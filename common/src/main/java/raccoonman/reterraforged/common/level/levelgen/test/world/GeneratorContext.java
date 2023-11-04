package raccoonman.reterraforged.common.level.levelgen.test.world;

import java.util.function.Function;

import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.task.LazySupplier;
import raccoonman.reterraforged.common.level.levelgen.test.concurrent.thread.ThreadPools;
import raccoonman.reterraforged.common.level.levelgen.test.tile.api.TileProvider;
import raccoonman.reterraforged.common.level.levelgen.test.tile.gen.TileGenerator;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.provider.StandardTerrainProvider;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.provider.TerrainProviderFactory;
import raccoonman.reterraforged.common.worldgen.data.preset.FilterSettings;
import raccoonman.reterraforged.common.worldgen.data.preset.Preset;

public class GeneratorContext {
    public Seed seed;
    public Levels levels;
    public Preset settings;
    public LazySupplier<TileProvider> cache;
    public TerrainProviderFactory terrainFactory;
    public LazySupplier<WorldGeneratorFactory> worldGenerator;
    
    public GeneratorContext(Preset settings, long seed) {
        this(settings, seed, StandardTerrainProvider::new, factory -> {
        	int tileSize = 3;
            int tileBorder = getTileBorderSize(factory.getFilters().getSettings());
            int batchCount = 5;
            int threadCount = ThreadPools.defaultPoolSize();
            return TileGenerator.builder()
                    .pool(ThreadPools.create(threadCount))
                    .size(tileSize, tileBorder)
                    .batch(batchCount)
                    .factory(factory)
                    .build()
                    .cached();
        });// GeneratorContext::createCache);
    }
    
    private static int getTileBorderSize(FilterSettings settings) {
        // Scale tile border size with droplet lifetime
        return Math.min(2, Math.max(1, settings.erosion.dropletLifetime / 16));
    }
    
    public <V> LazySupplier<V> then(Function<GeneratorContext, V> function) {
        return LazySupplier.factory(this.copy(), function);
    }
    
    public GeneratorContext(Preset settings, long seed, TerrainProviderFactory terrainFactory, Function<WorldGeneratorFactory, TileProvider> cache) {
        this.settings = settings;
        this.seed = new Seed((int) seed);
        this.levels = new Levels(settings.world().properties.worldHeight, settings.world().properties.seaLevel);
        this.terrainFactory = terrainFactory;
        this.worldGenerator = this.createFactory(this);
        this.cache = LazySupplier.supplied(this.worldGenerator, cache);
    }
    
    protected GeneratorContext(GeneratorContext src) {
        this(src, 0);
    }
    
    protected GeneratorContext(GeneratorContext src, int seedOffset) {
        this.seed = src.seed.offset(seedOffset);
        this.cache = src.cache;
        this.levels = src.levels;
        this.settings = src.settings;
        this.terrainFactory = src.terrainFactory;
        this.worldGenerator = src.worldGenerator;
    }
    
    public GeneratorContext copy() {
        return new GeneratorContext(this);
    }
    
    public GeneratorContext split(int offset) {
        return new GeneratorContext(this, offset);
    }
    
    public Seed seed() {
        return this.seed.split();
    }
    
    public Seed seed(int offset) {
        return this.seed.offset(offset);
    }
    
    protected LazySupplier<WorldGeneratorFactory> createFactory(GeneratorContext context) {
        return LazySupplier.factory(context.copy(), WorldGeneratorFactory::new);
    }
    
    public static GeneratorContext createNoCache(Preset settings, long seed) {
        return new GeneratorContext(settings, seed, StandardTerrainProvider::new, s -> null);
    }
    
    public static TileProvider createCache(WorldGeneratorFactory factory) {
        return TileGenerator.builder().pool(ThreadPools.createDefault()).factory(factory).size(3, 1).build().cached();
    }
}
