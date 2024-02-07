package raccoonman.reterraforged.world.worldgen.terrain.populator;

import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil;
import raccoonman.reterraforged.world.worldgen.noise.NoiseUtil.Vec2f;
import raccoonman.reterraforged.world.worldgen.rivermap.lake.LakeConfig;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.util.Boundsf;

public class LakePopulator {
    protected float valley;
    protected float valley2;
    protected float lakeDistance2;
    protected float valleyDistance2;
    protected float bankAlphaMin;
    protected float bankAlphaMax;
    protected float bankAlphaRange;
    private float depth;
    private float bankMin;
    private float bankMax;
    protected Vec2f center;
    
    public LakePopulator(Vec2f center, float radius, float multiplier, LakeConfig config) {
        float lake = radius * multiplier;
        float valley = 275.0F * multiplier;
        this.valley = valley;
        this.valley2 = valley * valley;
        this.center = center;
        this.depth = config.depth;
        this.bankMin = config.bankMin;
        this.bankMax = config.bankMax;
        this.bankAlphaMin = config.bankMin;
        this.bankAlphaMax = Math.min(1.0F, this.bankAlphaMin + 0.275F);
        this.bankAlphaRange = this.bankAlphaMax - this.bankAlphaMin;
        this.lakeDistance2 = lake * lake;
        this.valleyDistance2 = this.valley2 - this.lakeDistance2;
    }
    
    public void apply(Cell cell, float x, float z) {
        float distance2 = this.getDistance2(x, z);
        if (distance2 > this.valley2) {
            return;
        }
        float bankHeight = this.getBankHeight(cell);
        if (distance2 <= this.lakeDistance2) {
            cell.height = Math.min(bankHeight, cell.height);
            if (distance2 < this.lakeDistance2) {
                float depthAlpha = 1.0F - distance2 / this.lakeDistance2;
                if (depthAlpha < 0.0F) {
                    depthAlpha = 0.0F;
                } else if (depthAlpha > 1.0F) {
                    depthAlpha = 1.0F;
                }
                float lakeDepth = Math.min(cell.height, this.depth);
                cell.height = NoiseUtil.lerp(cell.height, lakeDepth, depthAlpha);
                cell.terrain = TerrainType.LAKE;
                cell.riverDistance = Math.min(cell.riverDistance, 1.0F - depthAlpha);
            }
            return;
        }
        if (cell.height < bankHeight) {
            return;
        }
        float valleyAlpha = 1.0F - (distance2 - this.lakeDistance2) / this.valleyDistance2;
        if (valleyAlpha < 0.0F) {
            valleyAlpha = 0.0F;
        } else if (valleyAlpha > 1.0F) {
            valleyAlpha = 1.0F;
        }
        cell.height = NoiseUtil.lerp(cell.height, bankHeight, valleyAlpha);
        cell.riverDistance *= 1.0F - valleyAlpha;
        cell.riverDistance = Math.min(cell.riverDistance, 1.0F - valleyAlpha);
    }
    
    public void recordBounds(Boundsf.Builder builder) {
        builder.record(this.center.x() - this.valley * 1.2F, this.center.y() - this.valley * 1.2F);
        builder.record(this.center.x() + this.valley * 1.2F, this.center.y() + this.valley * 1.2F);
    }
    
    public boolean overlaps(float x, float z, float radius2) {
        float dist2 = this.getDistance2(x, z);
        return dist2 < this.lakeDistance2 + radius2;
    }
    
    protected float getDistance2(float x, float z) {
        float dx = this.center.x() - x;
        float dz = this.center.y() - z;
        return dx * dx + dz * dz;
    }
    
    protected float getBankHeight(Cell cell) {
        float bankHeightAlpha = NoiseUtil.map(cell.height, this.bankAlphaMin, this.bankAlphaMax, this.bankAlphaRange);
        return NoiseUtil.lerp(this.bankMin, this.bankMax, bankHeightAlpha);
    }
}
