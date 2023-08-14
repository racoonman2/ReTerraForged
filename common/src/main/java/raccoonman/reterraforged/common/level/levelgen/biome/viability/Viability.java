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

import java.util.Arrays;
import java.util.function.Function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.Lifecycle;

import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import raccoonman.reterraforged.common.level.levelgen.climate.ClimateNoise;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseSample;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainData;
import raccoonman.reterraforged.common.level.levelgen.terrain.TerrainNoise;
import raccoonman.reterraforged.common.registries.RTFRegistries;
import raccoonman.reterraforged.common.util.CodecUtil;

public interface Viability {
    Codec<Viability> CODEC = CodecUtil.forRegistry(RTFRegistries.VIABILITY_TYPE, Lifecycle.stable(), Viability::codec, Function.identity());
    
    float getFitness(int x, int z, Context context);

    Codec<? extends Viability> codec();
    
    default float getScaler(int maxY) {
        return maxY / 255F;
    }

    default Viability mult(Viability... others) {
        var copy = Arrays.copyOf(others, others.length + 1);
        copy[others.length] = this;
        return new MulViability(copy);
    }

    interface Context {
        int seed();

        boolean edge();

        Holder<NoiseGeneratorSettings> getSettings();

        TerrainData getTerrain();

        TerrainNoise getTerrainNoise();
        
        NoiseSample sample();
        
        ClimateNoise getClimateNoise();
    }

    static float getFallOff(float value, float max) {
        return value < max ? 1F - (value / max) : 0F;
    }

    static float getFallOff(float value, float min, float mid, float max) {
        if (value < min) return 0F;
        if (value < mid) return (value - min) / (mid - min);
        if (value < max) return 1F - ((value - mid) / (max - mid));
        return 0F;
    }
}
