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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import raccoonman.reterraforged.common.noise.util.NoiseUtil;
import raccoonman.reterraforged.common.util.CodecUtil;

public record SumViability(float initial, Viability[] rules, float[] amounts) implements Viability {
    public static final Codec<SumViability> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Codec.FLOAT.optionalFieldOf("initial", 1.0F).forGetter(SumViability::initial),
    	CodecUtil.forArray(Viability.CODEC, Viability[]::new).fieldOf("rules").forGetter(SumViability::rules),
    	CodecUtil.forArray(Codec.FLOAT, Float[]::new).xmap(ArrayUtils::toPrimitive, ArrayUtils::toObject).fieldOf("amounts").forGetter(SumViability::amounts)
    ).apply(instance, SumViability::new));
    		
    @Override
    public float getFitness(int x, int z, Context context) {
        float sumValue = this.initial;
        for (int i = 0; i < this.rules.length; i++) {
            float value = this.rules[i].getFitness(x, z, context);
            float weight = this.amounts[i];
            sumValue += value * weight;
        }
        return NoiseUtil.clamp(sumValue, 0, 1);
    }

	@Override
	public Codec<SumViability> codec() {
		return CODEC;
	}

    public static Builder builder(float initial) {
        return new Builder(initial);
    }

    public static class Builder {
        private final float initial;
        private final List<Viability> viabilities = new ArrayList<>();
        private final FloatList weights = new FloatArrayList();

        public Builder(float initial) {
            this.initial = initial;
        }

        public Builder with(float weight, Viability viability) {
        	this.viabilities.add(viability);
        	this.weights.add(weight);
            return this;
        }

        public SumViability build() {
            return new SumViability(this.initial, this.viabilities.toArray(new Viability[0]), this.weights.toFloatArray());
        }
    }
}
