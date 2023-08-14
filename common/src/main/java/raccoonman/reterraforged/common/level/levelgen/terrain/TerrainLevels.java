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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.level.dimension.DimensionType;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseLevels;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.MathUtil;

@Deprecated
// this needs a rework since we used NoiseGeneratorSettings
// otherwise we'll have two different sea levels and shit like that
public class TerrainLevels {
    public static final Codec<TerrainLevels> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Codec.BOOL.optionalFieldOf("auto_scale", true).forGetter(l -> l.noiseLevels.auto),
    	Codec.floatRange(0.0F, 10.0F).optionalFieldOf("horizontal_scale", 1F).forGetter(l -> l.noiseLevels.scale),
    	Codec.intRange(Limits.MIN_MIN_Y, Limits.MAX_MIN_Y).fieldOf("min_y").forGetter(l -> l.minY),
    	Codec.intRange(Limits.MIN_MAX_Y, Limits.MAX_MAX_Y).fieldOf("max_y").forGetter(l -> l.maxY),
    	Codec.intRange(Limits.MAX_MIN_Y, Limits.MAX_MAX_Y).fieldOf("base_height").forGetter(l -> l.baseHeight),
    	Codec.intRange(Limits.MIN_SEA_LEVEL, Limits.MAX_SEA_LEVEL).fieldOf("sea_level").forGetter(l -> l.seaLevel),
    	Codec.intRange(Limits.MIN_SEA_FLOOR, Limits.MAX_SEA_FLOOR).fieldOf("sea_floor").forGetter(l -> l.seaFloor)
    ).apply(instance, TerrainLevels::new));

    public static final TerrainLevels DEFAULT = new TerrainLevels(true, Defaults.SCALE, Defaults.MIN_Y, Defaults.MAX_Y, Defaults.MAX_BASE_HEIGHT, Defaults.SEA_LEVEL, Defaults.SEA_FLOOR);

    private final int minY;
    private final int maxY; // Exclusive max block index
    private final int baseHeight;
    private final int seaFloor;
    private final int seaLevel; // Inclusive index of highest water block
    private final NoiseLevels noiseLevels;

    public TerrainLevels(boolean autoScale, float scale, int minY, int maxY, int baseHeight, int seaLevel, int seaFloor) {
        this.minY = MathUtil.clamp(minY, Limits.MIN_MIN_Y, Limits.MAX_MIN_Y);
        this.maxY = MathUtil.clamp(maxY, Limits.MIN_MAX_Y, Limits.MAX_MAX_Y);
        this.seaLevel = MathUtil.clamp(seaLevel, Limits.MIN_SEA_LEVEL, maxY >> 1);
        this.seaFloor = MathUtil.clamp(seaFloor, this.minY, this.seaLevel - 1);
        this.baseHeight = MathUtil.clamp(baseHeight, this.seaLevel, this.maxY);
        this.noiseLevels = new NoiseLevels(autoScale, scale, this.seaLevel, this.seaFloor, this.maxY, this.baseHeight);
    }
    
    public NoiseLevels getNoiseLevels() {
    	return this.noiseLevels;
    }
    
    public float getScaledHeight(float heightNoise) {
        return heightNoise * this.maxY;
    }

    public float getScaledBaseLevel(float waterLevelNoise) {
        return this.noiseLevels.toHeightNoise(waterLevelNoise, 0f) * this.maxY;
    }

    public int getHeight(float scaledHeight) {
        return NoiseUtil.floor(scaledHeight);
    }

    @Override
    public String toString() {
        return "TerrainLevels{" +
                "minY=" + this.minY +
                ", maxY=" + this.maxY +
                ", seaFloor=" + this.seaFloor +
                ", seaLevel=" + this.seaLevel +
                ", noiseLevels=" + this.noiseLevels +
                '}';
    }

    public static int getWaterLevel(int x, int z, int seaLevel, TerrainData terrainData) {
        return terrainData.getRiver().get(x, z) == 0.0F ? terrainData.getBaseHeight(x, z) : seaLevel;
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

    public static class Defaults {
        public static final float SCALE = 1;
        public static final int MIN_Y = -64;
        public static final int MAX_Y = 480;
        public static final int MAX_BASE_HEIGHT = 128;
        public static final int SEA_LEVEL = 62;
        public static final int SEA_FLOOR = SEA_LEVEL - 40;
        public static final int LEGACY_GEN_DEPTH = 256;
    }
}
