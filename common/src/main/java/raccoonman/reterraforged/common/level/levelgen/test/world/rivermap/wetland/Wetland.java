package raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.wetland;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.source.Line;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.util.Boundsf;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.ErosionLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.RidgeLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator.TerrainPopulator;

public class Wetland extends TerrainPopulator {
    private Vec2f a;
    private Vec2f b;
    private float radius;
    private float radius2;
    private float bed;
    private float banks;
    private float moundMin;
    private float moundMax;
    private float moundVariance;
    private Noise moundShape;
    private Noise moundHeight;
    private Noise terrainEdge;
    
    public Wetland(int seed, Vec2f a, Vec2f b, float radius, Levels levels) {
        super(TerrainType.WETLAND, Source.ZERO, Source.ZERO, Source.ZERO, Source.ZERO, 1.0F);
        this.a = a;
        this.b = b;
        this.radius = radius;
        this.radius2 = radius * radius;
        this.bed = levels.water(-1) - 0.5F / levels.worldHeight;
        this.banks = levels.ground(3);
        this.moundMin = levels.water(1);
        this.moundMax = levels.water(2);
        this.moundVariance = this.moundMax - this.moundMin;
        this.moundShape = Source.perlin(++seed, 10, 1).clamp(0.3, 0.6).map(0.0, 1.0);
        this.moundHeight = Source.simplex(++seed, 20, 1).clamp(0.0, 0.3).map(0.0, 1.0);
        this.terrainEdge = Source.perlin(++seed, 8, 1).clamp(0.2, 0.8).map(0.0, 0.9);
    }
    
    @Override
    public void apply(Cell cell, float x, float z, int seed) {
        this.apply(cell, x, z, x, z, seed);
    }
    
    public void apply(Cell cell, float rx, float rz, float x, float z, int seed) {
        if (cell.value < this.bed) {
            return;
        }
        float t = Line.distanceOnLine(rx, rz, this.a.x(), this.a.y(), this.b.x(), this.b.y());
        float d2 = getDistance2(rx, rz, this.a.x(), this.a.y(), this.b.x(), this.b.y(), t);
        if (d2 > this.radius2) {
            return;
        }
        float dist = 1.0F - d2 / this.radius2;
        if (dist <= 0.0F) {
            return;
        }
        float valleyAlpha = NoiseUtil.map(dist, 0.0F, 0.65F, 0.65F);
        if (cell.value > this.banks) {
            cell.value = NoiseUtil.lerp(cell.value, this.banks, valleyAlpha);
        }
        float poolsAlpha = NoiseUtil.map(dist, 0.65F, 0.7F, 0.050000012F);
        if (cell.value > this.bed && cell.value <= this.banks) {
            cell.value = NoiseUtil.lerp(cell.value, this.bed, poolsAlpha);
        }
        if (poolsAlpha >= 1.0F) {
            cell.erosionMask = true;
        }
        if (dist > 0.65F && poolsAlpha > this.terrainEdge.compute(x, z, seed)) {
            cell.terrain = this.getType();
            cell.ridgeLevel = RidgeLevel.VALLEYS.mid();
            cell.erosionLevel = ErosionLevel.LEVEL_6.mid();
        }
        if (cell.value >= this.bed && cell.value < this.moundMax) {
            float shapeAlpha = this.moundShape.compute(x, z, seed) * poolsAlpha;
            float mounds = this.moundMin + this.moundHeight.compute(x, z, seed) * this.moundVariance;
            cell.value = NoiseUtil.lerp(cell.value, mounds, shapeAlpha);
        }
        cell.riverMask = Math.min(cell.riverMask, 1.0F - valleyAlpha);
    }
    
    public void recordBounds(Boundsf.Builder builder) {
        builder.record(Math.min(this.a.x(), this.b.x()) - this.radius, Math.min(this.a.y(), this.b.y()) - this.radius);
        builder.record(Math.max(this.a.x(), this.b.x()) + this.radius, Math.max(this.a.y(), this.b.y()) + this.radius);
    }
    
    private static float getDistance2(float x, float y, float ax, float ay, float bx, float by, float t) {
        if (t <= 0.0f) {
            return Line.dist2(x, y, ax, ay);
        }
        if (t >= 1.0f) {
            return Line.dist2(x, y, bx, by);
        }
        float px = ax + t * (bx - ax);
        float py = ay + t * (by - ay);
        return Line.dist2(x, y, px, py);
    }
}
