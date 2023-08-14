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

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record SlopeViability(float normalize, float max) implements Viability {
    public static final Codec<SlopeViability> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Codec.FLOAT.optionalFieldOf("normalize", 1.0F).forGetter(SlopeViability::normalize),
    	Codec.FLOAT.optionalFieldOf("max", 1.0F).forGetter(SlopeViability::max)
    ).apply(instance, SlopeViability::new));

    @Override
    public float getFitness(int x, int z, Context context) {
        float norm = this.normalize * getScaler(context.getSettings().value().noiseSettings().height());
        float gradient = context.getTerrain().getGradient(x, z, norm);

        if (gradient >= this.max) return 1F;

        return gradient / this.max;
    }

	@Override
	public Codec<SlopeViability> codec() {
		return CODEC;
	}
}
