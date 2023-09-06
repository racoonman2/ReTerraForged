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

package raccoonman.reterraforged.common.level.levelgen.noise.curve;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;
import raccoonman.reterraforged.common.level.levelgen.noise.NoiseUtil;

/**
 * https://github.com/Auburns/FastNoise_Java
 */
public enum Interpolation implements CurveFunction, StringRepresentable {
    LINEAR("linear") {
    	
        @Override
        public float apply(float f) {
            return f;
        }
    },
    CURVE3("curve3") {
    	
        @Override
        public float apply(float f) {
            return NoiseUtil.interpHermite(f);
        }
    },
    CURVE4("curve4") {
    	
        @Override
        public float apply(float f) {
            return NoiseUtil.interpQuintic(f);
        }
    };
	
	public static final Codec<Interpolation> CODEC = StringRepresentable.fromEnum(Interpolation::values);
	private String name;
	
	private Interpolation(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	@Override
	public Codec<Interpolation> codec() {
		return CODEC;
	}
}
