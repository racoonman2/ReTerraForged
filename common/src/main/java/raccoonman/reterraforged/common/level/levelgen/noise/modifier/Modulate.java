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

public class Modulate extends Modifier {
	public static final Codec<Modulate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Noise.HOLDER_HELPER_CODEC.fieldOf("direction").forGetter((m) -> m.direction),
		Noise.HOLDER_HELPER_CODEC.fieldOf("strength").forGetter((m) -> m.strength)
	).apply(instance, Modulate::new));
	
    private final Noise direction;
    private final Noise strength;

    public Modulate(Noise source, Noise direction, Noise strength) {
        super(source);
        this.direction = direction;
        this.strength = strength;
    }

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Modulate(this.source.mapAll(visitor), this.direction.mapAll(visitor), this.strength.mapAll(visitor)));
	}

    @Override
    public float compute(float x, float y, int seed) {
        float angle = direction.compute(x, y, seed) * NoiseUtil.PI2;
        float strength = this.strength.compute(x, y, seed);
        float dx = NoiseUtil.sin(angle) * strength;
        float dy = NoiseUtil.cos(angle) * strength;
        return source.compute(x + dx, y + dy, seed);
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Modulate modulate = (Modulate) o;

        if (!direction.equals(modulate.direction)) return false;
        return strength.equals(modulate.strength);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + direction.hashCode();
        result = 31 * result + strength.hashCode();
        return result;
    }
    
    @Override
	public Codec<Modulate> codec() {
		return CODEC;
	}
}
