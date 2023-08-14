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

package raccoonman.reterraforged.common.level.levelgen.terrain;

import java.util.function.Consumer;

import raccoonman.reterraforged.common.level.levelgen.continent.ContinentGenerator;
import raccoonman.reterraforged.common.level.levelgen.continent.ContinentPoints;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseData;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.level.levelgen.settings.WorldSettings;
import raccoonman.reterraforged.common.level.levelgen.util.Seed;
import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.noise.Source;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class TerrainNoise {
    protected static final int OCEAN_OFFSET = 8763214;
    protected static final int TERRAIN_OFFSET = 45763218;
    protected static final int CONTINENT_OFFSET = 18749560;
    protected final float heightMultiplier = 1.2F;

    protected final int seed;
    protected final NoiseLevels levels;
    protected final Noise ocean;
    protected final TerrainBlender terrain;
    protected final ContinentGenerator continent;
    protected final ThreadLocal<NoiseData> localChunk = ThreadLocal.withInitial(NoiseData::new);

    public TerrainNoise(Seed seed, WorldSettings settings, NoiseLevels levels, TerrainBlender terrain) {
    	this.seed = seed.get();
    	this.levels = levels;
        this.ocean = createOceanTerrain();
        this.terrain = terrain;
        this.continent = createContinentNoise(seed, settings, levels);
    }

    public NoiseLevels getLevels() {
        return levels;
    }

    public ContinentGenerator getContinent() {
        return continent;
    }

    public void generate(int chunkX, int chunkZ, Consumer<NoiseData> consumer) {
        var noiseData = localChunk.get();
        var blender = terrain.getBlenderResource();
        var sample = noiseData.sample;

        int startX = chunkX << 4;
        int startZ = chunkZ << 4;
        for (int dz = -1; dz < 17; dz++) {
            for (int dx = -1; dx < 17; dx++) {
                int x = startX + dx;
                int z = startZ + dz;

                sample(x, z, sample, blender);

                noiseData.setNoise(dx, dz, sample);
            }
        }

        consumer.accept(noiseData);
    }

    public TerrainBlender.LocalBlender getBlenderResource() {
        return terrain.getBlenderResource();
    }

    public void sample(int x, int z, TerrainSample sample) {
        var blender = terrain.getBlenderResource();
        sample(x, z, sample, blender);
    }
    
    public TerrainSample sample(int x, int z, TerrainSample sample, TerrainBlender.LocalBlender blender) {
        float nx = getNoiseCoord(x);
        float nz = getNoiseCoord(z);
        sampleTerrain(nx, nz, sample, blender);
        this.continent.sampleRiver(nx, nz, sample, this.seed);
        return sample;
    }

    protected TerrainSample sampleTerrain(float x, float z, TerrainSample sample, TerrainBlender.LocalBlender blender) {
    	this.continent.sampleContinent(x, z, sample, this.seed);
    	
        float continentNoise = sample.continentNoise;
        if (continentNoise < ContinentPoints.SHALLOW_OCEAN) {
            getOcean(x, z, sample, blender);
        } else if (continentNoise < ContinentPoints.COAST) {
            getBlend(x, z, sample, blender);
        } else {
            getInland(x, z, sample, blender);
        }

        return sample;
    }

    private void getOcean(float x, float z, TerrainSample sample, TerrainBlender.LocalBlender blender) {
        float rawNoise = ocean.getValue(x, z, this.seed);

        sample.heightNoise = levels.toDepthNoise(rawNoise);
    }

    private void getInland(float x, float z, TerrainSample sample, TerrainBlender.LocalBlender blender) {
        float baseNoise = sample.baseNoise;
        float heightNoise = terrain.getValue(x, z, this.seed, blender) * heightMultiplier;

        sample.heightNoise = this.levels.toHeightNoise(baseNoise, heightNoise);
    }

    private void getBlend(float x, float z, TerrainSample sample, TerrainBlender.LocalBlender blender) {
        if (sample.continentNoise < ContinentPoints.BEACH) {
            float lowerRaw = ocean.getValue(x, z, this.seed);
            float lower = this.levels.toDepthNoise(lowerRaw);

            float upper = this.levels.heightMin;
            float alpha = (sample.continentNoise - ContinentPoints.SHALLOW_OCEAN) / (ContinentPoints.BEACH - ContinentPoints.SHALLOW_OCEAN);

            sample.heightNoise = NoiseUtil.lerp(lower, upper, alpha);
        } else if (sample.continentNoise < ContinentPoints.COAST) {
            float lower = this.levels.heightMin;

            float baseNoise = sample.baseNoise;
            float upperRaw = terrain.getValue(x, z, this.seed, blender) * heightMultiplier;
            float upper = this.levels.toHeightNoise(baseNoise, upperRaw);

            float alpha = (sample.continentNoise - ContinentPoints.BEACH) / (ContinentPoints.COAST - ContinentPoints.BEACH);

            sample.heightNoise = NoiseUtil.lerp(lower, upper, alpha);
        }
    }
 
    public float getNoiseCoord(int coord) {
        return coord * this.getLevels().frequency;
    }

    private static Noise createOceanTerrain() {
        return Source.simplex(64, 3).shift(OCEAN_OFFSET).scale(0.4);
    }
    
    private static ContinentGenerator createContinentNoise(Seed seed, WorldSettings settings, NoiseLevels levels) {
        return new ContinentGenerator(seed.offset(CONTINENT_OFFSET), levels, settings);
    }
}
