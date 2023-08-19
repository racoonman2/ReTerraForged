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

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseData;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.storage.FloatMap;

public record TerrainCache(NoiseLevels levels, FloatMap base, FloatMap height, FloatMap gradient, FloatMap water) implements TerrainSampler {

	public TerrainCache(NoiseLevels levels) {
		this(levels, new FloatMap(), new FloatMap(), new FloatMap(), new FloatMap());
	}
    
	@Override
	public float getBase(int x, int z) {
		return this.base.get(x, z);
	}

	@Override
	public float getHeight(int x, int z) {
		return this.height.get(x, z);
	}

	@Override
	public float getWater(int x, int z) {
		return this.water.get(x, z);
	}
    
	@Override
	public float getGradient(int x, int z) {
		return this.gradient.get(x, z);
	}
	
    public void fill(NoiseData noiseData) {
        FloatMap base = noiseData.base();
        FloatMap height = noiseData.height();

        for (int i = 0; i < 16 * 16; i++) {
            int x = i & 15;
            int z = i >> 4;
	        float n = height.get(x, z - 1);
	        float s = height.get(x, z + 1);
	        float e = height.get(x + 1, z);
	        float w = height.get(x - 1, z);
	        
	        float dx = e - w;
	        float dz = s - n;
	        float gradient = NoiseUtil.sqrt(dx * dx + dz * dz);
	        this.base.set(x, z, this.levels.getScaledBase(base.get(x, z)));
	        this.height.set(x, z, this.levels.getScaledHeight(height.get(x, z)));
	        this.gradient.set(x, z, NoiseUtil.clamp(gradient, TerrainSampler.MIN, TerrainSampler.MAX));
	        this.water.set(x, z, noiseData.water().get(x, z));
        }
    }
}