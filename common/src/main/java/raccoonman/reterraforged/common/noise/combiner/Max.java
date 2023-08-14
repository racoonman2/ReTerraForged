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

package raccoonman.reterraforged.common.noise.combiner;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.noise.Noise;
import raccoonman.reterraforged.common.util.CodecUtil;

/**
 * @author dags <dags@dags.me>
 */
public class Max extends Combiner {
	public static final Codec<Max> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		CodecUtil.forArray(Noise.DIRECT_CODEC, Noise[]::new).fieldOf("modules").forGetter((c) -> c.sources)
	).apply(instance, Max::new));

    public Max(Noise... modules) {
        super(modules);
    }

    @Override
    protected float minTotal(float total, Noise next) {
        return maxTotal(total, next);
    }

    @Override
    protected float maxTotal(float total, Noise next) {
        return Math.max(total, next.maxValue());
    }

    @Override
    protected float combine(float total, float value) {
        return Math.max(total, value);
    }

    @Override
	public Codec<Max> codec() {
		return CODEC;
	}
}
