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

import net.minecraft.world.level.dimension.DimensionType;
import raccoonman.reterraforged.common.ReTerraForged;
import raccoonman.reterraforged.common.util.MathUtil;

@Deprecated(forRemoval = true)
public class NoiseLevels {
	public final boolean auto;
    public final float scale;
    public final int seaLevel;
    public final float unit;

    public final float depthMin;
    public final float depthRange;

    public final float heightMin;
    public final float baseRange;
    public final float heightRange;
    public final int maxY;
    
    public final float frequency;

    public NoiseLevels(boolean autoScale, float scale, int seaLevel, int seaFloor, int maxY, int baseHeight) {
        this.auto = autoScale;
        this.scale = scale;
        this.seaLevel = seaLevel;
        this.unit = 1.0F / maxY;
        this.depthMin = seaFloor / (float) maxY;
        this.heightMin = seaLevel / (float) maxY;
        this.baseRange = baseHeight / (float) maxY;
        this.heightRange = 1F - (heightMin + baseRange);
        this.maxY = maxY;
        this.depthRange = heightMin - depthMin;
        this.frequency = calcFrequency(maxY - seaLevel, auto, scale, seaLevel);

        ReTerraForged.LOGGER.debug("Sea Level: {}, Base Height: {}, World Height: {}", seaLevel, baseHeight, maxY);
    }

    private static float calcFrequency(int verticalRange, boolean auto, float scale, int seaLevel) {
        scale = scale <= 0.0F ? 1.0F : scale;

        if (!auto) {
        	return scale;
        }

        // TODO dont hardcode 256; we only use it for compatibility reasons
        float frequency = (256 - seaLevel) / (float) verticalRange;
        return frequency * scale;
    }
    
    public static NoiseLevels create(boolean autoScale, float scale, int minY, int maxY, int baseHeight, int seaDepth, int seaLevel) {
        int cminY = MathUtil.clamp(minY, Limits.MIN_MIN_Y, Limits.MAX_MIN_Y);
        int cmaxY = MathUtil.clamp(maxY, Limits.MIN_MAX_Y, Limits.MAX_MAX_Y);
        int cseaLevel = MathUtil.clamp(seaLevel, Limits.MIN_SEA_LEVEL, maxY >> 1);
        int cseaFloor = MathUtil.clamp(seaLevel - seaDepth, cminY, cseaLevel - 1);
        int cbaseHeight = MathUtil.clamp(baseHeight, cseaLevel, cmaxY);
        return new NoiseLevels(autoScale, scale, cseaLevel, cseaFloor, cmaxY, cbaseHeight);
    }

    public static class Limits {
        public static final int MIN_MIN_Y = DimensionType.MIN_Y;
        public static final int MAX_MIN_Y = 0;
        public static final int MIN_SEA_LEVEL = 32;
        public static final int MAX_SEA_LEVEL = DimensionType.Y_SIZE;
        public static final int MIN_SEA_FLOOR = 0;
        public static final int MAX_SEA_FLOOR = MAX_SEA_LEVEL;
        public static final int MIN_MAX_Y = 128;
        public static final int MAX_MAX_Y = DimensionType.Y_SIZE;
    }
}
