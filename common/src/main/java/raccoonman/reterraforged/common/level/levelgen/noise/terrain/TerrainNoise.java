/*
 * MIT License
 *
 * Copyright (c) 2021 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raccoonman.reterraforged.common.level.levelgen.noise.terrain;

import net.minecraft.world.level.levelgen.DensityFunction;
import raccoonman.reterraforged.common.level.levelgen.continent.ContinentNoise;
import raccoonman.reterraforged.common.level.levelgen.continent.ContinentPoints;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseData;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class TerrainNoise implements TerrainSampler, DensityFunction.SimpleFunction {
    protected static final int OCEAN_OFFSET = 8763214;
    protected static final int TERRAIN_OFFSET = 45763218;
    protected static final int CONTINENT_OFFSET = 18749560;
    protected final float heightMultiplier = 1.2F;

    protected final int seed;
    protected final NoiseLevels levels;
    protected final Noise ocean;
    protected final TerrainBlender terrain;
    protected final ContinentNoise continent;
    protected final ThreadLocal<NoiseData> localChunk = ThreadLocal.withInitial(NoiseData::new);
    protected final ThreadLocal<SampleCache> localSample = ThreadLocal.withInitial(SampleCache::new);
    
    public TerrainNoise(Seed seed, WorldSettings settings, NoiseLevels levels, TerrainBlender terrain) {
    	this.seed = seed.get();
    	this.levels = levels;
        this.ocean = createOceanTerrain();
        this.terrain = terrain;
        this.continent = createContinentNoise(levels, settings);
    }

    public ContinentNoise getContinent() {
        return this.continent;
    }
//
//    public void generate(int chunkX, int chunkZ, Consumer<NoiseData> consumer) {
//        var noiseData = this.localChunk.get();
//        var blender = this.terrain.getBlenderResource();
//        var sample = noiseData.sample();
//
//        int startX = chunkX << 4;
//        int startZ = chunkZ << 4;
//        for (int dz = -1; dz < 17; dz++) {
//            for (int dx = -1; dx < 17; dx++) {
//                int x = startX + dx;
//                int z = startZ + dz;
//
//                this.sample(x, z, sample, blender);
//
//                noiseData.setNoise(dx, dz, sample);
//            }
//        }
//
//        consumer.accept(noiseData);
//    }

    public TerrainBlender.Local getBlenderResource() {
        return this.terrain.getBlenderResource();
    }

    public void sample(int x, int z, TerrainSample sample) {
        var blender = this.terrain.getBlenderResource();
        this.sample(x, z, sample, blender);
    }
    
    public TerrainSample sample(int x, int z, TerrainSample sample, TerrainBlender.Local blender) {
        float nx = getNoiseCoord(x);
        float nz = getNoiseCoord(z);
        this.sampleTerrain(nx, nz, sample, blender);
        this.continent.sampleRiver(nx, nz, sample, this.seed);
        return sample;
    }

    protected TerrainSample sampleTerrain(float x, float z, TerrainSample sample, TerrainBlender.Local blender) {
    	this.continent.sampleContinent(x, z, sample, this.seed);
    	
        float continentNoise = sample.continentNoise;
        if (continentNoise < ContinentPoints.SHALLOW_OCEAN) {
        	this.getOcean(x, z, sample, blender);
        } else if (continentNoise < ContinentPoints.COAST) {
        	this.getBlend(x, z, sample, blender);
        } else {
        	this.getInland(x, z, sample, blender);
        }

        return sample;
    }

    private void getOcean(float x, float z, TerrainSample sample, TerrainBlender.Local blender) {
        float rawNoise = this.ocean.getValue(x, z, this.seed);

        sample.heightNoise = this.levels.toDepthNoise(rawNoise);
    }

    private void getInland(float x, float z, TerrainSample sample, TerrainBlender.Local blender) {
        float heightNoise = terrain.getValue(x, z, this.seed, blender) * this.heightMultiplier;

        sample.heightNoise = this.levels.toHeightNoise(sample.baseNoise, heightNoise);
    }

    private void getBlend(float x, float z, TerrainSample sample, TerrainBlender.Local blender) {
    	if (sample.continentNoise < ContinentPoints.BEACH) {
            float lowerRaw = ocean.getValue(x, z, this.seed);
            float lower = this.levels.toDepthNoise(lowerRaw);

            float upper = this.levels.heightMin;
            float alpha = (sample.continentNoise - ContinentPoints.SHALLOW_OCEAN) / (ContinentPoints.BEACH - ContinentPoints.SHALLOW_OCEAN);

            sample.heightNoise = NoiseUtil.lerp(lower, upper, alpha);
        } else if (sample.continentNoise < ContinentPoints.COAST) {
            float lower = this.levels.heightMin;

            float upperRaw = this.terrain.getValue(x, z, this.seed, blender) * this.heightMultiplier;
            float upper = this.levels.toHeightNoise(sample.baseNoise, upperRaw);

            float alpha = (sample.continentNoise - ContinentPoints.BEACH) / (ContinentPoints.COAST - ContinentPoints.BEACH);

            sample.heightNoise = NoiseUtil.lerp(lower, upper, alpha);
        }
    }
 
    public float getNoiseCoord(int coord) {
        return coord * this.levels().frequency;
    }

    private static Noise createOceanTerrain() {
        return Source.simplex(64, 3).shift(OCEAN_OFFSET).scale(0.4);
    }
    
    private static ContinentNoise createContinentNoise(NoiseLevels levels, WorldSettings settings) {
        return new ContinentNoise(levels, settings);
    }

	@Override
	public NoiseLevels levels() {
		return this.levels;
	}

	@Override
	public float getBase(int x, int z) {
		return this.levels.getScaledBase(this.sample(x, z).baseNoise);
	}

	@Override
	public float getHeight(int x, int z) {
		return this.levels.getScaledHeight(this.sample(x, z).heightNoise);
	}

	@Override
	public float getWater(int x, int z) {
		return this.sample(x, z).waterNoise;
	}

	@Override
	public float getGradient(int x, int z) {
		SampleCache sample = this.localSample.get();
		
        float n = this.sample(x, z - 1, sample).heightNoise;
        float s = this.sample(x, z + 1, sample).heightNoise;
        float e = this.sample(x + 1, z, sample).heightNoise;
        float w = this.sample(x - 1, z, sample).heightNoise;

        float dx = e - w;
        float dz = s - n;
        float grad = NoiseUtil.sqrt(dx * dx + dz * dz);
        return NoiseUtil.clamp(grad, TerrainSampler.MIN, TerrainSampler.MAX);
	}

	private TerrainSample sample(int x, int z) {
		return this.sample(x, z, this.localSample.get());
	}
	
	private TerrainSample sample(int x, int z, SampleCache sample) {
		TerrainSample terrain = sample.sample;
		if(sample.x != x || sample.z != z) {
			sample.x = x;
			sample.z = z;
			this.sample(x, z, terrain);
		}
		return terrain;
	}
	
	private class SampleCache {
		public final TerrainSample sample = new TerrainSample();
		public int x = Integer.MIN_VALUE; 
		public int z = Integer.MIN_VALUE;
	}
}
