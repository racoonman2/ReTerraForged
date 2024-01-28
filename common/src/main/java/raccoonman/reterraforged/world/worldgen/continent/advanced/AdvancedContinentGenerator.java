package raccoonman.reterraforged.world.worldgen.continent.advanced;

import raccoonman.reterraforged.concurrent.Resource;
import raccoonman.reterraforged.data.preset.settings.WorldSettings;
import raccoonman.reterraforged.world.worldgen.GeneratorContext;
import raccoonman.reterraforged.world.worldgen.biome.Continentalness;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.continent.SimpleContinent;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.noise.domain.Domain;
import raccoonman.reterraforged.world.worldgen.noise.domain.Domains;
import raccoonman.reterraforged.world.worldgen.noise.module.Line;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.rivermap.Rivermap;
import raccoonman.reterraforged.world.worldgen.util.PosUtil;
import raccoonman.reterraforged.world.worldgen.util.Seed;

public class AdvancedContinentGenerator extends AbstractContinent implements SimpleContinent {
    protected static float CENTER_CORRECTION = 0.35F;
    protected float frequency;
    protected float variance;
    protected int varianceSeed;
    protected Domain warp;
    protected Noise cliffNoise;
    protected Noise bayNoise;
    
    public AdvancedContinentGenerator(Seed seed, GeneratorContext context) {
        super(seed, context);
        WorldSettings settings = context.preset.world();
        int tectonicScale = settings.continent.continentScale * 4;
        this.frequency = 1.0F / tectonicScale;
        this.varianceSeed = seed.next();
        this.variance = settings.continent.continentSizeVariance;
        this.warp = this.createWarp(seed, tectonicScale, settings.continent);
        
        float frequency = 1.0F / this.frequency;
        
        Noise cliffNoise = Noises.simplex2(seed.next(), this.continentScale / 2, 2);
        cliffNoise = Noises.clamp(cliffNoise, 0.1F, 0.25F);
        cliffNoise = Noises.map(cliffNoise, 0.0F, 1.0F);
        cliffNoise = Noises.frequency(cliffNoise, frequency);
        this.cliffNoise = cliffNoise;

        Noise bayNoise = Noises.simplex(seed.next(), 100, 1);
        bayNoise = Noises.mul(bayNoise, 0.1F);
        bayNoise = Noises.add(bayNoise, 0.9F);
        bayNoise = Noises.frequency(bayNoise, frequency);
        this.bayNoise = bayNoise;
    }
    
    @Override
    public void apply(Cell cell, float x, float y) {
        float wx = this.warp.getX(x, y, 0);
        float wy = this.warp.getZ(x, y, 0);
        x = wx * this.frequency;
        y = wy * this.frequency;
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        float cellPointX = x;
        float cellPointY = y;
        float nearest = Float.MAX_VALUE;
        for (int cy = yi - 1; cy <= yi + 1; ++cy) {
            for (int cx = xi - 1; cx <= xi + 1; ++cx) {
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float px = cx + vec.x() * this.jitter;
                float py = cy + vec.y() * this.jitter;
                float dist2 = Line.distSq(x, y, px, py);
                if (dist2 < nearest) {
                    cellPointX = px;
                    cellPointY = py;
                    cellX = cx;
                    cellY = cy;
                    nearest = dist2;
                }
            }
        }
        nearest = Float.MAX_VALUE;
        float sumX = 0.0F;
        float sumY = 0.0F;
        for (int cy2 = cellY - 1; cy2 <= cellY + 1; ++cy2) {
            for (int cx2 = cellX - 1; cx2 <= cellX + 1; ++cx2) {
                if (cx2 != cellX || cy2 != cellY) {
                    Vec2f vec2 = NoiseUtil.cell(this.seed, cx2, cy2);
                    float px2 = cx2 + vec2.x() * this.jitter;
                    float py2 = cy2 + vec2.y() * this.jitter;
                    float dist3 = getDistance(x, y, cellPointX, cellPointY, px2, py2);
                    sumX += px2;
                    sumY += py2;
                    if (dist3 < nearest) {
                        nearest = dist3;
                    }
                }
            }
        }
        
        cell.continentX = this.getCorrectedContinentCenter(cellPointX, sumX / 8.0F);
        cell.continentZ = this.getCorrectedContinentCenter(cellPointY, sumY / 8.0F);
        if (this.shouldSkip(cellX, cellY)) {
            return;
        }
        cell.continentId = AbstractContinent.getCellValue(this.seed, cellX, cellY);
        cell.continentEdge = this.getDistanceValue(x, y, cellX, cellY, nearest);
        cell.continentalness = this.getContinenalness(x, y, cellX, cellY, nearest) * 2.0F;
    }
    
    @Override
    public float getEdgeValue(float x, float z) {
        try (Resource<Cell> resource = Cell.getResource()) {
            Cell cell = resource.get();
            this.apply(cell, x, z);
            return cell.continentEdge;
        }
    }
    
    @Override
    public long getNearestCenter(float x, float z) {
        try (Resource<Cell> resource = Cell.getResource()) {
            Cell cell = resource.get();
            this.apply(cell, x, z);
            return PosUtil.pack(cell.continentX, cell.continentZ);
        }
    }
    
    @Override
    public Rivermap getRivermap(int x, int z) {
        return this.riverCache.getRivers(x, z);
    }
    
    protected Domain createWarp(Seed seed, int tectonicScale, WorldSettings.Continent continent) {
        int warpScale = NoiseUtil.round(tectonicScale * 0.225F);
        float strength = NoiseUtil.round(tectonicScale * 0.33F);
        return Domains.domain(
        	Noises.perlin2(seed.next(), warpScale, continent.continentNoiseOctaves, continent.continentNoiseLacunarity, continent.continentNoiseGain), 
        	Noises.perlin2(seed.next(), warpScale, continent.continentNoiseOctaves, continent.continentNoiseLacunarity, continent.continentNoiseGain), 
        	Noises.constant(strength)
        );
    }

    @Deprecated
    protected float getDistanceValue(float x, float y, int cellX, int cellY, float distance) {
        distance = this.getVariedDistanceValue(cellX, cellY, distance);
        distance = NoiseUtil.sqrt(distance);
        distance = NoiseUtil.map(distance, 0.05F, 0.25F, 0.2F);
        distance = this.getCoastalDistanceValue(x, y, distance, true);
        if (distance < this.controlPoints.inland && distance >= this.controlPoints.shallowOcean) {
            distance = this.getCoastalDistanceValue(x, y, distance, true);
        }
        return distance;
    }
    
    protected float getVariedDistanceValue(int cellX, int cellY, float distance) {
        if (this.variance > 0.0F && !this.isDefaultContinent(cellX, cellY)) {
            float sizeValue = AbstractContinent.getCellValue(this.varianceSeed, cellX, cellY);
            float sizeModifier = NoiseUtil.map(sizeValue, 0.0F, this.variance, this.variance);
            distance *= sizeModifier;
        }
        return distance;
    }
    
    protected float getCoastalDistanceValue(float x, float y, float distance, boolean cliffs) {
        if (distance > this.controlPoints.shallowOcean && distance < this.controlPoints.inland) {
            float alpha = distance / this.controlPoints.inland;
            float cliff = cliffs ? this.cliffNoise.compute(x, y, 0) : 1.0F;
            distance = NoiseUtil.lerp(distance * cliff, distance, alpha);
            if (distance < this.controlPoints.shallowOcean) {
                distance = this.controlPoints.shallowOcean * this.bayNoise.compute(x, y, 0);
            }
        }
        return distance;
    }

    @Deprecated
    protected float getContinenalness(float x, float y, int cellX, int cellY, float continentalness) {
        continentalness = this.getVariedContinentalness(cellX, cellY, continentalness);
        continentalness = NoiseUtil.sqrt(continentalness);
//        distance = NoiseUtil.map(distance, 0.05F, 0.5F, 0.2F);
//        distance = this.getCoastalContinentalness(x, y, distance, true);
//        if (distance < this.controlPoints.inland && distance >= this.controlPoints.shallowOcean) {
//            distance = this.getCoastalContinentalness(x, y, distance, true);
//        }
        return continentalness;
    }
    
    protected float getVariedContinentalness(int cellX, int cellY, float continentalness) {
        if (this.variance > 0.0F && !this.isDefaultContinent(cellX, cellY)) {
            float sizeValue = AbstractContinent.getCellValue(this.varianceSeed, cellX, cellY);
            float sizeModifier = NoiseUtil.map(sizeValue, 0.0F, this.variance, this.variance);
            continentalness *= sizeModifier;
        }
        return continentalness;
    }
    
    protected float getCoastalContinentalness(float x, float y, float continentalness, boolean cliffs) {
        if (continentalness > this.controlPoints.shallowOcean && continentalness < this.controlPoints.inland) {
            float alpha = continentalness / this.controlPoints.inland;
            float cliff = cliffs ? this.cliffNoise.compute(x, y, 0) : 1.0F;
            continentalness = NoiseUtil.lerp(continentalness * cliff, continentalness, alpha);
            if (continentalness < this.controlPoints.shallowOcean) {
                continentalness = this.controlPoints.shallowOcean * this.bayNoise.compute(x, y, 0);
            }
        }
        return continentalness;
    }
    
    protected int getCorrectedContinentCenter(float point, float average) {
        point = NoiseUtil.lerp(point, average, 0.35F) / this.frequency;
        return (int) point;
    }
    
    protected static float midPoint(float a, float b) {
        return (a + b) * 0.5F;
    }
    
    protected static float getDistance(float x, float y, float ax, float ay, float bx, float by) {
        float mx = midPoint(ax, bx);
        float my = midPoint(ay, by);
        float dx = bx - ax;
        float dy = by - ay;
        float nx = -dy;
        float ny = dx;
        return getDistance2Line(x, y, mx, my, mx + nx, my + ny);
    }
    
    protected static float getDistance2Line(float x, float y, float ax, float ay, float bx, float by) {
        float dx = bx - ax;
        float dy = by - ay;
        float v = (x - ax) * dx + (y - ay) * dy;
        v /= dx * dx + dy * dy;
        float ox = ax + dx * v;
        float oy = ay + dy * v;
        return Line.distSq(x, y, ox, oy);
    }
}
