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

public class SpiralIterator {
    private final int x;
    private final int z;
    private final int minRadius;
    private final int maxRadius;

    private int i = -1;
    private int radius;
    private int length;
    private int maxIndex;

    public SpiralIterator(int x, int z, int radius, int maxRadius) {
        this.x = x;
        this.z = z;
        this.minRadius = radius;
        this.maxRadius = maxRadius;
        this.setRadius(radius);
    }

    public void reset() {
    	this.i = -1;
        this.setRadius(this.minRadius);
    }

    public boolean hasNext() {
        return this.i + 1 < this.maxIndex || this.radius < this.maxRadius;
    }

    public long next() {
    	this.nextIndex();

        int edge = this.i / this.length;
        int step = this.i % this.length;
        int dx = -this.radius, dz = -this.radius;

        switch (edge) {
            case 0 -> dx = -this.radius + step;
            case 1 -> {
                dx = this.radius;
                dz = -this.radius + step;
            }
            case 2 -> {
                dx = this.radius - step;
                dz = this.radius;
            }
            case 3 -> dz = this.radius - step;
        }

        return PosUtil.pack(x + dx, z + dz);
    }

    public PositionFinder finder(Object2Long<SpiralIterator> function) {
        return new PositionFinder(function);
    }

    private void nextIndex() {
        if (this.radius > this.maxRadius) return;

        if (++this.i < this.maxIndex) return;

        this.setRadius(this.radius + 1);
        this.i = 0;
    }

    private void setRadius(int radius) {
        int diameter = 1 + radius * 2;
        this.radius = radius;
        this.length = diameter - 1;
        this.maxIndex = (diameter - 1) * 4;
    }

    public class PositionFinder {
        private final Object2Long<SpiralIterator> function;

        public PositionFinder(Object2Long<SpiralIterator> function) {
            this.function = function;
        }

        public boolean hasNext() {
            return SpiralIterator.this.hasNext();
        }

        public long next() {
            if (!this.hasNext()) return 0L;
            return this.function.apply(SpiralIterator.this);
        }
    }

    public interface Object2Long<T> {
        long apply(T t);
    }
}
