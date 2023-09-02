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

package raccoonman.reterraforged.common.level.levelgen.noise.selector;

import java.util.List;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import raccoonman.reterraforged.common.level.levelgen.noise.Noise;
import raccoonman.reterraforged.common.level.levelgen.noise.Noise.Visitor;
import raccoonman.reterraforged.common.level.levelgen.noise.curve.Interpolation;
import raccoonman.reterraforged.common.level.levelgen.noise.util.NoiseUtil;

/**
 * @Author <dags@dags.me>
 */
public class MultiBlend extends Selector {
	public static final Codec<MultiBlend> CODEC = RecordCodecBuilder.create(instance -> instance.group(
		Codec.FLOAT.optionalFieldOf("blend_range", 0.0F).forGetter((s) -> s.blend),
		Interpolation.CODEC.fieldOf("interp").forGetter((s) -> s.interpolation),
		Noise.HOLDER_HELPER_CODEC.fieldOf("control").forGetter((s) -> s.control),
		Noise.HOLDER_HELPER_CODEC.listOf().fieldOf("modules").forGetter((s) -> s.modules)
	).apply(instance, MultiBlend::new));
	
    private final Node[] nodes;
    private final int maxIndex;
    private final float blend;
    private final float blendRange;

    public MultiBlend(float blend, Interpolation interpolation, Noise control, List<Noise> sources) {
        super(control, sources, interpolation);

		int size = sources.size();
        float spacing = 1.0F / size;
        float radius = spacing / 2.0F;
        float blendRange = radius * blend;
        float cellRadius = (radius - blendRange) / 2;

        this.blend = blend;
        this.nodes = new Node[size];
        this.maxIndex = size - 1;
        this.blendRange = blendRange;

        for (int i = 0; i < size; i++) {
            float pos = i * spacing + radius;
            float min = i == 0 ? 0.0F : pos - cellRadius;
            float max = i == this.maxIndex ? 1.0F : pos + cellRadius;
            this.nodes[i] = new Node(sources.get(i), min, max);
        }
    }

    @Override
    public float selectValue(float x, float y, float selector, int seed) {
        int index = NoiseUtil.round(selector * this.maxIndex);

        Node min = this.nodes[index];
        Node max = min;

        if (this.blendRange == 0) {
            return min.source.compute(x, y, seed);
        }

        if (selector > min.max) {
            max = this.nodes[index + 1];
        } else if (selector < min.min) {
            min = this.nodes[index - 1];
        } else {
            return min.source.compute(x ,y, seed);
        }

        float alpha = (selector - min.max) / this.blendRange;
        alpha = NoiseUtil.clamp(alpha, 0.0F, 1.0F);

        return this.blendValues(min.source.compute(x, y, seed), max.source.compute(x, y, seed), alpha);
    }

    @Override
	public Codec<MultiBlend> codec() {
		return CODEC;
	}

	@Override
	public Noise mapAll(Visitor visitor) {
		return visitor.apply(new MultiBlend(this.blend, this.interpolation, this.control.mapAll(visitor), this.modules.stream().map((noise) -> noise.mapAll(visitor)).toList()));
	}
    
    private record Node(Noise source, float min, float max) {
        
        public Node {
            min = Math.max(0.0F, min);
            max = Math.min(1.0F, max);
        }
    }
}
