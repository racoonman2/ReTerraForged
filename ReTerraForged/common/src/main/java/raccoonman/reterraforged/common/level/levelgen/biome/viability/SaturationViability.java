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

public record SaturationViability(float min, float max) implements Viability {
    public static final Codec<SaturationViability> CODEC = RecordCodecBuilder.create(instance -> instance.group(
    	Codec.FLOAT.optionalFieldOf("min", 0.0F).forGetter(SaturationViability::min),
    	Codec.FLOAT.optionalFieldOf("max", 1.0F).forGetter(SaturationViability::max)
    ).apply(instance, SaturationViability::new));
	
    public SaturationViability(float max) {
        this(0, max);
    }

    @Override
    public float getFitness(int x, int z, Context context) {
        // Water mask represents distance from river/lake so invert to get saturation
        float saturation = 1f - context.getTerrain().getRiver().get(x, z);

        if (saturation < this.min) return 0F;
        if (saturation > this.max) return 1F;

        return (saturation - this.min) / (this.max - this.min);
    }

	@Override
	public Codec<SaturationViability> codec() {
		return CODEC;
	}
}
