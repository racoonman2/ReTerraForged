package raccoonman.reterraforged.world.worldgen.terrain.populator;

import raccoonman.reterraforged.world.worldgen.biome.Weirdness;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.noise.module.Line;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.util.Boundsf;

public class WetlandPopulator {
    private Vec2f a;
    private Vec2f b;
    private float radius;
    private float radiusSq;
    private float bed;
    private float banks;
    private float moundMin;
    private float moundMax;
    private float moundVariance;
    private Noise moundShape;
    private Noise moundHeight;
    private Noise terrainEdge;
    
    public WetlandPopulator(int seed, Vec2f a, Vec2f b, float radius, Levels levels) {
        this.a = a;
        this.b = b;
        this.radius = radius;
        this.radiusSq = radius * radius;
        this.bed = levels.water(-1) - 0.5F / levels.worldHeight;
        this.banks = levels.ground(3);
        this.moundMin = levels.water(1);
        this.moundMax = levels.water(2);
        this.moundVariance = this.moundMax - this.moundMin;
        
        Noise moundShape = Noises.perlin(++seed, 10, 1);
        moundShape = Noises.clamp(moundShape, 0.3F, 0.6F);
        moundShape = Noises.map(moundShape, 0.0F, 1.0F);
        this.moundShape = moundShape;
        
        Noise moundHeight = Noises.simplex(++seed, 20, 1);
        moundHeight = Noises.clamp(moundHeight, 0.0F, 0.3F);
        moundHeight = Noises.map(moundHeight, 0.0F, 1.0F);
        this.moundHeight = moundHeight;
        
        Noise terrainEdge = Noises.perlin(++seed, 8, 1);
        terrainEdge = Noises.clamp(terrainEdge, 0.2F, 0.8F);
        terrainEdge = Noises.map(terrainEdge, 0.0F, 0.9F);
        this.terrainEdge = terrainEdge;
    }
    
    public void apply(Cell cell, float rx, float rz, float x, float z) {
        if (cell.height < this.bed) {
            return;
        }
        float t = Line.distanceOnLine(rx, rz, this.a.x(), this.a.y(), this.b.x(), this.b.y());
        float d2 = getDistanceSq(rx, rz, this.a.x(), this.a.y(), this.b.x(), this.b.y(), t);
        if (d2 > this.radiusSq) {
            return;
        }
        float dist = 1.0F - d2 / this.radiusSq;
        if (dist <= 0.0F) {
            return;
        }
        float valleyAlpha = NoiseUtil.map(dist, 0.0F, 0.65F, 0.65F);
        float erosionAlpha = NoiseUtil.map(Math.max(dist, 0.0F), 0.0F, 0.65F, 0.65F, false);
        cell.erosion = NoiseUtil.lerp(cell.erosion, 0.55025F, erosionAlpha);
        cell.weirdness = Weirdness.LOW_SLICE_NORMAL_DESCENDING.midpoint();
        if (cell.height > this.banks) {
            cell.height = NoiseUtil.lerp(cell.height, this.banks, valleyAlpha);
        }

        float poolsAlpha = NoiseUtil.map(dist, 0.65F, 0.7F, 0.050000012F);
        if (cell.height > this.bed && cell.height <= this.banks) {
            cell.height = NoiseUtil.lerp(cell.height, this.bed, poolsAlpha);
        }
        if (poolsAlpha >= 1.0F) {
            cell.erosionMask = true;
        }
        if (dist > 0.65F && poolsAlpha > this.terrainEdge.compute(x, z, 0)) {
            cell.terrain = TerrainType.WETLAND;
        }
        if (cell.height >= this.bed && cell.height < this.moundMax) {
            float shapeAlpha = this.moundShape.compute(x, z, 0) * poolsAlpha;
            float mounds = this.moundMin + this.moundHeight.compute(x, z, 0) * this.moundVariance;
            cell.height = NoiseUtil.lerp(cell.height, mounds, shapeAlpha);
        }
        cell.riverMask = Math.min(cell.riverMask, 1.0F - valleyAlpha);
    }
    
    public void recordBounds(Boundsf.Builder builder) {
        builder.record(Math.min(this.a.x(), this.b.x()) - this.radius, Math.min(this.a.y(), this.b.y()) - this.radius);
        builder.record(Math.max(this.a.x(), this.b.x()) + this.radius, Math.max(this.a.y(), this.b.y()) + this.radius);
    }
    
    private static float getDistanceSq(float x, float y, float ax, float ay, float bx, float by, float t) {
        if (t <= 0.0f) {
            return Line.distSq(x, y, ax, ay);
        }
        if (t >= 1.0f) {
            return Line.distSq(x, y, bx, by);
        }
        float px = ax + t * (bx - ax);
        float py = ay + t * (by - ay);
        return Line.distSq(x, y, px, py);
    }
}
