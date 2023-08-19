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

package raccoonman.reterraforged.common.level.levelgen.noise;

import raccoonman.reterraforged.common.level.levelgen.noise.terrain.TerrainSample;
import raccoonman.reterraforged.common.util.storage.FloatMap;

public record NoiseData(FloatMap base, FloatMap height, FloatMap water) {
    public static final int BORDER = 1;
    public static final int MIN = -BORDER;
    public static final int MAX = 16 + BORDER;

    public NoiseData() {
    	this(new FloatMap(BORDER), new FloatMap(BORDER), new FloatMap(BORDER));
    }
    
    public void setNoise(int x, int z, float base, float height, float water) {
        int index = this.base.index().of(x, z);
        this.setNoise(index, base, height, water);
    }
    
    public void setNoise(int index, float base, float height, float water) {
    	this.base.set(index, base);
    	this.height.set(index, height);
    	this.water.set(index, water);
    }

    public void setNoise(int x, int z, TerrainSample sample) {
        int index = this.base.index().of(x, z);
        this.setNoise(index, sample);
    }
    
    public void setNoise(int index, TerrainSample sample) {
        this.base.set(index, sample.baseNoise);
        this.height.set(index, sample.heightNoise);
        this.water.set(index, sample.waterNoise);
    }

    public static boolean isInsideChunk(int x, int z) {
        return x >= -1 && x <= 16 && z >= -1 && z <= 16;
    }
}
