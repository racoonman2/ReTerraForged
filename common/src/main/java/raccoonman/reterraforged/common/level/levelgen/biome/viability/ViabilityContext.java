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

package raccoonman.reterraforged.common.level.levelgen.biome.viability;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseSample;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainCache;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;

public class ViabilityContext implements Viability.Context {
    public int seed;
    public TerrainCache terrainData;
    public TerrainNoise terrainNoise;
    public ClimateNoise climateNoise;
    public NoiseSample sample = new NoiseSample();
    public Holder<NoiseGeneratorSettings> settings;
    
    @Override
    public int seed() {
        return seed;
    }

    @Override
    public boolean edge() {
        return false;
    }

    @Override
    public Holder<NoiseGeneratorSettings> getSettings() {
        return this.settings;
    }

    @Override
    public TerrainCache getTerrain() {
        return terrainData;
    }

    @Override
    public TerrainNoise getTerrainNoise() {
    	return terrainNoise;
    }
    
    @Override
    public ClimateNoise getClimateNoise() {
        return climateNoise;
    }

	@Override
	public NoiseSample sample() {
		return this.sample;
	}
}
