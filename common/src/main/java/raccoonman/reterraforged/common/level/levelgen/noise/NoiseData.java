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

import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainSample;
import raccoonman.reterraforged.common.util.storage.FloatMap;
import raccoonman.reterraforged.common.util.storage.Index;

public class NoiseData {
    public static final int BORDER = 1;
    public static final int MIN = -BORDER;
    public static final int MAX = 16 + BORDER;

    public final TerrainSample sample = new TerrainSample();
    public final FloatMap height = new FloatMap(BORDER);
    public final FloatMap river = new FloatMap(BORDER);
    public final FloatMap base = new FloatMap(BORDER);

    public int min() {
        return MIN;
    }

    public int max() {
        return MAX;
    }

    public Index index() {
        return height.index();
    }

    public TerrainSample getSample() {
        return sample;
    }

    public FloatMap getHeight() {
        return height;
    }

    public FloatMap getBase() {
        return base;
    }

    public FloatMap getRiver() {
        return river;
    }

    public void setNoise(int x, int z, TerrainSample sample) {
        int index = index().of(x, z);
        setNoise(index, sample);
    }

    public void setNoise(int index, TerrainSample sample) {
        height.set(index, sample.heightNoise);
        base.set(index, sample.baseNoise);
        river.set(index, sample.riverNoise);
    }

    public static boolean isInsideChunk(int x, int z) {
        return x >= -1 && x <= 16 && z >= -1 && z <= 16;
    }
}
