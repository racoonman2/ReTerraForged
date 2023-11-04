package raccoonman.reterraforged.common.level.levelgen.test.world.continent.fancy;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.Continent;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.RiverCache;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public class FancyContinentGenerator implements Continent {
    private float frequency;
    private Domain warp;
    private FancyContinent source;
    private RiverCache riverCache;
    
    public FancyContinentGenerator(Seed seed, GeneratorContext context) {
        WorldSettings settings = context.settings.world();
        int warpScale = settings.continent.continentScale / 2;
        double warpStrength = warpScale * 0.4;
        this.source = new FancyContinent(seed.next(), 4, 0.2F, context, this);
        this.frequency = 1.0F / settings.continent.continentScale;
        this.riverCache = new RiverCache(this.source);
        this.warp = Domain.warp(Source.SIMPLEX, seed.next(), warpScale, 2, warpStrength).add(Domain.warp(seed.next(), 80, 2, 40.0)).add(Domain.warp(seed.next(), 20, 1, 15.0));
    }
    
    public FancyContinent getSource() {
        return this.source;
    }
    
    @Override
    public Rivermap getRivermap(int x, int y, int seed) {
        return this.riverCache.getRivers(x, y, seed);
    }
    
    @Override
    public float getEdgeValue(float x, float y, int seed) {
        float px = this.warp.getX(x, y, seed);
        float py = this.warp.getY(x, y, seed);
        px *= this.frequency;
        py *= this.frequency;
        return this.source.compute(px, py, seed);
    }
    
    @Override
    public float getLandValue(float x, float y, int seed) {
        float px = this.warp.getX(x, y, seed);
        float py = this.warp.getY(x, y, seed);
        px *= this.frequency;
        py *= this.frequency;
        float value = this.source.getLandValue(px, py);
        return NoiseUtil.map(value, 0.2F, 0.4F, 0.2F);
    }
    
    @Override
    public long getNearestCenter(float x, float z, int seed) {
        long min = this.source.getMin();
        long max = this.source.getMax();
        float width = NoiseUtil.unpackLeftf(max) - NoiseUtil.unpackLeftf(min);
        float height = NoiseUtil.unpackRightf(max) - NoiseUtil.unpackRightf(min);
        float cx = width * 0.5F;
        float cz = height * 0.5F;
        int centerX = (int)(cx / this.frequency);
        int centerZ = (int)(cz / this.frequency);
        return NoiseUtil.pack(centerX, centerZ);
    }
    
    @Override
    public void apply(Cell cell, float x, float y, int seed) {
        cell.continentX = 0;
        cell.continentZ = 0;
        cell.continentId = 0.0F;
        cell.continentEdge = this.getEdgeValue(x, y, seed);
    }
}
