package raccoonman.reterraforged.world.worldgen.terrain.populator;

import raccoonman.reterraforged.world.worldgen.biome.Erosion;
import raccoonman.reterraforged.world.worldgen.biome.Weirdness;
import raccoonman.reterraforged.world.worldgen.cell.Cell;
import raccoonman.reterraforged.world.worldgen.cell.CellPopulator;
import raccoonman.reterraforged.world.worldgen.heightmap.Levels;
import raccoonman.reterraforged.world.worldgen.heightmap.RegionConfig;
import raccoonman.reterraforged.world.worldgen.noise.function.CellFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.DistanceFunction;
import raccoonman.reterraforged.world.worldgen.noise.function.EdgeFunction;
import raccoonman.reterraforged.world.worldgen.noise.module.Noise;
import raccoonman.reterraforged.world.worldgen.noise.module.Noises;
import raccoonman.reterraforged.world.worldgen.terrain.Terrain;
import raccoonman.reterraforged.world.worldgen.terrain.TerrainType;
import raccoonman.reterraforged.world.worldgen.util.Seed;

public class VolcanoPopulator implements CellPopulator, WeightedPopulator {
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
    private Levels levels;
    private float weight;
    
    public VolcanoPopulator(Seed seed, RegionConfig region, Levels levels, float weight) {
        float midpoint = 0.3F;
        float range = 0.3F;
        Noise heightLookup = Noises.perlin(seed.next(), 2, 1);
        heightLookup = Noises.map(heightLookup, 0.45F, 0.65F);
        
        Noise heightNoise = Noises.worley(region.seed(), region.scale(), CellFunction.NOISE_LOOKUP, DistanceFunction.EUCLIDEAN, heightLookup);
        heightNoise = Noises.warp(heightNoise, region.warpX(), region.warpZ(), region.warpStrength());
        this.height = heightNoise;
        
        Noise cone = Noises.worleyEdge(region.seed(), region.scale(), EdgeFunction.DISTANCE_2_DIV, DistanceFunction.EUCLIDEAN);
        cone = Noises.invert(cone);
        cone = Noises.warp(cone, region.warpX(), region.warpZ(), region.warpStrength());
        cone = Noises.powCurve(cone, 11.0F);
        cone = Noises.clamp(cone, 0.475F, 1.0F);
        cone = Noises.map(cone, 0.0F, 1.0F);
        cone = Noises.gradient(cone, 0.0F, 0.5F, 0.5F);
        cone = Noises.warpPerlin(cone, seed.next(), 15, 2, 10.0F);
        cone = Noises.mul(cone, this.height);
        
        this.cone = cone;
        
        Noise lowlands = Noises.perlinRidge(seed.next(), 150, 3);
        lowlands = Noises.warpPerlin(lowlands, seed.next(), 30, 1, 30.0F);
        lowlands = Noises.mul(lowlands, 0.1F);
        
        this.lowlands = lowlands;
        this.inversionPoint = 0.94F;
        this.blendLower = midpoint - range / 2.0F;
        this.blendUpper = this.blendLower + range;
        this.blendRange = this.blendUpper - this.blendLower;
        this.outer = TerrainType.VOLCANO;
        this.inner = TerrainType.VOLCANO_PIPE;
        this.levels = levels;
        this.bias = levels.ground;
        
        this.weight = weight;
    }
    
    @Override
    public float weight() {
    	return this.weight;
    }
    
    @Override
    public void apply(Cell cell, float x, float z) {
        float value = this.cone.compute(x, z, 0);
        float limit = this.height.compute(x, z, 0);
        float maxHeight = limit * this.inversionPoint;
        cell.weirdness = Weirdness.LOW_SLICE_NORMAL_DESCENDING.midpoint();
        cell.erosion = Erosion.LEVEL_5.midpoint();
        if (value > maxHeight) {
            float steepnessModifier = 1.0F;
            float delta = (value - maxHeight) * steepnessModifier;
            float range = limit - maxHeight;
            float alpha = delta / range;
            if (alpha > 0.925F) {
                cell.terrain = this.inner;
            }
            cell.terrain = TerrainType.VOLCANO_PIPE;
            value = maxHeight - maxHeight / 5.0F * alpha;
            cell.volcanoHeightThreshold = this.levels.scale(maxHeight);
        } else if (value < this.blendLower) {
            value += this.lowlands.compute(x, z, 0);
            cell.terrain = this.outer;
        } else if (value < this.blendUpper) {
            float alpha2 = 1.0F - (value - this.blendLower) / this.blendRange;
            value += this.lowlands.compute(x, z, 0) * alpha2;
            cell.terrain = this.outer;
        }
        cell.height = this.bias + value;
    }
    
    public static void modifyVolcanoType(Cell cell, Levels levels) {
        if (cell.terrain == TerrainType.VOLCANO_PIPE && (cell.height < levels.water || cell.riverDistance < 0.85F)) {
            cell.terrain = TerrainType.VOLCANO;
        }
    }
}
