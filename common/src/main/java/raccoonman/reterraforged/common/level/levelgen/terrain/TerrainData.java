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

import raccoonman.reterraforged.common.level.levelgen.noise.NoiseData;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.storage.FloatMap;

public class TerrainData implements Consumer<NoiseData> {
    protected final TerrainLevels levels;
    protected final FloatMap height = new FloatMap();
    protected final FloatMap gradient = new FloatMap();
    protected final FloatMap river = new FloatMap();
    protected final FloatMap baseHeight = new FloatMap();

    protected float min = Float.MAX_VALUE;
    protected float max = Float.MIN_VALUE;
    protected float maxBase = Float.MIN_VALUE;

    public TerrainData(TerrainLevels levels) {
        this.levels = levels;
    }

    public int getMin() {
        return levels.getHeight(min);
    }

    public int getMax() {
        return levels.getHeight(max);
    }

    public int getMaxBase() {
        return levels.getHeight(maxBase);
    }

    public int getHeight(int x, int z) {
        float scaledHeight = height.get(x, z);
        return levels.getHeight(scaledHeight);
    }

    public int getBaseHeight(int x, int z) {
        float scaledLevel = baseHeight.get(x, z);
        return levels.getHeight(scaledLevel);
    }

    public TerrainLevels getLevels() {
        return levels;
    }

    public FloatMap getHeight() {
        return height;
    }

    public FloatMap getBaseHeight() {
        return baseHeight;
    }

    public FloatMap getGradient() {
        return gradient;
    }

    public FloatMap getRiver() {
        return river;
    }

    public float getGradient(int x, int z, float norm) {
        float grad = getGradient().get(x, z);
        return NoiseUtil.clamp(grad * norm, 0, 1);
    }

    @Override
    public void accept(NoiseData noiseData) {
        var basemap = noiseData.getBase();
        var heightmap = noiseData.getHeight();

        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                float heightNoise = heightmap.get(x, z);
                float baseLevelNoise = basemap.get(x, z);
                float scaledHeight = levels.getScaledHeight(heightNoise);
                float scaledBaseLevel = levels.getScaledBaseLevel(baseLevelNoise);

                float n = heightmap.get(x, z - 1);
                float s = heightmap.get(x, z + 1);
                float e = heightmap.get(x + 1, z);
                float w = heightmap.get(x - 1, z);

                float dx = e - w;
                float dz = s - n;
                float grad = NoiseUtil.sqrt(dx * dx + dz * dz);
                float noiseGrad = NoiseUtil.clamp(grad, 0, 1);

                gradient.set(x, z, noiseGrad);
                height.set(x, z, scaledHeight);
                baseHeight.set(x, z, scaledBaseLevel);
                river.set(x, z, noiseData.getRiver().get(x, z));
                max = Math.max(max, scaledHeight);
                min = Math.min(min, scaledHeight);
                maxBase = Math.max(maxBase, scaledBaseLevel);
            }
        }
    }
}
