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

package raccoonman.reterraforged.common.util;

import raccoonman.reterraforged.common.noise.util.NoiseUtil;

public class MathUtil {
    public static final float EPSILON = 0.99999F;

    public static int clamp(int value, int min, int max) {
        return value < min ? min : value > max ? max : value;
    }

    public static float clamp(float value, float min, float max) {
        return value < min ? min : value > max ? max : value;
    }

	public static float step(float value, int steps) {
        return ((float) NoiseUtil.round(value * steps)) / steps;
    }

    public static float map(float value, float min0, float max0, float min1, float max1) {
        float alpha = NoiseUtil.map(value, min0, max0, max0 - min0);

        return NoiseUtil.lerp(min1, max1, alpha);
    }

    public static int hash(int seed, int x) {
        return NoiseUtil.hash(seed, x);
    }

    public static int hash(int seed, int x, int z) {
        return NoiseUtil.hash2D(seed, x, z);
    }

    public static float getPosX(int hash, int cx, float jitter) {
        float offset = rand(hash, NoiseUtil.X_PRIME);
        return cx + offset * jitter;
    }

    public static float getPosY(int hash, int cy, float jitter) {
        float offset = rand(hash, NoiseUtil.Y_PRIME);
        return cy + offset * jitter;
    }

    public static float getPosX(int hash, int cx, float ox, float jitter) {
        float offset = rand(hash, NoiseUtil.X_PRIME);
        return cx + ox + offset * jitter;
    }

    public static float getPosY(int hash, int cy, float oy, float jitter) {
        float offset = rand(hash, NoiseUtil.Y_PRIME);
        return cy + oy + offset * jitter;
    }

    public static float randX(int hash) {
        return rand(hash, NoiseUtil.X_PRIME);
    }

    public static float randZ(int hash) {
        return rand(hash, NoiseUtil.Y_PRIME);
    }

    public static float rand(int seed, int offset) {
        return rand(hash(seed, offset));
    }

    public static float rand(int seed, int x, int y) {
        return rand(hash(seed, x, y));
    }

    public static float rand(int n) {
        n ^= 1619;
        n ^= 31337;
        float value = (n * n * n * '\uec4d') / 2.14748365E9F;
        return NoiseUtil.map(value, -1, 1, 2);
    }

    public static float sum(float... values) {
        float sum = 0.0F;
        for (float value : values) {
            sum += value;
        }
        return sum;
    }
}
