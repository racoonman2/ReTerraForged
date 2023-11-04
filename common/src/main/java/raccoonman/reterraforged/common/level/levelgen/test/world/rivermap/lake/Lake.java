package raccoonman.reterraforged.common.level.levelgen.test.world.rivermap.lake;

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.Vec2f;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.util.Boundsf;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.ErosionLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.biome.type.RidgeLevel;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator.TerrainPopulator;

public class Lake extends TerrainPopulator {
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
    
    public Lake(Vec2f center, float radius, float multiplier, LakeConfig config) {
        super(TerrainType.LAKE, Source.ZERO, Source.ZERO, Source.ZERO, Source.ZERO, 1.0F);
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
    
    @Override
    public void apply(Cell cell, float x, float z, int seed) {
        float distance2 = this.getDistance2(x, z);
        if (distance2 > this.valley2) {
            return;
        }
        float bankHeight = this.getBankHeight(cell);
        if (distance2 <= this.lakeDistance2) {
            cell.value = Math.min(bankHeight, cell.value);
            if (distance2 < this.lakeDistance2) {
                float depthAlpha = 1.0F - distance2 / this.lakeDistance2;
                if (depthAlpha < 0.0F) {
                    depthAlpha = 0.0F;
                } else if (depthAlpha > 1.0F) {
                    depthAlpha = 1.0F;
                }
                float lakeDepth = Math.min(cell.value, this.depth);
                cell.value = NoiseUtil.lerp(cell.value, lakeDepth, depthAlpha);
                cell.terrain = TerrainType.LAKE;
                cell.riverMask = Math.min(cell.riverMask, 1.0F - depthAlpha);
                cell.erosionLevel = ErosionLevel.LEVEL_5.mid();
                cell.ridgeLevel = RidgeLevel.VALLEYS.mid();
            }
            return;
        }
        if (cell.value < bankHeight) {
            return;
        }
        float valleyAlpha = 1.0F - (distance2 - this.lakeDistance2) / this.valleyDistance2;
        if (valleyAlpha < 0.0F) {
            valleyAlpha = 0.0F;
        } else if (valleyAlpha > 1.0F) {
            valleyAlpha = 1.0F;
        }
        cell.value = NoiseUtil.lerp(cell.value, bankHeight, valleyAlpha);
        cell.riverMask *= 1.0F - valleyAlpha;
        cell.riverMask = Math.min(cell.riverMask, 1.0F - valleyAlpha);
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
        float bankHeightAlpha = NoiseUtil.map(cell.value, this.bankAlphaMin, this.bankAlphaMax, this.bankAlphaRange);
        return NoiseUtil.lerp(this.bankMin, this.bankMax, bankHeightAlpha);
    }
}
