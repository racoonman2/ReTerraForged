package raccoonman.reterraforged.common.level.levelgen.test.world.continent.simple;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.GeneratorContext;
import raccoonman.reterraforged.common.level.levelgen.test.world.continent.SimpleContinent;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.ControlPoints;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.LegacyRiverCache;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.RiverCache;
import raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.Rivermap;
import raccoonman.reterraforged.common.worldgen.data.preset.WorldSettings;

public abstract class ContinentGenerator implements SimpleContinent {
    protected int seed;
    protected float frequency;
    protected int continentScale;
    private DistanceFunction distanceFunc;
    private ControlPoints controlPoints;
    private float clampMin;
    private float clampMax;
    private float clampRange;
    private float offsetAlpha;
    protected Domain warp;
    protected Noise shape;
    protected RiverCache cache;
    
    public ContinentGenerator(Seed seed, GeneratorContext context) {
        WorldSettings settings = context.settings.world();
        int tectonicScale = settings.continent.continentScale * 4;
        this.continentScale = settings.continent.continentScale / 2;
        this.seed = seed.next();
        this.distanceFunc = settings.continent.continentShape;
        this.controlPoints = ControlPoints.make(settings.controlPoints);
        this.frequency = 1.0F / tectonicScale;
        this.clampMin = 0.2F;
        this.clampMax = 1.0F;
        this.clampRange = this.clampMax - this.clampMin;
        this.offsetAlpha = context.settings.world().continent.continentJitter;
        this.warp = Domain.warp(Source.PERLIN, seed.next(), 20, 2, 20.0).warp(Domain.warp(Source.SIMPLEX, seed.next(), this.continentScale, 3, this.continentScale));
        this.shape = Source.simplex(seed.next(), settings.continent.continentScale * 2, 1).bias(0.65).clamp(0.0, 1.0);
        this.cache = new LegacyRiverCache(new SimpleRiverGenerator(this, context));
    }
    
    @Override
    public Rivermap getRivermap(int x, int y, int seed) {
        return this.cache.getRivers(x, y, seed);
    }
    
    @Override
    public float compute(float x, float y, int seed) {
        Cell cell = new Cell();
        this.apply(cell, x, y, seed);
        return cell.continentEdge;
    }
    
    @Override
    public void apply(Cell cell, float x, float y, int seed) {
        float ox = this.warp.getOffsetX(x, y, seed);
        float oz = this.warp.getOffsetY(x, y, seed);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        int cellX = xr;
        int cellY = yr;
        float centerX = px;
        float centerY = py;
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float cxf = cx + vec.x() * this.offsetAlpha;
                float cyf = cy + vec.y() * this.offsetAlpha;
                float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                    cellX = cx;
                    cellY = cy;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        cell.continentId = this.cellIdentity(this.seed, cellX, cellY);
        cell.continentEdge = this.cellEdgeValue(edgeDistance, edgeDistance2);
        cell.continentX = (int) (centerX / this.frequency);
        cell.continentZ = (int) (centerY / this.frequency);
        cell.continentEdge *= this.getShape(x, y, cell.continentEdge, seed);
    }
    
    @Override
    public float getEdgeValue(float x, float y, int seed) {
        float ox = this.warp.getOffsetX(x, y, seed);
        float oz = this.warp.getOffsetY(x, y, seed);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0F;
        float edgeDistance2 = 999999.0F;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float cxf = cx + vec.x() * this.offsetAlpha;
                float cyf = cy + vec.y() * this.offsetAlpha;
                float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        float edgeValue = this.cellEdgeValue(edgeDistance, edgeDistance2);
        float shapeNoise = this.getShape(x, y, edgeValue, seed);
        return edgeValue * shapeNoise;
    }
    
    @Override
    public long getNearestCenter(float x, float z, int seed) {
        float ox = this.warp.getOffsetX(x, z, seed);
        float oz = this.warp.getOffsetY(x, z, seed);
        float px = x + ox;
        float py = z + oz;
        px *= this.frequency;
        py *= this.frequency;
        float centerX = px;
        float centerY = py;
        int xr = NoiseUtil.floor(px);
        int yr = NoiseUtil.floor(py);
        float edgeDistance = 999999.0F;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xr + dx;
                int cy = yr + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float cxf = cx + vec.x() * this.offsetAlpha;
                float cyf = cy + vec.y() * this.offsetAlpha;
                float distance = this.distanceFunc.apply(cxf - px, cyf - py);
                if (distance < edgeDistance) {
                    edgeDistance = distance;
                    centerX = cxf;
                    centerY = cyf;
                }
            }
        }
        int conX = (int)(centerX / this.frequency);
        int conZ = (int)(centerY / this.frequency);
        return NoiseUtil.pack(conX, conZ);
    }
    
    @Override
    public float getDistanceToOcean(int cx, int cz, float dx, float dz, int seed) {
        float high = this.getDistanceToEdge(cx, cz, dx, dz, seed);
        float low = 0.0F;
        for (int i = 0; i < 50; ++i) {
            float mid = (low + high) / 2.0F;
            float x = cx + dx * mid;
            float z = cz + dz * mid;
            float edge = this.getEdgeValue(x, z, seed);
            if (edge > this.controlPoints.shallowOcean()) {
                low = mid;
            }
            else {
                high = mid;
            }
            if (high - low < 10.0f) {
                break;
            }
        }
        return high;
    }
    
    @Override
    public float getDistanceToEdge(int cx, int cz, float dx, float dz, int seed) {
        float distance = (float)(this.continentScale * 4);
        for (int i = 0; i < 10; ++i) {
            float x = cx + dx * distance;
            float z = cz + dz * distance;
            long centerPos = this.getNearestCenter(x, z, seed);
            int conX = NoiseUtil.unpackLeft(centerPos);
            int conZ = NoiseUtil.unpackRight(centerPos);
            distance += distance;
            if (conX != cx || conZ != cz) {
                float low = 0.0F;
                float high = distance;
                for (int j = 0; j < 50; ++j) {
                    float mid = (low + high) / 2.0F;
                    float px = cx + dx * mid;
                    float pz = cz + dz * mid;
                    centerPos = this.getNearestCenter(px, pz, seed);
                    conX = NoiseUtil.unpackLeft(centerPos);
                    conZ = NoiseUtil.unpackRight(centerPos);
                    if (conX == cx && conZ == cz) {
                        low = mid;
                    }
                    else {
                        high = mid;
                    }
                    if (high - low < 50.0F) {
                        break;
                    }
                }
                return high;
            }
        }
        return distance;
    }
    
    protected float cellIdentity(int seed, int cellX, int cellY) {
        float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }
    
    protected float cellEdgeValue(float distance, float distance2) {
    	EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        value = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        if (value <= this.clampMin) {
            return 0.0F;
        }
        if (value >= this.clampMax) {
            return 1.0F;
        }
        return (value - this.clampMin) / this.clampRange;
    }
    
    protected float getShape(float x, float z, float edgeValue, int seed) {
        if (edgeValue >= this.controlPoints.inland()) {
            return 1.0F;
        }
        float alpha = edgeValue / this.controlPoints.inland();
        return this.shape.compute(x, z, seed) * alpha;
    }
}
