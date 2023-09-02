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
import raccoonman.reterraforged.common.level.levelgen.noise.domain.Domain;

/**
 * @author dags <dags@dags.me>
 */
public class Warp extends Modifier {
	public static final Codec<Warp> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Noise.HOLDER_HELPER_CODEC.fieldOf("source").forGetter((m) -> m.source),
		Domain.CODEC.fieldOf("domain").forGetter((m) -> m.domain)
	).apply(instance, Warp::new));
	
    private final Domain domain;

    public Warp(Noise source, Domain domain) {
        super(source);
        this.domain = domain;
    }
    
	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new Warp(this.source.mapAll(visitor), this.domain));
	}
    
    @Override
    public float compute(float x, float y, int seed) {
        return source.compute(domain.getX(x, y, seed), domain.getY(x, y, seed), seed);
    }

    @Override
    public float modify(float x, float y, float noiseValue, int seed) {
        // not used
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        Warp warp = (Warp) o;

        return domain.equals(warp.domain);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + domain.hashCode();
        return result;
    }
    
    @Override
	public Codec<Warp> codec() {
		return CODEC;
	}
}
