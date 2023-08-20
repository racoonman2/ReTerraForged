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
public enum EdgeFunc implements StringRepresentable {
    DISTANCE_2("distance_2") {
        @Override
        public float apply(float distance, float distance2) {
            return distance2 - 1;
        }

        @Override
        public float max() {
            return 1;
        }

        @Override
        public float min() {
            return -1;
        }

        @Override
        public float range() {
            return 2;
        }
    },
    DISTANCE_2_ADD("distance_2_add") {
        @Override
        public float apply(float distance, float distance2) {
            return distance2 + distance - 1;
        }

        @Override
        public float max() {
            return 1.6F;
        }

        @Override
        public float min() {
            return -1;
        }

        @Override
        public float range() {
            return 2.6F;
        }
    },
    DISTANCE_2_SUB("distance_2_sub") {
        @Override
        public float apply(float distance, float distance2) {
            return distance2 - distance - 1;
        }

        @Override
        public float max() {
            return 0.8F;
        }

        @Override
        public float min() {
            return -1;
        }

        @Override
        public float range() {
            return 1.8F;
        }
    },
    DISTANCE_2_MUL("distance_2_mul") {
        @Override
        public float apply(float distance, float distance2) {
            return distance2 * distance - 1;
        }

        @Override
        public float max() {
            return 0.7F;
        }

        @Override
        public float min() {
            return -1F;
        }

        @Override
        public float range() {
            return 1.7F;
        }
    },
    DISTANCE_2_DIV("distance_2_div") {
        @Override
        public float apply(float distance, float distance2) {
            return distance / distance2 - 1;
        }

        @Override
        public float max() {
            return 0;
        }

        @Override
        public float min() {
            return -1;
        }

        @Override
        public float range() {
            return 1;
        }
    };
	
	public static final Codec<EdgeFunc> CODEC = StringRepresentable.fromEnum(EdgeFunc::values);
	private String name;
	
	private EdgeFunc(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
    public abstract float apply(float distance, float distance2);

    public abstract float max();

    public abstract float min();

    public abstract float range();
}
