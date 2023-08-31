/*
 *
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
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

package raccoonman.reterraforged.common.level.levelgen.noise.modifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

public class PowerCurve extends Modifier {
	public static final Codec<PowerCurve> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Codec.FLOAT.optionalFieldOf("power", 1.0F).forGetter((m) -> m.power)
	).apply(instance, PowerCurve::new));

    private final float min;
    private final float max;
    private final float mid;
    private final float range;
    private final float power;

    public PowerCurve(Noise source, float power) {
        super(source);
        float min = source.minValue();
        float max = source.maxValue();
        float mid = min + ((max - min) / 2F);
        this.power = power;
        this.min = mid - NoiseUtil.pow(mid - source.minValue(), power);
        this.max = mid + NoiseUtil.pow(source.maxValue() - mid, power);
        this.range = this.max - this.min;
        this.mid = this.min + (range / 2F);
    }
    
    @Override
    public float modify(float x, float y, float value, int seed) {
        if (value >= mid) {
            float part = value - mid;
            value = mid + NoiseUtil.pow(part, power);
        } else {
            float part = mid - value;
            value = mid - NoiseUtil.pow(part, power);
        }
        return NoiseUtil.map(value, min, max, range);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PowerCurve that = (PowerCurve) o;

        if (Float.compare(that.min, min) != 0) return false;
        if (Float.compare(that.max, max) != 0) return false;
        if (Float.compare(that.mid, mid) != 0) return false;
        if (Float.compare(that.range, range) != 0) return false;
        return Float.compare(that.power, power) == 0;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (min != +0.0f ? Float.floatToIntBits(min) : 0);
        result = 31 * result + (max != +0.0f ? Float.floatToIntBits(max) : 0);
        result = 31 * result + (mid != +0.0f ? Float.floatToIntBits(mid) : 0);
        result = 31 * result + (range != +0.0f ? Float.floatToIntBits(range) : 0);
        result = 31 * result + (power != +0.0f ? Float.floatToIntBits(power) : 0);
        return result;
    }
    
    @Override
	public Codec<PowerCurve> codec() {
		return CODEC;
	}
}
