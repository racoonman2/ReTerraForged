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

package raccoonman.reterraforged.common.noise.func;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

/**
 * https://github.com/Auburns/FastNoise_Java
 */
public enum DistanceFunc implements StringRepresentable {
    EUCLIDEAN("euclidean") {
        @Override
        public float apply(float vecX, float vecY) {
            return vecX * vecX + vecY * vecY;
        }
    },
    MANHATTAN("manhattan") {
        @Override
        public float apply(float vecX, float vecY) {
            return Math.abs(vecX) + Math.abs(vecY);
        }
    },
    NATURAL("natural") {
        @Override
        public float apply(float vecX, float vecY) {
            return (Math.abs(vecX) + Math.abs(vecY)) + (vecX * vecX + vecY * vecY);
        }
    };
	public static final Codec<DistanceFunc> CODEC = StringRepresentable.fromEnum(DistanceFunc::values);
	private String name;
	
	private DistanceFunc(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
    public abstract float apply(float vecX, float vecY);
}
