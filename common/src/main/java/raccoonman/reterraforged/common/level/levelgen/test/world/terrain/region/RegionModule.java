package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.region;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.DistanceFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Populator;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.RegionConfig;

public class RegionModule implements Populator {
    private int seed;
    private float frequency;
    private float edgeMin;
    private float edgeMax;
    private float edgeRange;
    private Domain warp;
    
    public RegionModule(RegionConfig regionConfig) {
        this.seed = regionConfig.seed() + 7;
        this.edgeMin = 0.0F;
        this.edgeMax = 0.5F;
        this.edgeRange = this.edgeMax - this.edgeMin;
        this.frequency = 1.0F / regionConfig.scale();
        this.warp = Domain.warp(regionConfig.warpX(), regionConfig.warpZ(), Source.constant(regionConfig.warpStrength()));
    }
    
    @Override
    public void apply(Cell cell, float x, float y, int seed) {
        float ox = this.warp.getOffsetX(x, y, seed);
        float oz = this.warp.getOffsetY(x, y, seed);
        float px = x + ox;
        float py = y + oz;
        px *= this.frequency;
        py *= this.frequency;
        int cellX = 0;
        int cellY = 0;
        float centerX = 0.0F;
        float centerY = 0.0F;
        int xi = NoiseUtil.floor(px);
        int yi = NoiseUtil.floor(py);
        float edgeDistance = Float.MAX_VALUE;
        float edgeDistance2 = Float.MAX_VALUE;
        DistanceFunction dist = DistanceFunction.NATURAL;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                int cx = xi + dx;
                int cy = yi + dy;
                Vec2f vec = NoiseUtil.cell(this.seed, cx, cy);
                float vecX = cx + vec.x() * 0.7F;
                float vecY = cy + vec.y() * 0.7F;
                float distance = dist.apply(vecX - px, vecY - py);
                if (distance < edgeDistance) {
                    edgeDistance2 = edgeDistance;
                    edgeDistance = distance;
                    centerX = vecX;
                    centerY = vecY;
                    cellX = cx;
                    cellY = cy;
                }
                else if (distance < edgeDistance2) {
                    edgeDistance2 = distance;
                }
            }
        }
        cell.terrainRegionId = this.cellValue(this.seed, cellX, cellY);
        cell.terrainRegionEdge = this.edgeValue(edgeDistance, edgeDistance2);
        cell.terrainRegionCenter = NoiseUtil.pack(centerX / this.frequency, centerY / this.frequency);
    }
    
    private float cellValue(int seed, int cellX, int cellY) {
        float value = NoiseUtil.valCoord2D(seed, cellX, cellY);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }
    
    private float edgeValue(float distance, float distance2) {
        EdgeFunction edge = EdgeFunction.DISTANCE_2_DIV;
        float value = edge.apply(distance, distance2);
        float edgeValue = 1.0F - NoiseUtil.map(value, edge.min(), edge.max(), edge.range());
        edgeValue = NoiseUtil.pow(edgeValue, 1.5F);
        if (edgeValue < this.edgeMin) {
            return 0.0F;
        }
        if (edgeValue > this.edgeMax) {
            return 1.0F;
        }
        return (edgeValue - this.edgeMin) / this.edgeRange;
    }
}
