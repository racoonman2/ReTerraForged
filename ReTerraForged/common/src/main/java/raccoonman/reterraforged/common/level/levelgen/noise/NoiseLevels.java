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

import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainLevels;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class NoiseLevels {
    public final boolean auto;
    public final float scale;
    public final float unit;

    public final float depthMin;
    public final float depthRange;

    public final float heightMin;
    public final float baseRange;
    public final float heightRange;

    public final float frequency;

    public NoiseLevels(boolean autoScale, float scale, int seaLevel, int seaFloor, int worldHeight, int baseHeight) {
        this.auto = autoScale;
        this.scale = scale;
        this.unit = 1.0f / worldHeight;
        this.depthMin = seaFloor / (float) worldHeight;
        this.heightMin = seaLevel / (float) worldHeight;
        this.baseRange = baseHeight / (float) worldHeight;
        this.heightRange = 1F - (heightMin + baseRange);
        this.depthRange = heightMin - depthMin;
        this.frequency = calcFrequency(worldHeight - seaLevel, auto, scale);

        ReTerraForged.LOGGER.debug("Sea Level: {}, Base Height: {}, World Height: {}", seaLevel, baseHeight, worldHeight);
    }
    
    public float toCoord(float coord) {
    	return coord * this.frequency;
    }

    public float scale(int level) {
        return level * unit;
    }

    public float toDepthNoise(float noise) {
        return depthMin + noise * depthRange;
    }

    public float toHeightNoise(float baseNoise, float heightNoise) {
        return heightMin + baseRange * baseNoise + heightRange * heightNoise;
    }

    public float floor(float value) {
        return NoiseUtil.floor(value / unit) * unit;
    }

    public float ceil(float value) {
        return unit + NoiseUtil.floor(value / unit) * unit;
    }
    
    public static float calcFrequency(int verticalRange, boolean auto, float scale) {
        scale = scale <= 0F ? 1 : scale;

        if (!auto) return scale;

        float frequency = (TerrainLevels.Defaults.LEGACY_GEN_DEPTH - TerrainLevels.Defaults.SEA_LEVEL) / (float) verticalRange;

        return frequency * scale;
    }
}
