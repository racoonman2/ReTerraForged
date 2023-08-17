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

public record HeightViability(float minOffset, float midOffset, float maxOffset) implements Viability {
	public static final Codec<HeightViability> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("min", 0.0F).forGetter(HeightViability::minOffset),
		Codec.FLOAT.optionalFieldOf("mid", 0.5F).forGetter(HeightViability::midOffset),
		Codec.FLOAT.optionalFieldOf("max", 1.0F).forGetter(HeightViability::maxOffset)		
	).apply(instance, HeightViability::new));

	@Override
    public float getFitness(int x, int z, Context context) {
        int base = context.getTerrain().getBaseLevel(x, z);
        int height = context.getTerrain().getHeightLevel(x, z);

        var settings = context.getSettings();
        float scale = getScaler(settings.value().noiseSettings().height());
        float min = base + minOffset() * scale;
        float mid = base + midOffset() * scale;
        float max = base + maxOffset() * scale;

        if (height < min) return 1F;
        if (height > max) return 1F;

        if (height < mid) {
            return (mid - height) / (mid - min);
        }

        return (height - mid) / (max - mid);
    }
	
	@Override
	public Codec<HeightViability> codec() {
		return CODEC;
	}
}
