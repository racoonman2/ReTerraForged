package raccoonman.reterraforged.common.level.levelgen.test.world.terrain.special;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Seed;
import raccoonman.reterraforged.common.level.levelgen.noise.Source;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.EdgeFunction;
import raccoonman.reterraforged.common.level.levelgen.test.cell.Cell;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.Levels;
import raccoonman.reterraforged.common.level.levelgen.test.world.heightmap.RegionConfig;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.Terrain;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.TerrainType;
import raccoonman.reterraforged.common.level.levelgen.test.world.terrain.populator.TerrainPopulator;

public class VolcanoPopulator extends TerrainPopulator {
    private Noise cone;
    private Noise height;
    private Noise lowlands;
    private float inversionPoint;
    private float blendLower;
    private float blendUpper;
    private float blendRange;
    private float bias;
    private Terrain inner;
    private Terrain outer;
    
    public VolcanoPopulator(Seed seed, RegionConfig region, Levels levels, float weight) {
        super(TerrainType.VOLCANO, Source.ZERO, Source.ZERO, Source.ZERO, Source.ZERO, weight);
        float midpoint = 0.3F;
        float range = 0.3F;
        Noise heightNoise = Source.perlin(seed.next(), 2, 1).map(0.45, 0.65);
        this.height = Source.cellNoise(region.seed(), region.scale(), heightNoise).warp(region.warpX(), region.warpZ(), region.warpStrength());
        this.cone = Source.cellEdge(region.seed(), region.scale(), EdgeFunction.DISTANCE_2_DIV).invert().warp(region.warpX(), region.warpZ(), region.warpStrength()).powCurve(11.0).clamp(0.475, 1.0).map(0.0, 1.0).grad(0.0, 0.5, 0.5).warp(seed.next(), 15, 2, 10.0).scale(this.height);
        this.lowlands = Source.ridge(seed.next(), 150, 3).warp(seed.next(), 30, 1, 30.0).scale(0.1);
        this.inversionPoint = 0.94F;
        this.blendLower = midpoint - range / 2.0F;
        this.blendUpper = this.blendLower + range;
        this.blendRange = this.blendUpper - this.blendLower;
        this.outer = TerrainType.VOLCANO;
        this.inner = TerrainType.VOLCANO_PIPE;
        this.bias = levels.ground;
    }
    
    @Override
    public void apply(Cell cell, float x, float z, int seed) {
        float value = this.cone.compute(x, z, seed);
        float limit = this.height.compute(x, z, seed);
        float maxHeight = limit * this.inversionPoint;
        if (value > maxHeight) {
            float steepnessModifier = 1.0F;
            float delta = (value - maxHeight) * steepnessModifier;
            float range = limit - maxHeight;
            float alpha = delta / range;
            if (alpha > 0.925F) {
                cell.terrain = this.inner;
            }
            value = maxHeight - maxHeight / 5.0F * alpha;
        } else if (value < this.blendLower) {
            value += this.lowlands.compute(x, z, seed);
            cell.terrain = this.outer;
        } else if (value < this.blendUpper) {
            float alpha2 = 1.0F - (value - this.blendLower) / this.blendRange;
            value += this.lowlands.compute(x, z, seed) * alpha2;
            cell.terrain = this.outer;
        }
        cell.value = this.bias + value;
    }
    
    public static void modifyVolcanoType(Cell cell, Levels levels) {
        if (cell.terrain == TerrainType.VOLCANO_PIPE && (cell.value < levels.water || cell.riverMask < 0.85F)) {
            cell.terrain = TerrainType.VOLCANO;
        }
    }
}
